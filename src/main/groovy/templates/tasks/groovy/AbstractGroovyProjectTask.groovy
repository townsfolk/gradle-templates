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

import templates.ProjectTemplate
import templates.TemplatesPlugin
import templates.tasks.AbstractProjectTask

/**
 *
 */
abstract class AbstractGroovyProjectTask extends AbstractProjectTask {

    static final String PROJECT_PARENT_DIR = 'projectParentDir'
    static final String NEW_PROJECT_NAME = 'newProjectName'
    static final String PROJECT_GROUP = 'projectGroup'
    static final String PROJECT_VERSION = 'projectVersion'

    AbstractGroovyProjectTask( final String name, final String description ){
        this.name = name
        this.group = TemplatesPlugin.group
        this.description = description
    }

    /**
     * Creates the basic Groovy project directory structure.
     * @param path the root of the project. Optional,defaults to user.dir.
     */
    protected void createBase(String path = defaultDir() ) {
        ProjectTemplate.fromRoot(path) {
            'src' {
                'main' {
                    'groovy' {}
                    'resources' {}
                }
                'test' {
                    "groovy" {}
                    "resources" {}
                }
            }
            'LICENSE.txt' '// Your License Goes here'
        }
    }
}
