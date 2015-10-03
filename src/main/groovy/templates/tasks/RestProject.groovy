package templates.tasks

import static com.google.common.base.CaseFormat.LOWER_HYPHEN
import static com.google.common.base.CaseFormat.UPPER_CAMEL

class RestProject {

    private BasicProject basicProject
    private String serviceName
    private String servicePackage

    RestProject(BasicProject basicProject) {
        this.basicProject = basicProject
        serviceName = LOWER_HYPHEN.to(UPPER_CAMEL, basicProject.repoName)
        servicePackage = "com.blackbaud.${serviceName.toLowerCase()}"
    }

    void initRestProject() {
        basicProject.initGradleProject()
        createRestBase()
    }

    private void createRestBase() {
        basicProject.applyTemplate {
            'build.gradle' template: "/templates/springboot/build.gradle.tmpl"
            'src' {
                'main' {
                    'java' {
                        'com' {
                            'blackbaud' {
                                "${serviceName.toLowerCase()}" {
                                    "${serviceName}.java" template: "/templates/springboot/application-class.tmpl",
                                            serviceName: "${serviceName}",
                                            servicePackage: "${servicePackage}"

                                    'api' {
                                        'ResourcePaths.java' template: "/templates/springboot/resourcepaths-class.tmpl",
                                                packageName: "${servicePackage}.api"
                                    }
                                }
                            }
                        }
                    }
                    'resources' {
                        'application.properties' content: """server.port=8080
management.port=8081
"""
                    }
                }
                'test' {
                    "groovy" {}
                }
                'componentTest' {
                    'resources' {
                        'logback.xml' template: "/templates/logback/logback.tmpl"
                    }
                }
            }
        }
        basicProject.commitProjectFiles("initial commit, springboot rest")
    }

    void createRestResource(String resourceName) {
        basicProject.applyTemplate("src/main/java/com/blackbaud/${serviceName.toLowerCase()}/resources") {
            "${resourceName}Resource.java" template: "/templates/springboot/rest-resource-class.tmpl",
                    resourceName: resourceName, servicePackage: "${servicePackage}"
        }

        basicProject.applyTemplate("src/componentTest/groovy/com/blackbaud/${serviceName.toLowerCase()}/resources") {
            "${resourceName}ResourceSpec.java" template: "/templates/springboot/rest-resource-spec.tmpl",
                    resourceName: resourceName, servicePackage: "${servicePackage}"

            "${resourceName}ResourceWireSpec.java" template: "/templates/springboot/rest-resource-wirespec.tmpl",
                    resourceName: resourceName, servicePackage: "${servicePackage}"
        }
    }

}
