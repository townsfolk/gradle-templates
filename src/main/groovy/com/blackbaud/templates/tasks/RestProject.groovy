package com.blackbaud.templates.tasks

import com.blackbaud.templates.ProjectProps

import static com.google.common.base.CaseFormat.LOWER_CAMEL
import static com.google.common.base.CaseFormat.LOWER_HYPHEN
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE
import static com.google.common.base.CaseFormat.UPPER_CAMEL

class RestProject {

    private BasicProject basicProject

    RestProject(BasicProject basicProject) {
        this.basicProject = basicProject
    }

    ProjectProps getProjectProps() {
        basicProject.projectProps
    }

    String getServiceId() {
        "${UPPER_CAMEL.to(LOWER_HYPHEN, serviceName)}"
    }

    String getServiceName() {
        basicProject.serviceName
    }

    String getServicePackage() {
        basicProject.servicePackage
    }

    String getServicePackagePath() {
        basicProject.servicePackagePath
    }

    BasicProject getBasicProject() {
        basicProject
    }

    void initRestProject(boolean shouldDisableAuthFilter, boolean vsts) {
        basicProject.initGradleProject()
        createRestBase(vsts)

        if (shouldDisableAuthFilter) {
            disableAuthFilter()
        } else {
            enableAuthFilter(vsts)
        }
    }

    private void enableAuthFilter(boolean vsts) {
        FileUtils.appendBeforeLine(basicProject.getBuildFile(), /compile "com.blackbaud:common-spring-boot-rest.*/,
                '    compile "com.blackbaud:tokens-client:3.+"')

        if (vsts) {
            File applicationProperties = basicProject.getProjectFileOrFail("src/main/resources/application.properties")
            applicationProperties << """\
bbauth.enabled=true
long.token.enabled=false
"""
        } else {
            File applicationClassFile = basicProject.findFile("${serviceName}.java")
            FileUtils.addImport(applicationClassFile, "import com.blackbaud.security.CoreSecurityEcosystemParticipantRequirementsProvider;")
            FileUtils.appendAfterLine(applicationClassFile, /public class .*/, """
    @Bean
    public CoreSecurityEcosystemParticipantRequirementsProvider coreSecurityEcosystemParticipantRequirementsProvider() {
        return new CoreSecurityEcosystemParticipantRequirementsProvider();
    }""")
        }

        basicProject.commitProjectFiles("enable auth filter")
    }

    private void disableAuthFilter() {
        File applicationProperties = basicProject.getProjectFileOrFail("src/main/resources/application.properties")
        applicationProperties << """
authorization.filter.enable=false
"""
        basicProject.commitProjectFiles("disable auth filter")
    }

    void initPostgres() {
        DatasourceProject datasourceProject = new DatasourceProject(this)
        datasourceProject.initPostgres()

        basicProject.commitProjectFiles("initialize postgres container")
    }

    void initMybatis() {
        DatasourceProject datasourceProject = new DatasourceProject(this)
        datasourceProject.initMybatis()

        basicProject.commitProjectFiles("initialize mybatis")
    }

    void initKafka() {
        KafkaProject kafkaProject = new KafkaProject(basicProject)
        kafkaProject.initKafka()

        basicProject.commitProjectFiles("initialize kafka")
    }

