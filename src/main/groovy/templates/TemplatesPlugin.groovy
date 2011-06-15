package templates

import org.gradle.api.Plugin
import org.gradle.api.Project

class TemplatesPlugin implements Plugin<Project> {

   static final String group = "Template"
   static final String lineSep = System.getProperty("line.separator")
   static final String inputPrompt = "${lineSep}??>"

   static void prependPlugin(String plugin, File gradleFile) {
      def oldText = gradleFile.text
      gradleFile.text = ""
      gradleFile.withPrintWriter { pw ->
         pw.println "apply plugin: '${plugin}'"
         pw.print oldText
      }
   }

   static String prompt(String message, String defaultValue = null) {
      if (defaultValue) {
         return System.console().readLine("${inputPrompt} ${message} [${defaultValue}] ") ?: defaultValue
      }
      return System.console().readLine("${inputPrompt} ${message} ") ?: defaultValue
   }

   static int promptOptions(String message, List options = []) {
      promptOptions(message, "", options)
   }

   static int promptOptions(String message, int defaultValue, List options = []) {
      String consoleMessage = "${inputPrompt} ${message}"
      consoleMessage += "${lineSep}    Pick an option ${1..options.size()}"
      options.eachWithIndex { option, index ->
         consoleMessage += "${lineSep}     (${index + 1}): ${option}"
      }
      if (defaultValue) {
         consoleMessage += "${inputPrompt} [${defaultValue}] "
      } else {
         consoleMessage += "${inputPrompt} "
      }
      try {
         def range = 0..options.size() - 1
         int choice = Integer.parseInt(System.console().readLine(consoleMessage) ?: "${defaultValue}") - 1
         if(range.containsWithinBounds(choice)) {
            return choice
         } else {
            throw new IllegalArgumentException("Option is not valid.")
         }
      } catch (Exception e) {
         throw new IllegalArgumentException("Option is not valid.", e)
      }
   }

   static boolean promptYesOrNo(String message, boolean defaultValue = false) {
      def defaultStr = defaultValue ? "Y" : "n"
      String consoleVal = System.console().readLine("${inputPrompt} ${message} (Y|n)[${defaultStr}] ")
      if (consoleVal) {
         return consoleVal.toLowerCase().startsWith("y")
      }
      return defaultValue
   }

   def void apply(Project project) {
      project.convention.plugins.templatePlugin = new TemplatesPluginConvention()
      project.apply(plugin: 'gradle-plugin-templates')
      project.apply(plugin: 'groovy-templates')
      project.apply(plugin: 'java-templates')
      project.apply(plugin: 'scala-templates')
      project.apply(plugin: 'webapp-templates')
   }
}