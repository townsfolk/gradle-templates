package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction

class AddKafkaMessageTask extends AbstractTemplateTask {

    AddKafkaMessageTask() {
        super("Adds a Kafka message and random builder skeleton (options: -Pname=?)")
    }

    @TaskAction
    void addApiObject() {
        String name = projectProps.getRequiredProjectProperty("name")
        BasicProject basicProject = openBasicProject()
        KafkaProject kafkaProject = new KafkaProject(basicProject)
        kafkaProject.addApiObject(name)
    }

}
