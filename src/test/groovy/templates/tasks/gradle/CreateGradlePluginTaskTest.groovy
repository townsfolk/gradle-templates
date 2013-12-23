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

class CreateGradlePluginTaskTest extends AbstractTaskTester {

    /*
        FIXME: creating props dynamic is deprecated, fix

        Creating properties on demand (a.k.a. dynamic properties) has been deprecated and is scheduled to be removed in Gradle 2.0. Please read http://gradle.org/docs/current/dsl/org.gradle.api.plugins.ExtraPropertiesExtension.html for information on the replacement for dynamic properties.
     */

    CreateGradlePluginTaskTest(){
        super( CreateGradlePluginTask )
    }

    @Test void create(){
        project.setProperty( CreateGradlePluginTask.PROJECT_PARENT_DIR, folder.getRoot() as String )
        project.setProperty( CreateGradlePluginTask.NEW_PROJECT_NAME, 'tester' )
        project.setProperty( CreateGradlePluginTask.PROJECT_GROUP, 'test-group' )
        project.setProperty( CreateGradlePluginTask.PROJECT_VERSION, '1.1.1' )
        project.setProperty( CreateGradlePluginTask.PLUGIN_APPLY_LABEL, 'test-foo' )
        project.setProperty( CreateGradlePluginTask.PLUGIN_CLASS_NAME, 'com.test' )

        task.create()

        assertFileExists folder.root, 'tester/src/main/groovy'
        assertFileExists folder.root, 'tester/src/main/resources'
        assertFileExists folder.root, 'tester/src/test/groovy'
        assertFileExists folder.root, 'tester/src/test/resources'
        assertFileExists folder.root, 'tester/LICENSE.txt'

        assertFileContains folder.root, 'tester/build.gradle', 'group = \'test-group\''
        assertFileContains folder.root, 'tester/gradle.properties', 'version=1.1.1'
    }
}
