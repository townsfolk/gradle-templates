package com.blackbaud.templates

import com.blackbaud.templates.project.ProjectProps
import org.gradle.api.Project

class TemplatedFileFactory {

    private Project project
    private ProjectProps customProps

    TemplatedFileFactory(Project project) {
        this.project = project
        this.customProps = customProps
    }

    /**
     * Pulls a fully qualified class name into it's parts - package, and name.
     *
     * @param fullClassName
     * @return Map containing the class name, package, and package as a path.
     */
    static Map getClassParts(final String fullClassName) {
        List classParts = fullClassName.split(/\./) as List
        [
                className       : classParts.pop(),
                classPackagePath: classParts.join(File.separator),
                classPackage    : classParts.join('.')
        ]
    }

    void createGroovyClass(String fullClassName) {
        Map classParts = getClassParts(fullClassName)

        ProjectTemplate.fromUserDir {
            "${mainSrcDir}" {
                "${classParts.classPackagePath}" {
                    "${classParts.className}.groovy" template: '/templates/groovy/groovy-class.tmpl',
                            className: classParts.className,
                            classPackage: classParts.classPackage
                }
            }
        }
    }

    void createJavaClass(String fullClassName) {
        Map classParts = getClassParts(fullClassName)

        ProjectTemplate.fromUserDir {
            "${mainSrcDir}" {
                "${classParts.classPackagePath}" {
                    "${classParts.className}.java" template: '/templates/java/java-class.tmpl',
                            classPackage: classParts.classPackage,
                            className: classParts.className
                }
            }
        }

    }

}
