package com.blackbaud.templates.tasks


class IntegrationTestProject {

    private BasicProject basicProject

    IntegrationTestProject(BasicProject basicProject) {
        this.basicProject = basicProject
    }

    String getRepoName() {
        basicProject.repoName
    }

    String getRepoDir() {
        basicProject.repoDir
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

    void initIntegrationTestProject() {
        basicProject.initBlackbaudGradleWrapper()

        basicProject.applyTemplate {
            'build.gradle' template: "/templates/springboot/integrationtest/build.gradle.tmpl",
                           artifactId: repoName
            'gradle.properties' template: "/templates/basic/gradle.properties.tmpl",
                                artifactId: repoName
            'src' {
                'integrationTest' {
                    'resources' {
                        'application-integrationTest.properties' template: "/templates/springboot/integrationtest/application-integrationTest.properties.tmpl",
                                                                 artifactId: repoName
                        'logback.xml' template: "/templates/logback/logback.tmpl"
                    }
                }
                'integrationTest' {
                    "groovy" {}
                }
            }
        }

        basicProject.applyTemplate("src/integrationTest/groovy/${servicePackagePath}") {
            "IntegrationTest.java" template: "/templates/springboot/integrationtest/integration-test-annotation.java.tmpl",
                    serviceName: serviceName, packageName: servicePackage

            "TestConfig.java" template: "/templates/springboot/rest/application-test-config.java.tmpl",
                    className: "IntegrationTestConfig", serviceName: serviceName, packageName: servicePackage
        }

        basicProject.applyTemplate("src/integrationTest/groovy/${servicePackagePath}/core") {
            "CoreARandom.java" template: "/templates/test/core-arandom.java.tmpl",
                    servicePackageName: servicePackage
            "CoreRandomBuilderSupport.java" template: "/templates/test/random-builder-support.java.tmpl",
                    packageName: "${servicePackage}.core", qualifier: "Core"
        }

        basicProject.commitProjectFiles("integration test bootstrap")
    }

}