    private void createRestBase(boolean vsts) {
        basicProject.applyTemplate("src/main/java/${servicePackagePath}") {
            "${serviceName}.java" template: "/templates/springboot/application-class.java.tmpl",
                    serviceName: serviceName, servicePackage: servicePackage
        }

        basicProject.applyTemplate("src/main/resources") {
            "bootstrap.properties" template: "/templates/springboot/bootstrap.properties.tmpl", serviceId: "${serviceId}"
        }

        basicProject.applyTemplate("src/main/resources") {
            "bootstrap-cloud.properties" template: "/templates/springboot/bootstrap-cloud.properties.tmpl"
        }

        basicProject.applyTemplate("src/deploy/cloudfoundry") {
            "app-descriptor.yml" template: "/templates/deploy/app-descriptor.yml.tmpl"
        }

        basicProject.applyTemplate("src/componentTest/groovy/${servicePackagePath}") {
            "ComponentTest.java" template: "/templates/springboot/rest/component-test-annotation.java.tmpl",
                    serviceName: serviceName, packageName: servicePackage

            "ComponentTestConfig.java" template: "/templates/springboot/rest/application-test-config.java.tmpl",
                    className: "ComponentTestConfig", serviceName: serviceName, packageName: servicePackage
        }
        basicProject.applyTemplate("src/componentTest/groovy/com/blackbaud/swagger") {
            "GenerateSwaggerDocsSpec.groovy" template: "/templates/springboot/rest/generate-swagger-docs-spec.groovy.tmpl",
                     servicePackage: servicePackage
        }

        basicProject.applyTemplate("src/sharedTest/groovy/${servicePackagePath}/core") {
            "CoreARandom.java" template: "/templates/test/core-arandom.java.tmpl",
                    servicePackageName: servicePackage
            "CoreRandomBuilderSupport.java" template: "/templates/test/random-builder-support.java.tmpl",
                    packageName: "${servicePackage}.core", qualifier: "Core"
        }

        basicProject.applyTemplate {
            'build.gradle' template: "/templates/springboot/rest/build.gradle.tmpl",
                    servicePackageName: servicePackage
            'gradle.properties' template: "/templates/basic/gradle.properties.tmpl",
                    artifactId: serviceId
            'src' {
                'main' {
                    'resources' {
                        'application.properties' template: "/templates/springboot/rest/application.properties.tmpl",
                                                 resourcePackageName: "${servicePackage}.resources"
                        'logback.xml' template: "/templates/logback/logback.tmpl",
                                      includeFileName: vsts ? "common-vsts.xml" : "common.xml"
                    }
                }
                'test' {
                    "groovy" {}
                }
                'componentTest' {
                    'groovy' {}
                }
            }
        }

        basicProject.commitProjectFiles("springboot rest bootstrap")
    }

    void createCrudResource(String resourceName, boolean addEntity, boolean addWireSpec) {
        addResourceAndSupportingClasses(resourceName, addWireSpec)

        File resourceFile = basicProject.findFile("${resourceName}Resource.java")
        FileUtils.appendAfterLine(resourceFile, "class", """\

    @GetMapping("/{id}")
    public ${resourceName} find(@PathVariable("id") UUID id) {
        throw new IllegalStateException("implement");
    }
""")

        File clientFile = basicProject.findFile("${resourceName}Client.java")
        FileUtils.appendAfterLine(clientFile, "class", """\

    @RequestLine("GET /{id}")
    ${resourceName} find(@Param("id") UUID id);
""")

        addApiObject(resourceName)

        if (addEntity) {
            addEntityObject(resourceName)
        }
    }

    private void addResourceAndSupportingClasses(String resourceName, boolean addWireSpec) {
        String resourcePath = "${UPPER_CAMEL.to(LOWER_UNDERSCORE, resourceName)}"
        String resourceVarName = "${resourcePath.toUpperCase()}_PATH"
        String resourceNameLowerCamel = UPPER_CAMEL.to(LOWER_CAMEL, resourceName)

        addResourcePathConstant(resourcePath, resourceVarName)

        basicProject.applyTemplate("src/main/java/${servicePackagePath}/resources") {
            "${resourceName}Resource.java" template: "/templates/springboot/rest/resource.java.tmpl",
                                           resourceName: resourceName, servicePackage: "${servicePackage}", resourcePathVar: resourceVarName
        }
        basicProject.applyTemplate("src/main/java/${servicePackagePath}/client") {
            "${resourceName}Client.java" template: "/templates/springboot/rest/resource-client.java.tmpl",
                                         resourceName: resourceName, servicePackage: "${servicePackage}", resourcePathVar: resourceVarName
        }
        basicProject.applyTemplate("src/componentTest/groovy/${servicePackagePath}/resources") {
            "${resourceName}ResourceSpec.groovy" template: "/templates/springboot/rest/resource-spec.groovy.tmpl",
                                                 resourceName: resourceName, servicePackage: "${servicePackage}"
        }
        if (addWireSpec) {
            basicProject.applyTemplate("src/componentTest/groovy/${servicePackagePath}/resources") {
                "${resourceName}ResourceWireSpec.groovy" template: "/templates/springboot/rest/resource-wirespec.groovy.tmpl",
                                                         resourceName: resourceName, servicePackage: "${servicePackage}"
            }
        }
        File testConfig = basicProject.findOptionalFile("ComponentTestConfig.java")
        if (testConfig == null) {
            testConfig = basicProject.findFile("TestConfig.java")
        }
        FileUtils.appendAfterLine(testConfig, /import.*/,
                                  """import org.springframework.context.annotation.Bean;
import ${servicePackage}.client.${resourceName}Client;
"""
        )
        FileUtils.appendToClass(testConfig, """

    @Bean
    public ${resourceName}Client ${resourceNameLowerCamel}Client() {
        return testClientSupport.createClientWithTestToken(${resourceName}Client.class);
    }
""")
    }

