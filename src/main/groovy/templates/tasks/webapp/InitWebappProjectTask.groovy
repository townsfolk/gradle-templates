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

package templates.tasks.webapp

import org.gradle.api.tasks.TaskAction
import templates.TemplatesPlugin

/**
 * Task to initialize a webapp project.
 */
class InitWebappProjectTask extends AbstractWebappProjectTask {

    InitWebappProjectTask(){
        super(
            'initWebappProject',
            'Initializes a new Gradle Webapp project in the current directory.'
        )
    }

    @TaskAction def init(){
        def props = project.properties

        createBase(project.name)

        boolean useJetty = false
        if( props[USE_JETTY_PLUGIN] ){
            useJetty = props[USE_JETTY_PLUGIN]?.toLowerCase() == 'y'
        } else {
            useJetty = TemplatesPlugin.promptYesOrNo('Use Jetty Plugin?')
        }

        File buildFile = new File(defaultDir(), 'build.gradle')
        buildFile.exists() ?: buildFile.createNewFile()

        TemplatesPlugin.prependPlugin useJetty ? 'jetty' : 'war', buildFile
    }
}
