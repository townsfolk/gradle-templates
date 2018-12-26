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
import org.junit.Test
import templates.AbstractTaskTester

class CreateGroovyProjectTaskTest extends AbstractTaskTester {

    CreateGroovyProjectTaskTest(){
        super( CreateGroovyProjectTask )
    }

    @Test void create(){
        project.ext[ CreateGroovyProjectTask.PROJECT_PARENT_DIR] = "${testRoot}"
        project.ext[ CreateGroovyProjectTask.NEW_PROJECT_NAME] = 'tester'
        project.ext[ CreateGroovyProjectTask.PROJECT_GROUP] = 'test-group'
        project.ext[ CreateGroovyProjectTask.PROJECT_VERSION] = '1.1.1'

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
