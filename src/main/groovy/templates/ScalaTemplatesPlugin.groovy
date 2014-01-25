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
import templates.tasks.scala.CreateScalaClassTask
import templates.tasks.scala.CreateScalaObjectTask
import templates.tasks.scala.CreateScalaProjectTask
import templates.tasks.scala.ExportScalaTemplatesTask
import templates.tasks.scala.InitScalaProjectTask

/**
 * Adds basic tasks for bootstrapping Scala projects.
 *
 * Adds createScalaClass, createScalaObject, createScalaProject, exportScalaTemplates, and initScalaProject tasks.
 */
class ScalaTemplatesPlugin implements Plugin<Project> {
    // TODO: is this plugin really needed or just roll into main?

    void apply(Project project) {
		project.task 'createScalaClass',   type:CreateScalaClassTask
		project.task 'createScalaObject',  type:CreateScalaObjectTask
        project.task 'createScalaProject', type:CreateScalaProjectTask

        project.task 'exportScalaTemplates', type:ExportScalaTemplatesTask

        project.task 'initScalaProject', type:InitScalaProjectTask
    }
}

