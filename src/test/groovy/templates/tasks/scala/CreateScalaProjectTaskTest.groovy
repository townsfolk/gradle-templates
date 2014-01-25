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

package templates.tasks.scala
import org.junit.Test
import templates.AbstractTaskTester

import static templates.tasks.scala.AbstractScalaProjectTask.NEW_PROJECT_NAME
import static templates.tasks.scala.AbstractScalaProjectTask.PROJECT_GROUP
import static templates.tasks.scala.AbstractScalaProjectTask.PROJECT_PARENT_DIR
import static templates.tasks.scala.AbstractScalaProjectTask.PROJECT_VERSION
import static templates.tasks.scala.AbstractScalaProjectTask.SCALA_VERSION
import static templates.tasks.scala.AbstractScalaProjectTask.USE_FAST_SCALA_COMPILER

class CreateScalaProjectTaskTest extends AbstractTaskTester {

    CreateScalaProjectTaskTest(){
        super( CreateScalaProjectTask )
    }

    @Test
    void create(){
        project.ext[PROJECT_PARENT_DIR] = folder.getRoot() as String
        project.ext[NEW_PROJECT_NAME] = 'tester'
        project.ext[PROJECT_GROUP] = 'test-group'
        project.ext[PROJECT_VERSION] = '1.1.1'
        project.ext[SCALA_VERSION] = '2.9.0'
        project.ext[USE_FAST_SCALA_COMPILER] = true

        task.create()

        assertFileExists folder.root, 'tester/src/main/scala'
        assertFileExists folder.root, 'tester/src/main/resources'
        assertFileExists folder.root, 'tester/src/test/scala'
        assertFileExists folder.root, 'tester/src/test/resources'
        assertFileExists folder.root, 'tester/LICENSE.txt'

        assertFileContains folder.root, 'tester/build.gradle', 'scalaVersion = \'2.9.0\'', 'scalaCompileOptions.useCompileDaemon = true', 'group = \'test-group\''
        assertFileContains folder.root, 'tester/gradle.properties', 'version=1.1.1'
    }
}
