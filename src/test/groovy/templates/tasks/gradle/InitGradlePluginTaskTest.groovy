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

class InitGradlePluginTaskTest extends AbstractTaskTester{

    InitGradlePluginTaskTest(){
        super( InitGradlePluginTask )
    }

    @Test void init(){
        project.setProperty( CreateGradlePluginTask.PROJECT_PARENT_DIR, folder.getRoot() as String )
        project.setProperty( CreateGradlePluginTask.PROJECT_GROUP, 'test-group' )
        project.setProperty( CreateGradlePluginTask.PROJECT_VERSION, '1.1.1' )
        project.setProperty( CreateGradlePluginTask.PLUGIN_APPLY_LABEL, 'test-foo' )
        project.setProperty( CreateGradlePluginTask.PLUGIN_CLASS_NAME, 'com.test' )

        task.init()

        assertFileExists folder.root, 'src/main/groovy'
        assertFileExists folder.root, 'src/main/resources'
        assertFileExists folder.root, 'src/test/groovy'
        assertFileExists folder.root, 'src/test/resources'
        assertFileExists folder.root, 'LICENSE.txt'

        assertFileContains folder.root, 'build.gradle', 'group = \'test-group\''
        assertFileContains folder.root, 'gradle.properties', 'version=1.1.1'
    }
}
