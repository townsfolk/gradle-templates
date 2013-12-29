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

import templates.JavaTemplatesPlugin
import templates.ProjectTemplate
import templates.TemplatesPlugin
import templates.tasks.AbstractProjectTask

/**
 *
 */
abstract class AbstractGradleProjectTask extends AbstractProjectTask {

    static final String PLUGIN_APPLY_LABEL = 'pluginApplyLabel'
    static final String PLUGIN_CLASS_NAME = 'pluginClassName'

    AbstractGradleProjectTask( final String name, final String description ){
        super( name, description )
    }

    /**
     * Creates the default project structure for a new gradle plugin.
     * @param path The root path of the project. optional, defaults to user.dir.
     * @param project A project object.
     */
    protected void createBase(String path = defaultDir(), def project) {

        def props = project.properties
        String lProjectName = project.name.toLowerCase()
        String cProjectName = project.name.capitalize()
        String projectGroup = props[ PROJECT_GROUP ] ?: TemplatesPlugin.prompt('Group:', lProjectName)
        String projectVersion = props[ PROJECT_VERSION ] ?: TemplatesPlugin.prompt('Version:', '1.0')
        String pluginApplyLabel = props[ PLUGIN_APPLY_LABEL ] ?: TemplatesPlugin.prompt('Plugin \'apply\' label:', lProjectName)
        String pluginClassName = props[ PLUGIN_CLASS_NAME ] ?: TemplatesPlugin.prompt('Plugin class name:', "${projectGroup}.${cProjectName}Plugin")

        createGroovyBase( path )

        ProjectTemplate.fromRoot(path){
            'src/main/' {
                'resources/META-INF/gradle-plugins' {
                    "${pluginApplyLabel}.properties" "implementation-class=${pluginClassName}"
                }
                'groovy' {
                    if (pluginClassName) {
                        def classParts = JavaTemplatesPlugin.getClassParts(pluginClassName)
                        "${classParts.classPackagePath}" {
                            "${classParts.className}.groovy" template: '/templates/plugin/plugin-class.tmpl',
                                className: classParts.className,
                                classPackage: classParts.classPackage
                            "${classParts.className}Convention.groovy" template: '/templates/plugin/convention-class.tmpl',
                                className: classParts.className,
                                classPackage: classParts.classPackage
                        }
                    }
                }
            }
            'build.gradle' template: '/templates/plugin/build.gradle.tmpl', projectGroup: projectGroup
            'gradle.properties' content: "version=${projectVersion}", append: true
        }
    }

    // FIXME: a bit of duplicated code to start with, need to refactor away
    private void createGroovyBase( path ){
        ProjectTemplate.fromRoot(path) {
            'src' {
                'main' {
                    'groovy' {}
                    'resources' {}
                }
                'test' {
                    "groovy" {}
                    "resources" {}
                }
            }
            'LICENSE.txt' '// Your License Goes here'
        }
    }
}