    private void addResourcePathConstant(String resourcePath, String resourceVarName) {
        File resourcePathsFile = basicProject.getProjectFile("src/main/java/${servicePackagePath}/api/ResourcePaths.java")
        if (resourcePathsFile.exists() == false) {
            basicProject.applyTemplate("src/main/java/${servicePackagePath}/api") {
                'ResourcePaths.java' template: "/templates/springboot/rest/resource-paths.java.tmpl",
                        packageName: "${servicePackage}.api"
            }
        }

        FileUtils.appendToClass(resourcePathsFile, """
    public static final String ${resourceVarName} = "/${resourcePath}";
""")
    }

    void addEntityObject(String resourceName) {
        String resourcePath = "${UPPER_CAMEL.to(LOWER_UNDERSCORE, resourceName)}"
        String resourceNameLowerCamel = UPPER_CAMEL.to(LOWER_CAMEL, resourceName)

        basicProject.applyTemplate("src/main/java/${servicePackagePath}/core/domain") {
            "${resourceName}Entity.java" template: "/templates/springboot/rest/resource-entity.java.tmpl",
                                         resourceName: resourceName, packageName: "${servicePackage}.core.domain", tableName: resourcePath
        }

        basicProject.applyTemplate("src/main/java/${servicePackagePath}/core/domain") {
            "${resourceName}Repository.java" template: "/templates/springboot/rest/resource-repository.java.tmpl",
                                             resourceName: resourceName, packageName: "${servicePackage}.core.domain"
        }

        basicProject.applyTemplate("src/sharedTest/groovy/${servicePackagePath}/core/domain") {
            "Random${resourceName}EntityBuilder.groovy" template: "/templates/test/random-core-builder.groovy.tmpl",
                                                        resourceName: resourceName,
                                                        resourceNameLowerCamel: resourceNameLowerCamel,
                                                        servicePackageName: servicePackage
        }

        File randomCoreBuilderSupport = basicProject.findFile("CoreRandomBuilderSupport.java")
        FileUtils.appendAfterLine(randomCoreBuilderSupport, "package", "import ${servicePackage}.core.domain.${resourceName}Repository;")
        FileUtils.appendAfterLine(randomCoreBuilderSupport, "package", "import ${servicePackage}.core.domain.Random${resourceName}EntityBuilder;")
        FileUtils.appendToClass(randomCoreBuilderSupport, """

    @Autowired
    private ${resourceName}Repository ${resourceNameLowerCamel}Repository;

    public Random${resourceName}EntityBuilder ${resourceNameLowerCamel}Entity() {
        return new Random${resourceName}EntityBuilder(${resourceNameLowerCamel}Repository);
    }
""")

        DatasourceProject datasourceProject = new DatasourceProject(this)
        datasourceProject.addCreateTableScript(resourcePath)
    }

    void addApiObject(String resourceName, boolean upperCamel = false) {
        basicProject.addApiObject("rest", resourceName, upperCamel)
    }

    void createBasicResource(String resourceName, boolean addWireSpec) {
        addResourceAndSupportingClasses(resourceName, addWireSpec)
    }

}
