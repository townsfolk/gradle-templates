package templates

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.file.RelativePath

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

   static String findMainJavaDir(Project project) {
      File rootDir = project.projectDir
      def mainSrcDir = project.sourceSets?.main?.java?.srcDirs*.path
      mainSrcDir = mainSrcDir?.first()
      mainSrcDir = mainSrcDir?.minus(rootDir.path)
      return mainSrcDir
   }

   void apply(Project project) {
      project.task("createJavaClass", group: TemplatesPlugin.group, description: "Creates a new Java class in the current project.") << {

         def mainSrcDir = null
         try {
            // get main java dir, and check to see if Java plugin is installed.
            mainSrcDir = findMainJavaDir(project)
         } catch (Exception e) {
            throw new IllegalStateException("It seems that the Java plugin is not installed, I cannot determine the main java source directory.", e)
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
                     "${className}.java" template: "/templates/java/java-class.tmpl",
                           classPackage: classPackage,
                           className: className
                  }
               }
            }
         } else {
            println "No class name provided."
         }
      }
      project.task("createJavaProject", group: TemplatesPlugin.group, description: "Creates a new Gradle Java project in a new directory named after your project.") << {
         def projectName = TemplatesPlugin.prompt("Project Name:")
         if (projectName) {
            createBase(projectName)
            ProjectTemplate.fromRoot(projectName) {
               "build.gradle" template: "/templates/java/build.gradle.tmpl"
            }
         } else {
            println "No project name provided."
         }
      }
      project.task("initJavaProject", group: TemplatesPlugin.group, description: "Initializes a new Gradle Java project in the current directory.") << {
         createBase()
         TemplatesPlugin.prependPlugin "java", new File("build.gradle")
      }

   }
}