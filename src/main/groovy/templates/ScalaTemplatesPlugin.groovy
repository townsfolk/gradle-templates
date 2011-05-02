package templates

import org.gradle.api.Project
import org.gradle.api.Plugin

class ScalaTemplatesPlugin implements Plugin<Project> {
   
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

   void apply(Project project) {
      project.task("createScalaClass", group: TemplatesPlugin.group, description: "Creates a new Scala class in the current project.") << {

         def mainSrcDir = null
         try {
            // get main groovy dir, and check to see if Groovy plugin is installed.
            mainSrcDir = findMainGroovyDir(project)
         } catch (Exception e) {
            throw new IllegalStateException("It seems that the Scala plugin is not installed, I cannot determine the main scala source directory.", e)
         }

         def fullClassName = TemplatesPlugin.prompt("Class name (com.example.MyClass)")
         if (fullClassName) {
            def classParts = JavaTemplatesPlugin.getClassParts(fullClassName)
            ProjectTemplate.fromUserDir {
               "${mainSrcDir}" {
                  "${classParts.classPackagePath}" {
                     "${classParts.className}.groovy" template: "/templates/groovy/groovy-class.tmpl",
                           className: classParts.className,
                           classPackage: classParts.classPackage
                  }
               }
            }
         } else {
            println "No class name provided."
         }
      }
      project.task("createScalaProject", group: TemplatesPlugin.group, description: "Creates a new Gradle Scala project in a new directory named after your project.") << {
         def projectName = TemplatesPlugin.prompt("Project Name:")
         if (projectName) {
            createBase(projectName)
            ProjectTemplate.fromRoot(projectName) {
               "build.gradle" template: "/templates/groovy/build.gradle.tmpl"
            }
         } else {
            println "No project name provided."
         }
      }
      project.task("initScalaProject", group: TemplatesPlugin.group, description: "Initializes a new Gradle Scala project in the current directory.") << {
         createBase()
         TemplatesPlugin.prependPlugin "groovy", new File("build.gradle")
      }
   }
}