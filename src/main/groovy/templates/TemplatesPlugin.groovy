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

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * The core of the templates plugin.
 */
class TemplatesPlugin implements Plugin<Project> {

	static final String group = 'Template'

	static void prependPlugin(String plugin, File gradleFile) {
		def oldText = gradleFile.text
		gradleFile.text = ''
		gradleFile.withPrintWriter { pw ->
			pw.println "apply plugin: '${plugin}'"
			pw.print oldText
		}
	}

	def void apply(Project project) {
		project.convention.plugins.templatePlugin = new TemplatesPluginConvention()

		// FIXME: would be better to allow user to configure the desired template set rather than get them all
		project.apply(plugin: GroovyTemplatesPlugin)
		project.apply(plugin: GradlePluginTemplatesPlugin)
		project.apply(plugin: JavaTemplatesPlugin)
	}
}
