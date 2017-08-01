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

import com.blackbaud.templates.tasks.AddApiObjectTask
import com.blackbaud.templates.tasks.AddJpaObjectTask
import com.blackbaud.templates.tasks.CreateBasicResourceTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.blackbaud.templates.tasks.AddKafkaContainerTask
import com.blackbaud.templates.tasks.AddPostgresContainerTask
import com.blackbaud.templates.tasks.CreateLibraryProjectTask
import com.blackbaud.templates.tasks.CreateEmbeddedServiceTask
import com.blackbaud.templates.tasks.CreateDeployableProjectTask
import com.blackbaud.templates.tasks.CreateCrudResourceTask

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
        }

        project.task 'createCrudResource', type: CreateCrudResourceTask
        project.task 'createBasicResource', type: CreateBasicResourceTask
        project.task 'createRestEmbeddedService', type: CreateEmbeddedServiceTask
        project.task 'addPostgresContainer', type: AddPostgresContainerTask
        project.task 'addKafkaContainer', type: AddKafkaContainerTask
        project.task 'addApiObject', type: AddApiObjectTask
        project.task 'addJpaObject', type: AddJpaObjectTask
    }

}
