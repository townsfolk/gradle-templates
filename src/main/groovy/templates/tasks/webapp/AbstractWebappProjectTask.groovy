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

import templates.ProjectTemplate
import templates.TemplatesPlugin
import templates.tasks.AbstractProjectTask

/**
 *
 */
abstract class AbstractWebappProjectTask extends AbstractProjectTask {

    static final String USE_JETTY_PLUGIN = 'useJettyPlugin'

    AbstractWebappProjectTask( final String name, final String description ){
        super( name, description )
    }

    /**
     * Creates the basic Groovy project directory structure.
     * @param path the root of the project. Optional,defaults to user.dir.
     */
    protected void createBase(String path = defaultDir(), String projectName) {
        createJavaBase path

        ProjectTemplate.fromRoot(path) {
            'src/main/webapp/WEB-INF' {
                'web.xml' template: '/templates/webapp/web-xml.tmpl', project: [name: projectName]
            }
        }
    }

    protected boolean useJetty(){
        if( project.properties[USE_JETTY_PLUGIN] ){
            return project.properties[USE_JETTY_PLUGIN]?.toLowerCase() == 'y'
        } else {
            return TemplatesPlugin.promptYesOrNo('Use Jetty Plugin?')
        }
    }

    // FIXME: copied from AbstractJavaProjectTask
    private void createJavaBase(String path = defaultDir() ){
        ProjectTemplate.fromRoot(path) {
            'src' {
                'main' {
                    'java' {}
                    'resources' {}
                }
                'test' {
                    'java' {}
                    'resources' {}
                }
            }
            'LICENSE.txt' '// Your License Goes here'
        }
    }
}
