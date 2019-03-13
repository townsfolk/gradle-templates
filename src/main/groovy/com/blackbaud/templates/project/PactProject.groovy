package com.blackbaud.templates.project

import static com.google.common.base.CaseFormat.LOWER_CAMEL
import static com.google.common.base.CaseFormat.LOWER_HYPHEN
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE
import static com.google.common.base.CaseFormat.UPPER_CAMEL

class PactProject {

    private BasicProject basicProject

    PactProject(BasicProject basicProject) {
        this.basicProject = basicProject
    }

    private String getRepoName() {
        basicProject.repoName
    }

    private String getServiceName() {
        basicProject.serviceName
    }

    private String getServicePackage() {
        basicProject.servicePackage
    }

    private String getServicePackagePath() {
        basicProject.servicePackagePath
    }

    private ProjectFile getBuildFile() {
        basicProject.buildFile
    }

    void addConsumerPact(String providerServiceName, String objectTypeReturnedByProvider, boolean sasProviderService) {
        addPactDependenciesAndPlugin(sasProviderService)
        addConsumerPactSpec(providerServiceName, objectTypeReturnedByProvider, sasProviderService)
    }

    void addProviderPact() {
        addPactDependenciesAndPlugin(false)
        addProviderPactSpecAndStateInitializer()
    }

    private addPactDependenciesAndPlugin(boolean sasProviderService) {
        if (buildFile.text =~ /com.blackbaud:pact-gradle-plugin:/) {
            return
        }
        buildFile.appendAfterLine("classpath \"com.blackbaud:gradle-internal:\\d.+\"", """\
        classpath \"com.blackbaud:pact-gradle-plugin:1.+\"\
""")
        basicProject.applyPlugin("pact")

        buildFile.addDependency("sharedTestCompile", 'com.blackbaud:common-deployable-spring-boot-rest-test:${commonSpringBootVersion}')

        if (sasProviderService) {
            buildFile.addDependency("sharedTestCompile", "com.blackbaud:sasquatch-test:2.+")
        }
    }

    private addConsumerPactSpec(String providerServiceName, String objectTypeReturnedByProvider, boolean sasProviderService) {
        basicProject.applyTemplate("src/test/groovy/${servicePackagePath}/pact") {
            "${LOWER_HYPHEN.to(UPPER_CAMEL, providerServiceName)}PactSpec.groovy" template: "/templates/test/pact/sas-consumer-spec.groovy.tmpl",
                                                        packageName: "${servicePackage}",
                                                        consumerServiceName: serviceName,
                                                        objectName: objectTypeReturnedByProvider,
                                                        providerServiceNameUpperCamelCase: LOWER_HYPHEN.to(UPPER_CAMEL, providerServiceName),
                                                        providerServiceName: providerServiceName,
                                                        providerServiceNameLowerCamelCase: LOWER_HYPHEN.to(LOWER_CAMEL, providerServiceName),
                                                        objectNameLowerUnderscore: UPPER_CAMEL.to(LOWER_UNDERSCORE, objectTypeReturnedByProvider),
                                                        sasProviderService: sasProviderService
        }
    }

    private addProviderPactSpecAndStateInitializer() {
        String pactSrcPath = "src/componentTest/groovy/${servicePackagePath}/pact"

        basicProject.applyTemplate(pactSrcPath) {
            "ProviderVerificationSpec.groovy" template: "/templates/test/pact/provider-spec.groovy.tmpl",
                                              packageName: servicePackage,
                                              pactServiceName: repoName
        }

        basicProject.applyTemplate(pactSrcPath) {
            "ProviderStateInitializer.groovy" template: "/templates/test/pact/provider-state-initializer.groovy.tmpl",
                                              packageName: servicePackage
        }

        ProjectFile testConfig = basicProject.findOptionalFile("ComponentTestConfig.java")
        if (testConfig != null) {
            testConfig.addImport("${servicePackage}.pact.ProviderStateInitializer")
            testConfig.appendToClass("""
    @Bean
    public ProviderStateInitializer providerStateInitializer() {
        return new ProviderStateInitializer();
    }
""")
        }
    }

}
