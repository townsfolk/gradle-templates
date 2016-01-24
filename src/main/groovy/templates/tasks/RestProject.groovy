package templates.tasks

import static com.google.common.base.CaseFormat.LOWER_HYPHEN
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE
import static com.google.common.base.CaseFormat.UPPER_CAMEL

class RestProject {

    private BasicProject basicProject
    private String serviceName
    private String serviceId
    private String servicePackage
    private String servicePackagePath

    RestProject(BasicProject basicProject) {
        this(basicProject, LOWER_HYPHEN.to(UPPER_CAMEL, basicProject.repoName))
    }

    RestProject(BasicProject basicProject, String serviceName) {
        this.basicProject = basicProject
        this.serviceName = serviceName
        this.serviceId = "${UPPER_CAMEL.to(LOWER_HYPHEN, serviceName)}"
        this.servicePackage = "com.blackbaud.${serviceName.toLowerCase()}"
        this.servicePackagePath = servicePackage.replaceAll("\\.", "/")
    }

    void initRestProject() {
        basicProject.initGradleProject()
        createRestBase()
    }

    private void createRestBase() {
        addResourcePaths()
        basicProject.applyTemplate("src/main/java/${servicePackagePath}") {
            "${serviceName}.java" template: "/templates/springboot/application-class.java.tmpl",
                    serviceName: serviceName, servicePackage: servicePackage
        }

        basicProject.applyTemplate("src/componentTest/java/${servicePackagePath}") {
            "ComponentTest.java" template: "/templates/springboot/rest/component-test-annotation.java.tmpl",
                    serviceName: serviceName, packageName: servicePackage

            "${serviceName}TestConfig.java" template: "/templates/springboot/rest/application-test-config.java.tmpl",
                    className: "${serviceName}TestConfig", packageName: servicePackage
        }

        basicProject.applyTemplate("src/mainTest/groovy/${servicePackagePath}") {
            "ARandom.java" template: "/templates/test/arandom.java.tmpl",
                    packageName: servicePackage
        }

        basicProject.applyTemplate {
            'build.gradle' template: "/templates/springboot/rest/build.gradle.tmpl"
            'gradle.properties' template: "/templates/basic/gradle.properties.tmpl",
                    artifactId: serviceId
            'src' {
                'main' {
                    'resources' {
                        'application.properties' template: "/templates/springboot/rest/application.properties.tmpl"
                    }
                }
                'test' {
                    "groovy" {}
                }
                'componentTest' {
                    'resources' {
                        'logback.xml' template: "/templates/logback/logback.tmpl"

                        'db' {
                            "test_cleanup.sql" content: ""
                        }
                    }
                }
            }
        }

        basicProject.applyTemplate {
            'settings.gradle' content: "include 'client'"
            'client' {
                'build.gradle' template: "/templates/springboot/rest/client/build.gradle.tmpl"
                'src' {
                    'main' {
                        'resources' {
                            'swagger-gen-config.json' template: "/templates/springboot/rest/client/swagger-gen-config.json.tmpl",
                                    packageName: servicePackage, artifactId: basicProject.repoName
                        }
                    }
                }
            }
        }

        basicProject.commitProjectFiles("springboot rest bootstrap")
    }

    private void addResourcePaths() {
        basicProject.applyTemplate("src/main/java/${servicePackagePath}/api") {
            'ResourcePaths.java' template: "/templates/springboot/rest/resource-paths.java.tmpl",
                    packageName: "${servicePackage}.api"
        }
    }

    void createEmbeddedService() {
        addResourcePaths()
        createRestResource(serviceName)

        String moduleName = "client-${serviceId}"
        basicProject.getProjectFile("settings.gradle").withWriterAppend { BufferedWriter writer ->
            writer.newLine()
            writer.write("include '${moduleName}'")
        }
        basicProject.applyTemplate {
            "${moduleName}" {
                'build.gradle' template: "/templates/springboot/rest/client/embedded-build.gradle.tmpl",
                        embeddedService: serviceName.toLowerCase()
                'src' {
                    'main' {
                        'resources' {
                            'swagger-gen-config.json' template: "/templates/springboot/rest/client/swagger-gen-config.json.tmpl",
                                    packageName: servicePackage, artifactId: "${serviceId}-client"
                        }
                    }
                }
            }
        }
    }

    void createRestResource(String resourceName) {
        String resourcePath = "${UPPER_CAMEL.to(LOWER_UNDERSCORE, resourceName)}"
        String resourceVarName = "${resourcePath.toUpperCase()}_PATH"
        basicProject.applyTemplate("src/main/java/${servicePackagePath}/resources") {
            "${resourceName}Resource.java" template: "/templates/springboot/rest/resource.java.tmpl",
                    resourceName: resourceName, servicePackage: "${servicePackage}", resourcePathVar: resourceVarName
        }
        addResourcePathConstant(resourcePath, resourceVarName)

        basicProject.applyTemplate("src/componentTest/groovy/${servicePackagePath}/resources") {
            "${resourceName}ResourceSpec.groovy" template: "/templates/springboot/rest/resource-spec.groovy.tmpl",
                    resourceName: resourceName, servicePackage: "${servicePackage}"

            "${resourceName}ResourceWireSpec.groovy" template: "/templates/springboot/rest/resource-wirespec.groovy.tmpl",
                    resourceName: resourceName, servicePackage: "${servicePackage}"
        }
    }

    private void addResourcePathConstant(String resourcePath, String resourceVarName) {
        File resourcePathsFile = basicProject.getProjectFile("src/main/java/${servicePackagePath}/api/ResourcePaths.java")
        if (resourcePathsFile.exists() == false) {
            basicProject.applyTemplate("src/main/java/${servicePackagePath}/api") {
                'ResourcePaths.java' template: "/templates/springboot/rest/resource-paths.java.tmpl",
                                     packageName: "${servicePackage}.api"
            }
        }

        String resourcePathsText = resourcePathsFile.text
        resourcePathsText = resourcePathsText.replaceAll(/(?m)\s*}\s*/, """
    public static final String ${resourceVarName} = "/${resourcePath}";

}
"""
        )
        resourcePathsFile.text = resourcePathsText
    }

}
