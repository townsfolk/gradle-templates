package templates

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class TemplatesPlugin implements Plugin<Project> {

	static final String group = "Template"
	static final String lineSep = System.getProperty("line.separator")
	static final String inputPrompt = "${lineSep}??>"

	static void exportTemplates(def templates = []) {
		ProjectTemplate.fromUserDir {
			templates.each { template ->
				def tStream = getClass().getResourceAsStream(template)
				"$template" tStream.text
			}
		}
	}

	static void prependPlugin(String plugin, File gradleFile) {
		def oldText = gradleFile.text
		gradleFile.text = ''
		gradleFile.withPrintWriter { pw ->
			pw.println "apply plugin: '${plugin}'"
			pw.print oldText
		}
	}

	static String prompt(String message, String defaultValue = null) {
		readLine(message, defaultValue)
	}

	static int promptOptions(String message, List options = []) {
		promptOptions(message, 0, options)
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
			int choice = Integer.parseInt(readLine(consoleMessage, defaultValue))
			if (choice == 0) {
				throw new GradleException('No option provided')
			}
			choice--
			if (range.containsWithinBounds(choice)) {
				return choice
			} else {
				throw new IllegalArgumentException('Option is not valid.')
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException('Option is not valid.', e)
		}
	}

	static boolean promptYesOrNo(String message, boolean defaultValue = false) {
		def defaultStr = defaultValue ? 'Y' : 'n'
		String consoleVal = readLine("$message (Y|n)", defaultValue)
		if (consoleVal) {
			return consoleVal.toLowerCase().startsWith('y')
		}
		return defaultValue
	}

	private static readLine(String message, def defaultValue = null) {
		String _message = "$inputPrompt $message " + (defaultValue ? "[$defaultValue] " : "")
		if (System.console()) {
			return System.console().readLine(_message) ?: defaultValue
		}
		println "$_message (WAITING FOR INPUT BELOW)"
		return System.in.newReader().readLine() ?: defaultValue
	}

	def void apply(Project project) {
		project.convention.plugins.templatePlugin = new TemplatesPluginConvention()
		project.apply(plugin: GroovyTemplatesPlugin)
		project.apply(plugin: GradlePluginTemplatesPlugin)
		project.apply(plugin: JavaTemplatesPlugin)
		project.apply(plugin: ScalaTemplatesPlugin)
		project.apply(plugin: WebappTemplatesPlugin)

		project.task('exportAllTemplates', dependsOn: [
				'exportJavaTemplates', 'exportGroovyTemplates', 'exportScalaTemplates', 'exportWebappTemplates',
				'exportPluginTemplates'], group: TemplatesPlugin.group,
				description: 'Exports all the default template files into the current directory.') {}
	}
}