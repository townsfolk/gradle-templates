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

import static templates.tasks.webapp.AbstractWebappProjectTask.NEW_PROJECT_NAME
import static templates.tasks.webapp.AbstractWebappProjectTask.PROJECT_GROUP
import static templates.tasks.webapp.AbstractWebappProjectTask.PROJECT_PARENT_DIR
import static templates.tasks.webapp.AbstractWebappProjectTask.PROJECT_VERSION
import static templates.tasks.webapp.AbstractWebappProjectTask.USE_JETTY_PLUGIN

class CreateWebappProjectTaskTest extends AbstractTaskTester {

    CreateWebappProjectTaskTest(){
        super( CreateWebappProjectTask )
    }

    @Test void 'create: jetty'(){
        project.ext[PROJECT_PARENT_DIR] = folder.getRoot() as String
        project.ext[NEW_PROJECT_NAME] = 'tester'
        project.ext[PROJECT_GROUP] = 'test-group'
        project.ext[PROJECT_VERSION] = '1.1.1'
        project.ext[USE_JETTY_PLUGIN] = 'y'

        task.create()

        assertFileExists folder.root, 'tester/src/main/java'
        assertFileExists folder.root, 'tester/src/main/resources'
        assertFileExists folder.root, 'tester/src/test/java'
        assertFileExists folder.root, 'tester/src/test/resources'
        assertFileExists folder.root, 'tester/LICENSE.txt'

        assertFileContains folder.root, 'tester/build.gradle', 'group = \'test-group\'', 'apply plugin: \'jetty\''
        assertFileContains folder.root, 'tester/gradle.properties', 'version=1.1.1'
    }

    @Test void 'create: war'(){
        project.ext[PROJECT_PARENT_DIR] = folder.getRoot() as String
        project.ext[NEW_PROJECT_NAME] = 'tester'
        project.ext[PROJECT_GROUP] = 'test-group'
        project.ext[PROJECT_VERSION] = '1.1.1'
        project.ext[USE_JETTY_PLUGIN] = 'n'

        task.create()

        assertFileExists folder.root, 'tester/src/main/java'
        assertFileExists folder.root, 'tester/src/main/resources'
        assertFileExists folder.root, 'tester/src/test/java'
        assertFileExists folder.root, 'tester/src/test/resources'
        assertFileExists folder.root, 'tester/LICENSE.txt'

        assertFileContains folder.root, 'tester/build.gradle', 'group = \'test-group\'', 'apply plugin: \'war\''
        assertFileContains folder.root, 'tester/gradle.properties', 'version=1.1.1'
    }
}
