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
import org.gradle.api.tasks.TaskAction
import templates.JavaTemplatesPlugin
import templates.ProjectTemplate
import templates.TemplatesPlugin

/**
 * Task for creating a new scala class in the current project.
 */
class CreateScalaClassTask extends AbstractScalaProjectTask {

    protected static final String NEW_CLASS_NAME = 'newClassName'
    protected static final String NEW_OBJECT_NAME = 'newObjectName'

    CreateScalaClassTask(){
        super(
            'createScalaClass',
            'Creates a new Scala class in the current project.'
        )
    }

    @TaskAction
    def create(){
        createScalaClass project, false
    }

    /**
     * Creates a Scala class, or object in the current working directory.
     *
     * @param project The project.
     * @param object Is this a Scala class, or object.
     */
    protected void createScalaClass(Project project, boolean object) {
        def mainSrcDir = null
        try {
            // get main Scala dir, and check to see if Scala plugin is installed.
            mainSrcDir = findMainScalaDir(project)
        } catch (Exception e) {
            throw new IllegalStateException('It seems that the Scala plugin is not installed, I cannot determine the main scala source directory.', e)
        }

        def props = project.properties
        def type = object ? 'Object' : 'Class'

        // TODO: seems like we'd only need one name that works for either case
        def fullClassName = props[NEW_CLASS_NAME] ?: props[NEW_OBJECT_NAME] ?: TemplatesPlugin.prompt("${type} name (com.example.My${type})")

        if (fullClassName) {
            def classParts = JavaTemplatesPlugin.getClassParts(fullClassName)
            ProjectTemplate.fromUserDir {
                "${mainSrcDir}" {
                    "${classParts.classPackagePath}" {
                        "${classParts.className}.scala" template: '/templates/scala/scala-class.tmpl',
                            className: classParts.className,
                            classPackage: classParts.classPackage,
                            object: object
                    }
                }
            }

        } else {
            // TODO: should be an error of some sort
            println 'No class name provided.'
        }
    }

    /**
     * Finds the path to the main java source directory.
     *
     * @param project The project.
     * @return The path to the main groovy source directory.
     */
    protected static String findMainScalaDir( final Project project ){
        def mainSrcDir = project.sourceSets?.main?.scala?.srcDirs*.path
        mainSrcDir = mainSrcDir?.first()
        mainSrcDir = mainSrcDir?.minus(project.projectDir.path)
        return mainSrcDir
    }
}
