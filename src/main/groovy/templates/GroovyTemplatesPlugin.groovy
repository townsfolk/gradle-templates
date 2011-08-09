package templates

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Adds basic tasks for bootstrapping Groovy projects. Adds createGroovyClass, createGroovyProject,
 * exportGroovyTemplates, and initGroovyProject tasks.
 */
class GroovyTemplatesPlugin implements Plugin<Project> {
	/**
	 * Creates the basic Groovy project directory structure.
	 * @param path the root of the project. Optional,defaults to user.dir.
	 */
	void createBase(String path = System.getProperty('user.dir')) {
		ProjectTemplate.fromRoot(path) {
			'src' {
				'main' {
					'groovy' {}
					'resources' {}
				}
				'test' {
					"groovy" {}
					"resources" {}
				}
			}
			'LICENSE.txt' '// Your License Goes here'
		}
	}
	/**
	 * Finds the path to the main java source directory.
	 * @param project The project.
	 * @return The path to the main groovy source directory.
	 */
	static String findMainGroovyDir(Project project) {
		File rootDir = project.projectDir
		def mainSrcDir = project.sourceSets?.main?.groovy?.srcDirs*.path
		mainSrcDir = mainSrcDir?.first()
		mainSrcDir = mainSrcDir?.minus(rootDir.path)
		return mainSrcDir
	}

	void apply(Project project) {
		def props = project.properties

		project.task('createGroovyClass', group: TemplatesPlugin.group, description: 'Creates a new Groovy class in the current project.') << {

			def mainSrcDir = null
			try {
				// get main groovy dir, and check to see if Groovy plugin is installed.
				mainSrcDir = findMainGroovyDir(project)
			} catch (Exception e) {
				throw new IllegalStateException('It seems that the Groovy plugin is not installed, I cannot determine the main groovy source directory.', e)
			}

			def fullClassName = props['newClassName'] ?: TemplatesPlugin.prompt('Class name (com.example.MyClass)')

			if (fullClassName) {
				def classParts = JavaTemplatesPlugin.getClassParts(fullClassName)
				ProjectTemplate.fromUserDir {
					"${mainSrcDir}" {
						"${classParts.classPackagePath}" {
							"${classParts.className}.groovy" template: '/templates/groovy/groovy-class.tmpl',
									className: classParts.className,
									classPackage: classParts.classPackage
						}
					}
				}
			} else {
				println 'No class name provided.'
			}
		}
		project.task('createGroovyProject', group: TemplatesPlugin.group,
				description: 'Creates a new Gradle Groovy project in a new directory named after your project.') << {

			def projectName = props['newProjectName'] ?: TemplatesPlugin.prompt('Project Name:')
			if (projectName) {
				String projectGroup = props['projectGroup'] ?: TemplatesPlugin.prompt('Group:', projectName.toLowerCase())
				String projectVersion = props['projectVersion'] ?: TemplatesPlugin.prompt('Version:', '1.0')
				createBase(projectName)
				ProjectTemplate.fromRoot(projectName) {
					'build.gradle' template: '/templates/groovy/build.gradle.tmpl', projectGroup: projectGroup
					'gradle.properties' content: "version=$projectVersion", append: true
				}
			} else {
				println 'No project name provided.'
			}
		}
		project.task('exportGroovyTemplates', group: TemplatesPlugin.group,
				description: 'Exports the default groovy template files into the current directory.') << {
			def _ = '/templates/groovy'
			def templates = [
					"$_/build.gradle.tmpl",
					"$_/groovy-class.tmpl"
			]
			TemplatesPlugin.exportTemplates(templates)
		}
		project.task('initGroovyProject', group: TemplatesPlugin.group,
				description: 'Initializes a new Gradle Groovy project in the current directory.') << {
			createBase()
			def buildFile = new File('build.gradle')
			buildFile.exists() ?: buildFile.createNewFile()
			TemplatesPlugin.prependPlugin 'groovy', buildFile
		}
	}
}