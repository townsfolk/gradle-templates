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

package templates.tasks.gradle

import org.gradle.api.tasks.TaskAction
import templates.TemplatesPlugin

/**
 * Task to create a gradle project in a new directory.
 */
class CreateGradlePluginTask extends AbstractGradleProjectTask {

    protected static final String PROJECT_PARENT_DIR = 'projectParentDir'

    CreateGradlePluginTask(){
        name = 'createGradlePlugin'
        group = TemplatesPlugin.group
        description = 'Creates a new Gradle Plugin project in a new directory named after your project.'
    }

    @TaskAction def create(){
        def props = project.properties
        def projectName = props[NEW_PROJECT_NAME] ?: TemplatesPlugin.prompt('Project Name:')
        if (projectName) {
            String projectPath = props[PROJECT_PARENT_DIR] ? "${props[PROJECT_PARENT_DIR]}/$projectName" : projectName

            createBase(projectPath, [name: projectName, properties: project.properties])

        } else {
            // FIXME: error
            println 'No project name provided.'
        }
    }
}
