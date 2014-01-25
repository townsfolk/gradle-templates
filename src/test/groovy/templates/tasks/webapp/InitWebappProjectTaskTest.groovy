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
import org.junit.Test
import templates.AbstractTaskTester

import static templates.tasks.webapp.AbstractWebappProjectTask.PROJECT_GROUP
import static templates.tasks.webapp.AbstractWebappProjectTask.USE_JETTY_PLUGIN

class InitWebappProjectTaskTest extends AbstractTaskTester {

    InitWebappProjectTaskTest(){
        super( InitWebappProjectTask )
    }

    @Test void 'init: jetty'(){
        project.ext[PROJECT_GROUP] = 'test-group'
        project.ext[USE_JETTY_PLUGIN] = 'y'

        task.init()

        assertFileExists folder.root, 'src/main/java'
        assertFileExists folder.root, 'src/main/resources'
        assertFileExists folder.root, 'src/test/java'
        assertFileExists folder.root, 'src/test/resources'
        assertFileExists folder.root, 'LICENSE.txt'

        assertFileContains folder.root, 'build.gradle', 'apply plugin: \'jetty\''
    }

    @Test void 'init: war'(){
        project.ext[PROJECT_GROUP] = 'test-group'
        project.ext[USE_JETTY_PLUGIN] = 'n'

        task.init()

        assertFileExists folder.root, 'src/main/java'
        assertFileExists folder.root, 'src/main/resources'
        assertFileExists folder.root, 'src/test/java'
        assertFileExists folder.root, 'src/test/resources'
        assertFileExists folder.root, 'LICENSE.txt'

        assertFileContains folder.root, 'build.gradle', 'apply plugin: \'war\''
    }
}
