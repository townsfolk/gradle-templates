package templates

import org.gradle.api.Project
import org.gradle.api.Plugin

class JavaTemplatesPlugin implements Plugin<Project> {
   void createBase(String path = System.getProperty("user.dir")) {
      ProjectTemplate.fromRoot(path) {
         "src" {
            "main" {
               "java" {}
               "resources" {}
            }
            "test" {
               "java" {}
               "resources" {}
            }
         }
         "LICENSE.txt" "// Your License Goes here"
      }
   }

   void apply(Project project) {
      project.task("create-java-class", group: TemplatesPlugin.group, description: "Creates a new Java class in the current project.") << {
         def fullClassName = TemplatesPlugin.prompt("Class name (com.example.MyClass)")
         if (fullClassName) {
            def classParts = fullClassName.split("\\.") as List
            def className = classParts.pop()
            def classPackage = classParts.join(File.separator)
            ProjectTemplate.fromUserDir {
               "src/main/java" {
                  "${classPackage}" {
                     "${className}.java" """
                     package ${classParts.join('.')};
                     
                     public class ${className} {
                        public ${className}() {
                        }
                     }
                     """
                  }
               }
            }
         } else {
            println "No class name provided."
         }
      }
      project.task("create-java-project", group: TemplatesPlugin.group, description: "Creates a new Gradle Java project in a new directory named after your project.") << {
         def projectName = TemplatesPlugin.prompt("Project Name:")
         if (projectName) {
            createBase(projectName)
            ProjectTemplate.fromRoot(projectName) {
               "build.gradle" "apply plugin: 'java'"
            }
         } else {
            println "No project name provided."
         }
      }
      project.task("init-java-project", group: TemplatesPlugin.group, description: "Initializes a new Gradle Java project in the current directory.") << {
         createBase()
         TemplatesPlugin.prependPlugin "java", new File("build.gradle")
      }

   }
}