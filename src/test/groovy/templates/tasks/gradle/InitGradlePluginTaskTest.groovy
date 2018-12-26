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

import static templates.tasks.gradle.AbstractGradleProjectTask.*

class InitGradlePluginTaskTest extends AbstractTaskTester{

    InitGradlePluginTaskTest(){
        super( InitGradlePluginTask )
    }

    @Test void init(){
        project.ext[PROJECT_PARENT_DIR] = "${testRoot}"
        project.ext[PROJECT_GROUP] = 'test-group'
        project.ext[PROJECT_VERSION] = '1.1.1'
        project.ext[PLUGIN_APPLY_LABEL] = 'test-foo'
        project.ext[PLUGIN_CLASS_NAME] = 'com.test'

        task.init()

        assertFileExists testRoot, 'src/main/groovy'
        assertFileExists testRoot, 'src/main/resources'
        assertFileExists testRoot, 'src/test/groovy'
        assertFileExists testRoot, 'src/test/resources'
        assertFileExists testRoot, 'LICENSE.txt'

        assertFileContains testRoot, 'build.gradle', 'group = \'test-group\''
        assertFileContains testRoot, 'gradle.properties', 'version=1.1.1'
    }
}
