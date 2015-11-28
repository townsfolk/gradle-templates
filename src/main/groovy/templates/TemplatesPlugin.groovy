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
import templates.tasks.AddPostgresContainerTask
import templates.tasks.CreateBasicProjectTask
import templates.tasks.CreateRestProjectTask
import templates.tasks.CreateRestResourceTask

/**
 * The core of the templates plugin.
 */
class TemplatesPlugin implements Plugin<Project> {

    static final String group = 'Template'

    void apply(Project project) {
        project.convention.plugins.templatePlugin = new TemplatesPluginConvention()

        ProjectProps customProps = new ProjectProps(project)
        if (!customProps.isCustomPropertiesInitialized()) {
            customProps.initCustomPropertiesFile()
        }

        customProps.applyCustomPropertiesFile()
        project.task 'createBasicProject', type: CreateBasicProjectTask
        project.task 'createRestProject', type: CreateRestProjectTask
        project.task 'createRestResource', type: CreateRestResourceTask
        project.task 'addPostgresContainer', type: AddPostgresContainerTask
    }

}
