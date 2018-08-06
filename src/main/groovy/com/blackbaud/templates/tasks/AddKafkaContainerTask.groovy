package com.blackbaud.templates.tasks

import com.blackbaud.templates.project.BasicProject
import com.blackbaud.templates.project.KafkaProject
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
