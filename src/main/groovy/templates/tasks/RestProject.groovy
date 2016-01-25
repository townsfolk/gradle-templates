package templates.tasks

import static com.google.common.base.CaseFormat.LOWER_CAMEL
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
        this.serviceName = serviceName.capitalize()
        this.serviceId = "${UPPER_CAMEL.to(LOWER_HYPHEN, serviceName)}"
        this.servicePackage = "com.blackbaud.${serviceName.toLowerCase()}"
        this.servicePackagePath = servicePackage.replaceAll("\\.", "/")
    }

    void initRestProject(boolean postgres) {
        basicProject.initGradleProject()
        createRestBase()
        if (postgres) {
            DatasourceProject datasourceProject = new DatasourceProject(basicProject)
            datasourceProject.initPostgres()
            basicProject.commitProjectFiles("initialize postgres container")
        }
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
            "RandomBuilderSupport.java" template: "/templates/test/random-builder-support.java.tmpl",
                    packageName: servicePackage
            "RandomClientBuilderSupport.java" template: "/templates/test/random-client-builder-support.java.tmpl",
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

    void createEmbeddedService(boolean addEntity) {
        addResourcePaths()
        createRestResource(serviceName, addEntity)

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

    void createRestResource(String resourceName, boolean addEntity) {
        String resourcePath = "${UPPER_CAMEL.to(LOWER_UNDERSCORE, resourceName)}"
        String resourceVarName = "${resourcePath.toUpperCase()}_PATH"
        String resourceNameLowerCamel = UPPER_CAMEL.to(LOWER_CAMEL, resourceName)

        addResourcePathConstant(resourcePath, resourceVarName)
        basicProject.applyTemplate("src/main/java/${servicePackagePath}/resources") {
            "${resourceName}Resource.java" template: "/templates/springboot/rest/resource.java.tmpl",
                    resourceName: resourceName, servicePackage: "${servicePackage}", resourcePathVar: resourceVarName
        }
        basicProject.applyTemplate("src/componentTest/groovy/${servicePackagePath}/resources") {
            "${resourceName}ResourceSpec.groovy" template: "/templates/springboot/rest/resource-spec.groovy.tmpl",
                    resourceName: resourceName, servicePackage: "${servicePackage}"

            "${resourceName}ResourceWireSpec.groovy" template: "/templates/springboot/rest/resource-wirespec.groovy.tmpl",
                    resourceName: resourceName, servicePackage: "${servicePackage}"
        }

        basicProject.applyTemplate("src/main/java/${servicePackagePath}/api") {
            "${resourceName}.java" template: "/templates/springboot/rest/resource-api.java.tmpl",
                    resourceName: resourceName, packageName: "${servicePackage}.api"
        }
        basicProject.applyTemplate("src/mainTest/groovy/${servicePackagePath}/client/model") {
            "Random${resourceName}Builder.groovy" template: "/templates/test/random-builder.groovy.tmpl",
                    targetClass: resourceName, packageName: "${servicePackage}.client.model"
        }
        FileUtils.appendToClass(basicProject.findFile("RandomClientBuilderSupport.java"), """

    public Random${resourceName}Builder ${resourceNameLowerCamel}() {
        return new Random${resourceName}Builder();
    }
""")

        if (addEntity) {
            basicProject.applyTemplate("src/main/java/${servicePackagePath}/core/domain") {
                "${resourceName}Entity.java" template: "/templates/springboot/rest/resource-entity.java.tmpl",
                        resourceName: resourceName, packageName: "${servicePackage}.core.domain", tableName: resourcePath
            }

            basicProject.applyTemplate("src/mainTest/groovy/${servicePackagePath}/core/domain") {
                "Random${resourceName}EntityBuilder.groovy" template: "/templates/test/random-builder.groovy.tmpl",
                        targetClass: "${resourceName}Entity", packageName: "${servicePackage}.core.domain"
            }

            FileUtils.appendToClass(basicProject.findFile("RandomBuilderSupport.java"), """

    public Random${resourceName}EntityBuilder ${resourceNameLowerCamel}Entity() {
        return new Random${resourceName}EntityBuilder();
    }
""")

            DatasourceProject datasourceProject = new DatasourceProject(basicProject)
            datasourceProject.addCreateTableScript(resourcePath)
        }
    }

    private void addResourcePathConstant(String resourcePath, String resourceVarName) {
        File resourcePathsFile = basicProject.getProjectFile("src/main/java/${servicePackagePath}/api/ResourcePaths.java")
        if (resourcePathsFile.exists() == false) {
            addResourcePaths()
        }

        FileUtils.appendToClass(resourcePathsFile, """
    public static final String ${resourceVarName} = "/${resourcePath}";
""")
    }

}
