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
import templates.tasks.scala.CreateScalaProjectTask
import templates.tasks.scala.InitScalaProjectTask

class InitScalaProjectTaskTest extends AbstractTaskTester {

    InitScalaProjectTaskTest(){
        super( InitScalaProjectTask )
    }

    @Test void init(){
        project.setProperty( CreateScalaProjectTask.PROJECT_GROUP, 'test-group' )
        project.setProperty( CreateScalaProjectTask.SCALA_VERSION, '2.9.0' )
        project.setProperty( CreateScalaProjectTask.USE_FAST_SCALA_COMPILER, true )

        task.init()

        assertFileExists folder.root, 'src/main/scala'
        assertFileExists folder.root, 'src/main/resources'
        assertFileExists folder.root, 'src/test/scala'
        assertFileExists folder.root, 'src/test/resources'
        assertFileExists folder.root, 'LICENSE.txt'

        assertFileContains folder.root, 'build.gradle', 'scalaVersion = \'2.9.0\'', 'scalaCompileOptions.useCompileDaemon = true', 'group = \'test-group\''
        assertFileContains folder.root, 'gradle.properties', 'version=0.1'
    }
}
