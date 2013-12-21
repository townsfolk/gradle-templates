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

import org.gradle.api.Project

/**
 * Adds basic tasks for bootstrapping gradle plugin projects. Adds createGradlePlugin, exportPluginTemplates, and
 * initGradlePlugin tasks. Also applies the groovy-templates plugin.
 */
class GradlePluginTemplatesPlugin extends GroovyTemplatesPlugin {

	/**
	 * Creates the default project structure for a new gradle plugin.
	 * @param path The root path of the project. optional, defaults to user.dir.
	 * @param project A project object.
	 */
	void createBase(String path = System.getProperty('user.dir'), def project) {

		def props = project.properties
		String lProjectName = project.name.toLowerCase()
		String cProjectName = project.name.capitalize()
		String projectGroup = props['projectGroup'] ?: TemplatesPlugin.prompt('Group:', lProjectName)
		String projectVersion = props['projectVersion'] ?: TemplatesPlugin.prompt('Version:', '1.0')
		String pluginApplyLabel = props['pluginApplyLabel'] ?: TemplatesPlugin.prompt('Plugin \'apply\' label:', lProjectName)
		String pluginClassName = props['pluginClassName'] ?: TemplatesPlugin.prompt('Plugin class name:', "${projectGroup}.${cProjectName}Plugin")

		super.createBase(path)

		ProjectTemplate.fromRoot(path) {
			'src/main/' {
				'resources/META-INF/gradle-plugins' {
					"${pluginApplyLabel}.properties" "implementation-class=${pluginClassName}"
				}
				'groovy' {
					if (pluginClassName) {
						def classParts = JavaTemplatesPlugin.getClassParts(pluginClassName)
						"${classParts.classPackagePath}" {
							"${classParts.className}.groovy" template: '/templates/plugin/plugin-class.tmpl',
									className: classParts.className,
									classPackage: classParts.classPackage
							"${classParts.className}Convention.groovy" template: '/templates/plugin/convention-class.tmpl',
									className: classParts.className,
									classPackage: classParts.classPackage
						}
					}
				}
			}
			'build.gradle' template: '/templates/plugin/build.gradle.tmpl', projectGroup: projectGroup
			'gradle.properties' content: "version=${projectVersion}", append: true
		}
	}

	void apply(Project project) {
		// Check to make sure GroovyTemplatesPlugin isn't already added.
		if (!project.plugins.findPlugin(GroovyTemplatesPlugin)) {
			project.apply(plugin: GroovyTemplatesPlugin)
		}
		def props = project.properties

		project.task('createGradlePlugin', group: TemplatesPlugin.group,
				description: 'Creates a new Gradle Plugin project in a new directory named after your project.') << {
			def projectName = props['newProjectName'] ?: TemplatesPlugin.prompt('Project Name:')
			if (projectName) {
				createBase(projectName, [name: projectName, properties: project.properties])
			} else {
				println 'No project name provided.'
			}
		}

		project.task('exportPluginTemplates', group: TemplatesPlugin.group,
				description: 'Exports the default plugin template files into the current directory.') << {
			def _ = '/templates/plugin'
			def templates = [
					"$_/build.gradle.tmpl",
					"$_/convention-class.tmpl",
					"$_/plugin-class.tmpl"
			]
			TemplatesPlugin.exportTemplates(templates)
		}

		project.task('initGradlePlugin', group: TemplatesPlugin.group, dependsOn: ['initGroovyProject'],
				description: 'Initializes a new Gradle Plugin project in the current directory.') << {
			createBase(project)
		}

	}
}