package templates

import org.gradle.api.Plugin
import org.gradle.api.Project

class ScalaTemplatesPlugin implements Plugin<Project> {

	static String findMainScalaDir(Project project) {
		File rootDir = project.projectDir
		def mainSrcDir = project.sourceSets?.main?.scala?.srcDirs*.path
		mainSrcDir = mainSrcDir?.first()
		mainSrcDir = mainSrcDir?.minus(rootDir.path)
		return mainSrcDir
	}

	void createBase(String path = System.getProperty("user.dir")) {
		ProjectTemplate.fromRoot(path) {
			"src" {
				"main" {
					"scala" {}
					"resources" {}
				}
				"test" {
					"scala" {}
					"resources" {}
				}
			}
			"LICENSE.txt" "// Your License Goes here"
		}
	}

	void setupBuildFile(String path = System.getProperty("user.dir")) {
		String scalaVersion = TemplatesPlugin.prompt("Scala Version:", "2.9.0")
		boolean useFastCompiler = TemplatesPlugin.promptYesOrNo("Use fast compiler?", false)
		ProjectTemplate.fromRoot(path) {
			"build.gradle" template: "/templates/scala/build.gradle", append: true,
					scalaVersion: scalaVersion,
					useFastCompiler: useFastCompiler
		}
	}

	void createScalaClass(Project project, boolean object) {
		def mainSrcDir = null
		try {
			// get main Scala dir, and check to see if Scala plugin is installed.
			mainSrcDir = findMainScalaDir(project)
		} catch (Exception e) {
			throw new IllegalStateException("It seems that the Scala plugin is not installed, I cannot determine the main scala source directory.", e)
		}
		def type = object ? "Object" : "Class"
		def fullClassName = TemplatesPlugin.prompt("${type} name (com.example.My${type})")
		if (fullClassName) {
			def classParts = JavaTemplatesPlugin.getClassParts(fullClassName)
			ProjectTemplate.fromUserDir {
				"${mainSrcDir}" {
					"${classParts.classPackagePath}" {
						"${classParts.className}.scala" template: "/templates/scala/scala-class.scala",
								className: classParts.className,
								classPackage: classParts.classPackage,
								object: object
					}
				}
			}
		} else {
			println "No class name provided."
		}
	}

	void apply(Project project) {
		project.task("createScalaClass", group: TemplatesPlugin.group,
				description: "Creates a new Scala class in the current project.") << {
			createScalaClass(project, false)
		}
		project.task("createScalaObject", group: TemplatesPlugin.group,
				description: "Creates a new Scala object in the current project.") << {
			createScalaClass(project, true)
		}
		project.task("createScalaProject", group: TemplatesPlugin.group,
				description: "Creates a new Gradle Scala project in a new directory named after your project.") << {
			def projectName = TemplatesPlugin.prompt("Project Name:")
			if (projectName) {
				createBase(projectName)
				setupBuildFile(projectName)
			} else {
				println "No project name provided."
			}
		}
		project.task("initScalaProject", group: TemplatesPlugin.group,
				description: "Initializes a new Gradle Scala project in the current directory.") << {
			createBase()
			setupBuildFile()
		}
	}
}