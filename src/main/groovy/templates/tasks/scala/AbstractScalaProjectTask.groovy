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

import org.gradle.api.Project
import templates.ProjectTemplate
import templates.TemplatesPlugin
import templates.tasks.AbstractProjectTask
/**
 * Base class for Scala tasks.
 */
abstract class AbstractScalaProjectTask extends AbstractProjectTask {

    protected static final String NEW_PROJECT_NAME = 'newProjectName'
    protected static final String PROJECT_GROUP = 'projectGroup'
    protected static final String PROJECT_VERSION = 'projectVersion'
    protected static final String PROJECT_PARENT_DIR = 'projectParentDir'
    protected static final String SCALA_VERSION = 'scalaVersion'
    protected static final String USE_FAST_SCALA_COMPILER = 'useFastScalaCompiler'

    /**
     * Creates the basic Scala project directory structure.
     *
     * @param path the root of the project. Optional,defaults to user.dir.
     */
    protected void createBase(String path = defaultDir() ){
        ProjectTemplate.fromRoot(path) {
            'src' {
                'main' {
                    'scala' {}
                    'resources' {}
                }
                'test' {
                    'scala' {}
                    'resources' {}
                }
            }
            'LICENSE.txt' '// Your License Goes here'
        }
    }

    /**
     * Initializes or creates the project's build.gradle file.
     *
     * @param project The project
     * @param path The path to the root of the project. optional, defaults to user.dir.
     */
    protected void setupBuildFile(Project project, String path = defaultDir() ) {
        def props = project.properties

        String scalaVersion = props[SCALA_VERSION] ?: TemplatesPlugin.prompt('Scala Version:', '2.9.0')

        boolean useFastCompiler = props[USE_FAST_SCALA_COMPILER] ?: TemplatesPlugin.promptYesOrNo('Use fast compiler?', false)

        ProjectTemplate.fromRoot(path) {
            'build.gradle' template: '/templates/scala/build.gradle.tmpl', append: true,
                scalaVersion: scalaVersion,
                useFastCompiler: useFastCompiler,
                projectGroup: props[PROJECT_GROUP]
            'gradle.properties' content: "version=${props[PROJECT_VERSION] ?: '0.1'}", append: true
        }
    }
}
