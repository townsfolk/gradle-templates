package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction


class AddCosmosObjectTask extends AbstractTemplateTask {

    AddCosmosObjectTask() {
        super("Adds a Mongo entity object and random builder skeleton (options: -Pname=?, -Pauditable)")
    }

    @TaskAction
    void addEntityObject() {
        boolean auditable = projectProps.isPropertyDefined("auditable")
        String name = projectProps.getRequiredProjectProperty("name")
        RestProject restProject = openRestProject()
        restProject.addCosmosEntityObject(name, auditable)
    }

}
