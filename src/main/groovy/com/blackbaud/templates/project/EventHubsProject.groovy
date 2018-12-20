package com.blackbaud.templates.project

import com.blackbaud.templates.CurrentVersions

class EventHubsProject {

    private BasicProject basicProject

    EventHubsProject(BasicProject basicProject) {
        this.basicProject = basicProject
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

    void initEventHubsIfNotAlreadyInitialized(String name) {
        BuildFile buildFile = basicProject.buildFile
        String lowerCaseName = name.toLowerCase()

        if (buildFile.text =~ /common-async-event-hubs/) {
            return
        }

        buildFile.applyPlugin("async-stub")
        buildFile.appendAfterLine(/commonSpringBootVersion\s*=\s*"\$\{springBootVersion}/,
                                      "        commonAsyncEventHubsVersion = \"${CurrentVersions.COMMON_SERVICE_BUS_MAJOR_VERSION}.+\"")
        buildFile.appendAfterLine(/compile.*common-spring-boot/,
                '    compile "com.blackbaud:common-async-event-hubs:${commonAsyncEventHubsVersion}"')
        buildFile.appendAfterLine(/sharedTestCompile/,
                '    sharedTestCompile "com.blackbaud:common-async-event-hubs-test:${commonAsyncEventHubsVersion}"')

        File componentTestPropertiesFile = basicProject.getProjectFile("src/componentTest/resources/application-componentTest.properties")
        componentTestPropertiesFile.append("""
eventhubs.${lowerCaseName}.name=test${name}hub
eventhubs.${lowerCaseName}.namespace=test${name}namespace
eventhubs.${lowerCaseName}.sasKey=testsaskey
eventhubs.${lowerCaseName}.sasKeyName=testsaskeyname
eventhubs.${lowerCaseName}.storageAccountContainer=testcontainer

eventhubs.consumer.defaults.maxBatchSize=100
eventhubs.consumer.defaults.consumerGroupName=\$Default
eventhubs.consumer.defaults.storageAccountName=eventhubcommits
eventhubs.consumer.defaults.storageAccountKey=anotherFakeKey

eventhubs.stub=true
""")

        File applicationPropertiesFile = basicProject.getProjectFile("src/main/resources/application-local.properties")
        applicationPropertiesFile.append("""
eventhubs.${lowerCaseName}.name=${name}
eventhubs.${lowerCaseName}.namespace=
eventhubs.${lowerCaseName}.sasKey=fakeKey
eventhubs.${lowerCaseName}.sasKeyName=RootManageSharedAccessKey
eventhubs.${lowerCaseName}.storageAccountContainer=testcontainer

eventhubs.consumer.defaults.maxBatchSize=100
eventhubs.consumer.defaults.consumerGroupName=\$Default
eventhubs.consumer.defaults.storageAccountName=eventhubcommits
eventhubs.consumer.defaults.storageAccountKey=anotherFakeKey

eventhubs.stub=true
""")
    }

    void addExternalApiObject(String resourceName) {
        basicProject.addExternalApiObject("eventhubs", resourceName, false)
    }

    void addInternalApiObject(String resourceName) {
        basicProject.addInternalApiObject("eventhubs", resourceName, false)
    }

}
