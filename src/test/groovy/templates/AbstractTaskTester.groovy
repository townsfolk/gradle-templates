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

package templates

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before

/**
 * Base class for template-based task testers.
 */
@Slf4j
abstract class AbstractTaskTester {

    protected File testRoot
    protected Project project
    protected Task task

    private final Class taskClass

    AbstractTaskTester(final Class taskClass) {
        this.taskClass = taskClass
    }

    @Before
    void before() {
        final testsDir = System.properties['tests.dir'] ?: "${System.properties['user.dir']}/build/tests"
        final testProjectName = "${taskClass.simpleName}_Tests"
        final initDir = "${testsDir}/${testProjectName}"
        testRoot = new File(initDir)

        project = ProjectBuilder.builder()
                .withName(testProjectName)
                .withProjectDir(new File(initDir))
                .build()

        log.debug("user.dir: ${System.properties['user.dir']}")
        log.debug("Project: ${project}")
        log.debug("    Dir: ${project.projectDir}")
        log.debug("   Root: ${project.rootDir}")
        log.debug("  Build: ${project.buildDir}")

        System.setProperty('init.dir', initDir)

        task = project.task('targetTask', type: taskClass)
    }

    @After
    void after() {
        System.setProperty('init.dir', '')
    }

    /**
     * Asserts that the specified file exists.
     *
     * @param root the root directory
     * @param path the path to the file
     */
    protected void assertFileExists(File root, String path) {
        assert new File(root, path).exists()
    }

    /**
     * Asserts that the file at the given path (root+path) exists and that it contains the specified
     * content strings.
     *
     * @param root the root directory
     * @param path the path to the file
     * @param contents the content string to be tested
     */
    protected void assertFileContains(File root, String path, String... contents) {
        assertFileExists root, path

        String text = new File(root, path).text

        contents.each { String str ->
            assert text.contains(str)
        }
    }
}
