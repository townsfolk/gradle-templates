package templates.tasks


class KafkaProject {

    private BasicProject basicProject

    KafkaProject(BasicProject basicProject) {
        this.basicProject = basicProject
    }

    void initKafka() {
        basicProject.addDockerPlugin()
        basicProject.applyPlugin("kafka")

        File componentTestPropertiesFile = basicProject.getProjectFile("src/componentTest/resources/application-componentTest.properties")
        componentTestPropertiesFile.append("""
kafka.consumer.groupId=${basicProject.repoName}-test
""")

        File applicationPropertiesFile = basicProject.getProjectFile("src/main/resources/application-local.properties")
        applicationPropertiesFile.append("""
kafka.zkNodes=local.docker
kafka.defaultZkPort=2181
kafka.schemaRegHosts=local.docker
kafka.schemaRegPort=8081
kafka.brokers=local.docker
kafka.defaultBrokerPort=9096
kafka.sessionTimeout=10000

kafka.consumer.groupId=${basicProject.repoName}-local
kafka.consumer.sessionTimeout=10000
""")
    }

}
