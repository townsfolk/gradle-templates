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

package templates.tasks.groovy

import org.gradle.api.tasks.TaskAction
import templates.TemplatesPlugin

/**
 * Task to export the default groovy templates.
 */
class ExportGroovyTemplatesTask extends AbstractGroovyProjectTask {

    ExportGroovyTemplatesTask(){
        super(
            'exportGroovyTemplates',
            'Exports the default groovy template files into the current directory.'
        )
    }

    // FIXME: these export() methods are all very similar, refactor into something common
    @TaskAction def export(){
        TemplatesPlugin.exportTemplates([
            '/templates/groovy/build.gradle.tmpl',
            '/templates/groovy/groovy-class.tmpl'
        ])
    }
}
