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
import templates.tasks.webapp.CreateWebappProjectTask
import templates.tasks.webapp.ExportWebappTemplatesTask
import templates.tasks.webapp.InitWebappProjectTask

/**
 * Adds basic tasks for bootstrapping Webapp projects. Adds createWebappProject, exportWebappTemplates, and
 * initWebappProject tasks. Also applies the java-templates plugin.
 */
class WebappTemplatesPlugin extends JavaTemplatesPlugin implements Plugin<Project> {

	void apply(Project project) {
		// Check to make sure JavaTemplatesPlugin isn't already added.
		if (!project.plugins.findPlugin(JavaTemplatesPlugin)) {
			project.apply(plugin: JavaTemplatesPlugin)
		}

		project.task 'createWebappProject', type:CreateWebappProjectTask

		project.task 'exportWebappTemplates', type:ExportWebappTemplatesTask

		project.task 'initWebappProject', type:InitWebappProjectTask
	}
}