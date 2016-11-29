package templates.tasks

import org.gradle.api.tasks.TaskAction


class AddKafkaContainerTask extends AbstractTemplateTask {

    AddKafkaContainerTask() {
        super("Add a Kafka container and default configuration to an existing project")
    }

    @TaskAction
    void addKafkaContainer() {
        BasicProject basicProject = openBasicProject()
        KafkaProject kafkaProject = new KafkaProject(basicProject)
        kafkaProject.initKafka()
    }

}
