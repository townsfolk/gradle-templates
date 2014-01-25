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

import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import templates.JavaTemplatesPlugin
import templates.ProjectTemplate
import templates.TemplatesPlugin

/**
 * Task to create a new Groovy class in the current project.
 */
class CreateGroovyClassTask extends AbstractGroovyProjectTask {

    public static final String NEW_CLASS_NAME = 'newClassName'

    CreateGroovyClassTask(){
        super(
            'createGroovyClass',
            'Creates a new Groovy class in the current project.'
        )
    }

    @TaskAction def create(){
        def mainSrcDir = null
        try {
            // get main groovy dir, and check to see if Groovy plugin is installed.
            mainSrcDir = findMainGroovyDir(project)
        } catch (Exception e) {
            throw new IllegalStateException('It seems that the Groovy plugin is not installed, I cannot determine the main groovy source directory.', e)
        }

        def fullClassName = project.properties[NEW_CLASS_NAME] ?: TemplatesPlugin.prompt('Class name (com.example.MyClass)')

        if (fullClassName) {
            def classParts = JavaTemplatesPlugin.getClassParts(fullClassName)
            ProjectTemplate.fromUserDir {
                "${mainSrcDir}" {
                    "${classParts.classPackagePath}" {
                        "${classParts.className}.groovy" template: '/templates/groovy/groovy-class.tmpl',
                            className: classParts.className,
                            classPackage: classParts.classPackage
                    }
                }
            }
        } else {
            println 'No class name provided.'
        }
    }

    // FIXME: these finders are all very similar, refactor
    /**
     * Finds the path to the main java source directory.
     * @param project The project.
     * @return The path to the main groovy source directory.
     */
    private static String findMainGroovyDir(Project project) {
        def mainSrcDir = project.sourceSets?.main?.groovy?.srcDirs*.path
        mainSrcDir = mainSrcDir?.first()
        mainSrcDir = mainSrcDir?.minus(project.projectDir.path)
        return mainSrcDir
    }
}
