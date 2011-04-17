package templates

import org.gradle.api.Project
import org.gradle.api.Plugin

class GroovyTemplatesPlugin implements Plugin<Project> {
   void createBase(String path = System.getProperty("user.dir")) {
      ProjectTemplate.fromRoot(path) {
         "src" {
            "main" {
               "groovy" {}
               "resources" {}
            }
            "test" {
               "groovy" {}
               "resources" {}
            }
         }
         "LICENSE.txt" "// Your License Goes here"
      }
   }

   void apply(Project project) {
      project.task("create-groovy-class", group: TemplatesPlugin.group, description: "Creates a new Groovy class in the current project.") << {
         def fullClassName = TemplatesPlugin.prompt("Class name (com.example.MyClass)")
         if (fullClassName) {
            def classParts = fullClassName.split("\\.") as List
            def className = classParts.pop()
            def classPackagePath = classParts.join(File.separator)
            def classPackage = classParts.join('.')
            ProjectTemplate.fromUserDir {
               "src/main/groovy" {
                  "${classPackagePath}" {
                     "${className}.groovy" template: "/templates/groovy/groovy-class.tmpl",
                           className: className,
                           classPackage: classPackage
                  }
               }
            }
         } else {
            println "No class name provided."
         }
      }
      project.task("create-groovy-project", group: TemplatesPlugin.group, description: "Creates a new Gradle Groovy project in a new directory named after your project.") << {
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
      project.task("init-groovy-project", group: TemplatesPlugin.group, description: "Initializes a new Gradle Groovy project in the current directory.") << {
         createBase()
         TemplatesPlugin.prependPlugin "groovy", new File("build.gradle")
      }

   }
}