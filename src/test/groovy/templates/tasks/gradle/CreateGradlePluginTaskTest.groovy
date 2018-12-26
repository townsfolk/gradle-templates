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

package templates.tasks.gradle
import org.junit.Test
import templates.AbstractTaskTester

import static AbstractGradleProjectTask.NEW_PROJECT_NAME
import static AbstractGradleProjectTask.PROJECT_PARENT_DIR
import static AbstractGradleProjectTask.PROJECT_GROUP
import static AbstractGradleProjectTask.PROJECT_VERSION
import static AbstractGradleProjectTask.PLUGIN_CLASS_NAME
import static AbstractGradleProjectTask.PLUGIN_APPLY_LABEL

class CreateGradlePluginTaskTest extends AbstractTaskTester {

    CreateGradlePluginTaskTest(){
        super( CreateGradlePluginTask )
    }

    @Test void create(){
        project.ext[PROJECT_PARENT_DIR] = "${testRoot}"
        project.ext[NEW_PROJECT_NAME] = 'tester'
        project.ext[PROJECT_GROUP] = 'test-group'
        project.ext[PROJECT_VERSION] = '1.1.1'
        project.ext[PLUGIN_APPLY_LABEL] = 'test-foo'
        project.ext[PLUGIN_CLASS_NAME] = 'com.test'

        task.create()

        assertFileExists testRoot, 'tester/src/main/groovy'
        assertFileExists testRoot, 'tester/src/main/resources'
        assertFileExists testRoot, 'tester/src/test/groovy'
        assertFileExists testRoot, 'tester/src/test/resources'
        assertFileExists testRoot, 'tester/LICENSE.txt'

        assertFileContains testRoot, 'tester/build.gradle', 'group = \'test-group\''
        assertFileContains testRoot, 'tester/gradle.properties', 'version=1.1.1'
    }
}
