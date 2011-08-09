package templates

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Adds basic tasks for bootstrapping Scala projects. Adds createScalaClass, createScalaObject, createScalaProject,
 * exportScalaTemplates, and initScalaProject tasks.
 */
class ScalaTemplatesPlugin implements Plugin<Project> {

	/**
	 * Finds the path to the main java source directory.
	 * @param project The project.
	 * @return The path to the main groovy source directory.
	 */
	static String findMainScalaDir(Project project) {
		File rootDir = project.projectDir
		def mainSrcDir = project.sourceSets?.main?.scala?.srcDirs*.path
		mainSrcDir = mainSrcDir?.first()
		mainSrcDir = mainSrcDir?.minus(rootDir.path)
		return mainSrcDir
	}

	/**
	 * Creates the basic Scala project directory structure.
	 * @param path the root of the project. Optional,defaults to user.dir.
	 */
	void createBase(String path = System.getProperty('user.dir')) {
		ProjectTemplate.fromRoot(path) {
			'src' {
				'main' {
					'scala' {}
					'resources' {}
				}
				'test' {
					'scala' {}
					'resources' {}
				}
			}
			'LICENSE.txt' '// Your License Goes here'
		}
	}

	/**
	 * Initializes or creates the project's build.gradle file.
	 * @param project The project
	 * @param path The path to the root of the project. optional, defaults to user.dir.
	 */
	void setupBuildFile(Project project, String path = System.getProperty('user.dir')) {
		def props = project.properties
		String scalaVersion = props['scalaVersion'] ?: TemplatesPlugin.prompt('Scala Version:', '2.9.0')
		boolean useFastCompiler = props['useFastScalaCompiler'] ?: TemplatesPlugin.promptYesOrNo('Use fast compiler?', false)
		ProjectTemplate.fromRoot(path) {
			'build.gradle' template: '/templates/scala/build.gradle.tmpl', append: true,
					scalaVersion: scalaVersion,
					useFastCompiler: useFastCompiler,
					projectGroup: project.projectGroup
			'gradle.properties' content: "version=${project.projectVersion}", append: true
		}
	}

	/**
	 * Creates a Scala class, or object in the currect working directory.
	 * @param project The project.
	 * @param object Is this a Scala class, or object.
	 */
	void createScalaClass(Project project, boolean object) {
		def mainSrcDir = null
		try {
			// get main Scala dir, and check to see if Scala plugin is installed.
			mainSrcDir = findMainScalaDir(project)
		} catch (Exception e) {
			throw new IllegalStateException('It seems that the Scala plugin is not installed, I cannot determine the main scala source directory.', e)
		}
		def props = project.properties
		def type = object ? 'Object' : 'Class'
		def fullClassName = props['newClassName'] ?: props['newObjectName'] ?: TemplatesPlugin.prompt("${type} name (com.example.My${type})")
		if (fullClassName) {
			def classParts = JavaTemplatesPlugin.getClassParts(fullClassName)
			ProjectTemplate.fromUserDir {
				"${mainSrcDir}" {
					"${classParts.classPackagePath}" {
						"${classParts.className}.scala" template: '/templates/scala/scala-class.tmpl',
								className: classParts.className,
								classPackage: classParts.classPackage,
								object: object
					}
				}
			}
		} else {
			println 'No class name provided.'
		}
	}

	void apply(Project project) {
		def props = project.properties

		project.task('createScalaClass', group: TemplatesPlugin.group,
				description: 'Creates a new Scala class in the current project.') << {
			createScalaClass(project, false)
		}
		project.task('createScalaObject', group: TemplatesPlugin.group,
				description: 'Creates a new Scala object in the current project.') << {
			createScalaClass(project, true)
		}
		project.task('createScalaProject', group: TemplatesPlugin.group,
				description: 'Creates a new Gradle Scala project in a new directory named after your project.') << {
			def projectName = props['newProjectName'] ?: TemplatesPlugin.prompt('Project Name:')
			if (projectName) {
				String projectGroup = props['projectGroup'] ?: TemplatesPlugin.prompt('Group:', projectName.toLowerCase())
				String projectVersion = props['projectVersion'] ?: TemplatesPlugin.prompt('Version:', '1.0')
				project.setProperty('projectGroup', projectGroup)
				project.setProperty('projectVersion', projectVersion)
				createBase(projectName)
				setupBuildFile(project, projectName)
			} else {
				println 'No project name provided.'
			}
		}
		project.task('exportScalaTemplates', group: TemplatesPlugin.group,
				description: 'Exports the default scala template files into the current directory.') << {
			def _ = '/templates/scala'
			def templates = [
					"$_/build.gradle.tmpl",
					"$_/scala-class.tmpl"
			]
			TemplatesPlugin.exportTemplates(templates)
		}
		project.task('initScalaProject', group: TemplatesPlugin.group,
				description: 'Initializes a new Gradle Scala project in the current directory.') << {
			createBase()
			setupBuildFile(project)
		}
	}
}