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

import static templates.tasks.gradle.AbstractGradleProjectTask.PROJECT_GROUP

class InitGroovyProjectTaskTest extends AbstractTaskTester {

	InitGroovyProjectTaskTest() {
		super(InitGroovyProjectTask)
	}

	@Test
	void init() {
		project.ext[PROJECT_GROUP] = 'test-group'

		task.init()

		assertFileExists folder.root, 'src/main/groovy'
		assertFileExists folder.root, 'src/main/resources'
		assertFileExists folder.root, 'src/test/groovy'
		assertFileExists folder.root, 'src/test/resources'
		assertFileExists folder.root, 'LICENSE.txt'

		assertFileContains folder.root, 'build.gradle', 'apply plugin: \'groovy\''
	}
}
