package com.blackbaud.templates.tasks


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

    void initEventHubs(String name) {
        FileUtils.appendAfterLine(basicProject.getBuildFile(), /commonSpringBootVersion\s*=\s*"\$\{springBootVersion}/,
                                      '        commonEventHubsVersion = "2.+"')
        FileUtils.appendAfterLine(basicProject.getBuildFile(), /compile.*common-spring-boot/,
                '    compile "com.blackbaud:common-async-event-hubs:${commonEventHubsVersion}"')
        FileUtils.appendAfterLine(basicProject.getBuildFile(), /sharedTestCompile/,
                '    sharedTestCompile "com.blackbaud:common-async-event-hubs-test:${commonEventHubsVersion}"')

        File componentTestPropertiesFile = basicProject.getProjectFile("src/componentTest/resources/application-componentTest.properties")
        componentTestPropertiesFile.append("""
eventhubs.${name}.name=test${name}hub
eventhubs.${name}.namespace=test${name}namespace
eventhubs.${name}.sasKey=testsaskey
eventhubs.${name}.sasKeyName=testsaskeyname
eventhubs.${name}.storageAccountContainer=testcontainer

eventhubs.consumer.defaults.maxBatchSize=100
eventhubs.consumer.defaults.consumerGroupName=$Default
eventhubs.consumer.defaults.storageAccountName=eventhubcommits
eventhubs.consumer.defaults.storageAccountKey=anotherFakeKey

eventhubs.stub=true
""")

        File applicationPropertiesFile = basicProject.getProjectFile("src/main/resources/application-local.properties")
        applicationPropertiesFile.append("""
eventhubs.${name}.name=${name}
eventhubs.${name}.namespace=
eventhubs.${name}.sasKey=fakeKey
eventhubs.${name}.sasKeyName=RootManageSharedAccessKey
eventhubs.${name}.storageAccountContainer=testcontainer

eventhubs.consumer.defaults.maxBatchSize=100
eventhubs.consumer.defaults.consumerGroupName=$Default
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
