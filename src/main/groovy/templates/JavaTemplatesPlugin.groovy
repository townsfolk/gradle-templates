package templates

import org.gradle.api.Project
import org.gradle.api.Plugin

class JavaTemplatesPlugin implements Plugin<Project> {
   void createBase(String path = System.getProperty("user.dir")) {
      ProjectTemplate.root(path) {
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
      project.task("init-java-project") << {
         createBase()
         prependPlugin "java", new File("build.gradle")
      }
      project.task("create-java-project") << {
         def projectName = TemplatesPlugin.prompt("Project Name")
         createBase(projectName)
         ProjectTemplate.root(projectName) {
            "build.gradle" "apply plugin: 'java'"
         }
      }
   }
}