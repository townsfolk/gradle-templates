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

   static String findMainGroovyDir(Project project) {
      File rootDir = project.projectDir
      def mainSrcDir = project.sourceSets?.main?.groovy?.srcDirs*.path
      mainSrcDir = mainSrcDir?.first()
      mainSrcDir = mainSrcDir?.minus(rootDir.path)
      return mainSrcDir
   }

   void apply(Project project) {
      project.task("createGroovyClass", group: TemplatesPlugin.group, description: "Creates a new Groovy class in the current project.") << {
         
         def mainSrcDir = null
         try {
            // get main groovy dir, and check to see if Groovy plugin is installed.
            mainSrcDir = findMainGroovyDir(project)
         } catch (Exception e) {
            throw new IllegalStateException("It seems that the Groovy plugin is not installed, I cannot determine the main groovy source directory.", e)
         }

         def fullClassName = TemplatesPlugin.prompt("Class name (com.example.MyClass)")
         if (fullClassName) {
            def classParts = fullClassName.split("\\.") as List
            def className = classParts.pop()
            def classPackagePath = classParts.join(File.separator)
            def classPackage = classParts.join('.')
            ProjectTemplate.fromUserDir {
               "${mainSrcDir}" {
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
      project.task("createGroovyProject", group: TemplatesPlugin.group, description: "Creates a new Gradle Groovy project in a new directory named after your project.") << {
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
      project.task("initGroovyProject", group: TemplatesPlugin.group, description: "Initializes a new Gradle Groovy project in the current directory.") << {
         createBase()
         TemplatesPlugin.prependPlugin "groovy", new File("build.gradle")
      }

   }
}