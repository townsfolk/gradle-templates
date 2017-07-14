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
package com.blackbaud.templates

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.blackbaud.templates.tasks.AddKafkaContainerTask
import com.blackbaud.templates.tasks.AddPostgresContainerTask
import com.blackbaud.templates.tasks.CreateBasicProjectTask
import com.blackbaud.templates.tasks.CreateEmbeddedServiceTask
import com.blackbaud.templates.tasks.CreateRestProjectTask
import com.blackbaud.templates.tasks.CreateRestResourceTask

/**
 * The core of the templates plugin.
 */
class BlackbaudTemplatesPlugin implements Plugin<Project> {

    static final String GROUP = 'Template'

    void apply(Project project) {
        ProjectProps customProps = new ProjectProps(project)

        if (customProps.isThisProjectGradleTemplates()) {
            customProps.applyCustomPropertiesFile()

            project.task 'createBasicProject', type: CreateBasicProjectTask
            project.task 'createRestProject', type: CreateRestProjectTask
        }

        project.task 'createRestResource', type: CreateRestResourceTask
        project.task 'createRestEmbeddedService', type: CreateEmbeddedServiceTask
        project.task 'addPostgresContainer', type: AddPostgresContainerTask
        project.task 'addKafkaContainer', type: AddKafkaContainerTask
    }

}
