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

package templates.tasks.scala
import org.gradle.api.tasks.TaskAction

/**
 * Task for creating a new Gradle Scala project in a specified directory.
 */
class CreateScalaProjectTask extends AbstractScalaProjectTask {

    CreateScalaProjectTask(){
        super(
            'createScalaProject',
            'Creates a new Gradle Scala project in a new directory named after your project.'
        )
    }

    @TaskAction void create(){
        String projectName = projectName()
        if (projectName) {
            project.ext[PROJECT_GROUP] = projectGroup( projectName )
            project.ext[PROJECT_VERSION] = projectVersion()

            String projectPath = projectPath( projectName )

            createBase projectPath
            setupBuildFile project, projectPath

        } else {
            // FIXME: error
            println 'No project name provided.'
        }
    }
}
