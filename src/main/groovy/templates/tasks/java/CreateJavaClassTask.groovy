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

package templates.tasks.java

import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import templates.JavaTemplatesPlugin
import templates.ProjectTemplate
import templates.TemplatesPlugin

/**
 * Task to create a new Java class in the current project.
 */
class CreateJavaClassTask extends AbstractJavaProjectTask {

    // TODO: the property names should be standardized and in a shared location
    public static final String NEW_CLASS_NAME = 'newClassName'

    CreateJavaClassTask(){
        super(
            'createJavaClass',
            'Creates a new Java class in the current project.'
        )
    }

    @TaskAction def create(){
        def mainSrcDir = null
        try {
            // get main java dir, and check to see if Java plugin is installed.
            mainSrcDir = findMainJavaDir(project)
        } catch (Exception e) {
            throw new IllegalStateException('It seems that the Java plugin is not installed, I cannot determine the main java source directory.', e)
        }

        def fullClassName = project.properties[NEW_CLASS_NAME] ?: TemplatesPlugin.prompt('Class name (com.example.MyClass)')
        if (fullClassName) {
            def classParts = JavaTemplatesPlugin.getClassParts(fullClassName)
            ProjectTemplate.fromUserDir {
                "${mainSrcDir}" {
                    "${classParts.classPackagePath}" {
                        "${classParts.className}.java" template: '/templates/java/java-class.tmpl',
                            classPackage: classParts.classPackage,
                            className: classParts.className
                    }
                }
            }
        } else {
            // TODO: should be an error
            println 'No class name provided.'
        }
    }

    /**
     * Finds the path to the main java source directory.
     *
     * @param project The project.
     * @return The path to the main java source directory.
     */
    private static String findMainJavaDir( final Project project ){
        def mainSrcDir = project.sourceSets?.main?.java?.srcDirs*.path
        mainSrcDir = mainSrcDir?.first()
        mainSrcDir = mainSrcDir?.minus(project.projectDir.path)
        return mainSrcDir
    }
}
