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

import org.gradle.api.tasks.TaskAction

/**
 * Created by cjstehno on 12/22/13.
 */
class CreateScalaProjectTask extends AbstractScalaProjectTask {

    CreateScalaProjectTask(){
        name = 'createScalaProject'
        group = TemplatesPlugin.group
        description = 'Creates a new Gradle Scala project in a new directory named after your project.'
    }

    @TaskAction
    def create(){
        def props = project.properties

        String projectName = props[NEW_PROJECT_NAME] ?: TemplatesPlugin.prompt('Project Name:')

        if (projectName) {
            String projectGroup = props[PROJECT_GROUP] ?: TemplatesPlugin.prompt('Group:', projectName.toLowerCase())
            String projectVersion = props[PROJECT_VERSION] ?: TemplatesPlugin.prompt('Version:', '1.0')

            project.setProperty PROJECT_GROUP, projectGroup
            project.setProperty PROJECT_VERSION, projectVersion

            String projectPath = props[PROJECT_PARENT_DIR] ? "${props[PROJECT_PARENT_DIR]}/$projectName" : projectName

            createBase projectPath
            setupBuildFile project, projectPath

        } else {
            // TODO: this should probably be an error or something more useful.
            println 'No project name provided.'
        }
    }
}
