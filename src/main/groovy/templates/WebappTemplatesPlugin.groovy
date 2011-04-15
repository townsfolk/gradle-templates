package templates

import org.gradle.api.Project
import org.gradle.api.Plugin

class WebappTemplatesPlugin extends JavaTemplatesPlugin implements Plugin<Project> {
   void createBase(String path = System.getProperty("user.dir"), String projectName) {
      super.createBase(path)
      ProjectTemplate.root(path) {
         "src/main/webapp/WEB-INF" {
            "web.xml" template: "/templates/webapp/web-xml.tmpl", project: [name: projectName]
         }
      }
   }

   void apply(Project project) {
      project.apply(plugin: "java-templates")
      
      project.task("create-webapp-project", group: TemplatesPlugin.group, description: "Creates a new Gradle Webapp project in a new directory named after your project.") << {
         def projectName = TemplatesPlugin.prompt("Project Name:")
         if (projectName) {
            createBase(projectName, projectName)
            ProjectTemplate.root(projectName) {
               "build.gradle" "apply plugin: 'war'"
            }
         } else {
            println "No project name provided."
         }
      }
      project.task("init-webapp-project", group: TemplatesPlugin.group, description: "Initializes a new Gradle Webapp project in the current directory.") << {
         createBase(project.name)
         TemplatesPlugin.prependPlugin "war", new File("build.gradle")
      }

   }
}