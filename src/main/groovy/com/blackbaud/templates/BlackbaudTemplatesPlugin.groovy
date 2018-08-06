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

import com.blackbaud.templates.project.ProjectProps
import com.blackbaud.templates.tasks.AddCosmosContainerTask
import com.blackbaud.templates.tasks.AddCosmosObjectTask
import com.blackbaud.templates.tasks.AddEventHubsTask
import com.blackbaud.templates.tasks.AddEventHubsMessageTask
import com.blackbaud.templates.tasks.AddKafkaMessageTask
import com.blackbaud.templates.tasks.AddPerformanceTestsTask
import com.blackbaud.templates.tasks.AddRestApiObjectTask
import com.blackbaud.templates.tasks.AddJpaObjectTask
import com.blackbaud.templates.tasks.AddServiceBusTopicConfigTask
import com.blackbaud.templates.tasks.CreateIntegrationTestProjectTask
import com.blackbaud.templates.project.CreateScsProject
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.blackbaud.templates.tasks.AddKafkaContainerTask
import com.blackbaud.templates.tasks.AddPostgresContainerTask
import com.blackbaud.templates.tasks.CreateLibraryProjectTask
import com.blackbaud.templates.tasks.CreateDeployableProjectTask
import com.blackbaud.templates.tasks.CreateResourceTask

/**
 * The core of the templates plugin.
 */
class BlackbaudTemplatesPlugin implements Plugin<Project> {

    static final String GROUP = 'Template'

    void apply(Project project) {
        ProjectProps customProps = new ProjectProps(project)

        if (customProps.isThisProjectGradleTemplates()) {
            customProps.applyCustomPropertiesFile()

            project.task 'createLibraryProject', type: CreateLibraryProjectTask
            project.task 'createDeployableProject', type: CreateDeployableProjectTask
            project.task 'createScsProject', type: CreateScsProject
            project.task 'createIntegrationTestProject', type: CreateIntegrationTestProjectTask
        }

        project.task 'createResource', type: CreateResourceTask
        project.task 'addPostgresContainer', type: AddPostgresContainerTask
        project.task 'addCosmosContainer', type: AddCosmosContainerTask
        project.task 'addKafkaContainer', type: AddKafkaContainerTask
        project.task 'addKafkaMessage', type: AddKafkaMessageTask
        project.task 'addEventHubsContainer', type: AddEventHubsTask
        project.task 'addEventHubsMessage', type: AddEventHubsMessageTask
        project.task 'addRestApiObject', type: AddRestApiObjectTask
        project.task 'addJpaObject', type: AddJpaObjectTask
        project.task 'addCosmosObject', type: AddCosmosObjectTask
        project.task 'addServiceBusTopicConfig', type: AddServiceBusTopicConfigTask
        project.task 'addPerformanceTests', type: AddPerformanceTestsTask
    }

}
