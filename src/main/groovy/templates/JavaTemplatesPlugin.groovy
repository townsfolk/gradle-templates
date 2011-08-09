package templates

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Adds basic tasks for bootstrapping Java projects. Adds createJavaClass, createJavaProject,
 * exportJavaTemplates, and initJavaProject tasks.
 */
class JavaTemplatesPlugin implements Plugin<Project> {

	/**
	 * Pulls a fully qualified classname into it's parts - package, and name.
	 * @param fullClassName
	 * @return Map containing the classname, package, and package as a path.
	 */
	static def getClassParts(String fullClassName) {
		def classParts = fullClassName.split(/\./) as List
		[
				className: classParts.pop(),
				classPackagePath: classParts.join(File.separator),
				classPackage: classParts.join('.')
		]
	}

	/**
	 * Creates the basic Java project directory structure.
	 * @param path the root of the project. Optional,defaults to user.dir.
	 */
	void createBase(String path = System.getProperty('user.dir')) {
		ProjectTemplate.fromRoot(path) {
			'src' {
				'main' {
					'java' {}
					'resources' {}
				}
				'test' {
					'java' {}
					'resources' {}
				}
			}
			'LICENSE.txt' '// Your License Goes here'
		}
	}

	/**
	 * Finds the path to the main java source directory.
	 * @param project The project.
	 * @return The path to the main java source directory.
	 */
	static String findMainJavaDir(Project project) {
		File rootDir = project.projectDir
		def mainSrcDir = project.sourceSets?.main?.java?.srcDirs*.path
		mainSrcDir = mainSrcDir?.first()
		mainSrcDir = mainSrcDir?.minus(rootDir.path)
		return mainSrcDir
	}

	void apply(Project project) {

		def props = project.properties

		project.task('createJavaClass', group: TemplatesPlugin.group, description: 'Creates a new Java class in the current project.') << {

			def mainSrcDir = null
			try {
				// get main java dir, and check to see if Java plugin is installed.
				mainSrcDir = findMainJavaDir(project)
			} catch (Exception e) {
				throw new IllegalStateException('It seems that the Java plugin is not installed, I cannot determine the main java source directory.', e)
			}

			def fullClassName = props['newClassName'] ?: TemplatesPlugin.prompt('Class name (com.example.MyClass)')
			if (fullClassName) {
				def classParts = JavaTemplatesPlugin.getClassParts(fullClassName)
				ProjectTemplate.fromUserDir {
					"${mainSrcDir}" {
						"${classParts.classPackagePath}" {
							"${classParts.className}.java" template: '/templates/java/java-class.tmpl',
									classPackage: classParts.classPackage,
									className: classParts.className
						}
					}
				}
			} else {
				println 'No class name provided.'
			}
		}
		project.task('createJavaProject', group: TemplatesPlugin.group, description: 'Creates a new Gradle Java project in a new directory named after your project.') << {
			def projectName = props['newProjectName'] ?: TemplatesPlugin.prompt('Project Name:')
			if (projectName) {
				String projectGroup = props['projectGroup'] ?: TemplatesPlugin.prompt('Group:', projectName.toLowerCase())
				String projectVersion = props['projectVersion'] ?: TemplatesPlugin.prompt('Version:', '1.0')
				createBase(projectName)
				ProjectTemplate.fromRoot(projectName) {
					'build.gradle' template: '/templates/java/build.gradle.tmpl', projectGroup: projectGroup
					'gradle.properties' content: "version=$projectVersion", append: true
				}
			} else {
				println 'No project name provided.'
			}
		}
		project.task('exportJavaTemplates', group: TemplatesPlugin.group,
				description: 'Exports the default java template files into the current directory.') << {
			def _ = '/templates/java'
			def templates = [
					"$_/build.gradle.tmpl",
					"$_/java-class.tmpl"
			]
			TemplatesPlugin.exportTemplates(templates)
		}
		project.task('initJavaProject', group: TemplatesPlugin.group, description: 'Initializes a new Gradle Java project in the current directory.') << {
			createBase()
			File buildFile = new File('build.gradle')
			buildFile.exists() ?: buildFile.createNewFile()
			TemplatesPlugin.prependPlugin 'java', buildFile
		}
	}
}