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
 * Task for creating a new scala object in the current project.
 */
class CreateScalaObjectTask extends CreateScalaClassTask {
    // TODO: seems like there should be a better way to do this since its just a different property

    CreateScalaObjectTask(){
        super()
        this.name = 'createScalaObject'
        this.description = 'Creates a new Scala object in the current project.'
    }

    @Override @TaskAction
    def create(){
        createScalaClass project, true
    }
}
