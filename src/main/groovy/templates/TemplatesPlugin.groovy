/*
 * Copyright (c) 2011,2012 Eric Berry <elberry@tellurianring.com>
 * Copyright (c) 2013 Christopher J. Stehno <chris@stehno.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package templates

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * The core of the templates plugin.
 */
class TemplatesPlugin implements Plugin<Project> {

	static final String group = 'Template'
	static final String lineSep = System.getProperty( 'line.separator' )
	static final String inputPrompt = "${lineSep}templates>"

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
		String consoleMessage = "${message}"
		consoleMessage += "${lineSep}    Pick an option ${1..options.size()}"
		options.eachWithIndex { option, index ->
			consoleMessage += "${lineSep}     (${index + 1}): ${option}"
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

	static boolean promptYesOrNo( final String message, final boolean defaultValue = false) {
		String consoleVal = readLine("$message (Y|n)", defaultValue ? 'Y' : 'n')
        return consoleVal?.toLowerCase()?.startsWith('y') ?: defaultValue
	}

	private static readLine(String message, def defaultValue = null) {
		String _message = "$inputPrompt $message " + (defaultValue ? "$lineSep [$defaultValue] " : "")
		if (System.console()) {
			return System.console().readLine(_message) ?: defaultValue
		}
		println "$_message (WAITING FOR INPUT BELOW)"
		return System.in.newReader().readLine() ?: defaultValue
	}

	def void apply(Project project) {
		project.convention.plugins.templatePlugin = new TemplatesPluginConvention()

        // FIXME: would be better to allow user to configure the desired template set rather than get them all
		project.apply(plugin: GroovyTemplatesPlugin)
		project.apply(plugin: GradlePluginTemplatesPlugin)
		project.apply(plugin: JavaTemplatesPlugin)
		project.apply(plugin: ScalaTemplatesPlugin)
		project.apply(plugin: WebappTemplatesPlugin)

		project.task(
            'exportAllTemplates',
            dependsOn: [
				'exportJavaTemplates', 'exportGroovyTemplates', 'exportScalaTemplates', 'exportWebappTemplates', 'exportPluginTemplates'
            ],
            group: TemplatesPlugin.group,
            description: 'Exports all the default template files into the current directory.'
        ) {}
	}
}
