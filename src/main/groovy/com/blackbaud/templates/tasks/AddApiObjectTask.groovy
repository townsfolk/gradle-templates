package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction

class AddApiObjectTask extends AbstractTemplateTask {

    AddApiObjectTask() {
        super("Adds a JSON api object and random builder skeleton (options: -Pname=?, [-PupperCamel])")
    }

    @TaskAction
    void addApiObject() {
        boolean upperCamel = projectProps.isPropertyDefined("upperCamel")
        String name = projectProps.getRequiredProjectProperty("name")
        RestProject restProject = openRestProject()
        restProject.addApiObject(name, upperCamel)
    }

}
