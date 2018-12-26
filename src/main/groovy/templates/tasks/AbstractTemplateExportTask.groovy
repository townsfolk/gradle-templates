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

package templates.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import templates.TemplatesPlugin

/**
 * Abstract base task for template exporters.
 */
abstract class AbstractTemplateExportTask extends DefaultTask {

    private final templatePaths = []

    /**
     * Creates a new template export task with the given properties and templates.
     *
     * @param name the task name
     * @param description the task description
     * @param paths the template paths
     */
    protected AbstractTemplateExportTask( final String name, final String description, final paths = []){
        //this.name = name
        this.group = TemplatesPlugin.group
        this.description = description
        this.templatePaths = paths
    }

    /**
     * Exports the configured templates.
     */
    @TaskAction void export(){
        exportTemplates templatePaths
    }

    private void exportTemplates(def templates = []) {
        templates.ProjectTemplate.fromUserDir {
            templates.each { template ->
                def tStream = getClass().getResourceAsStream(template)
                "$template" tStream.text
            }
        }
    }
}
