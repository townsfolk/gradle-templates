package com.blackbaud.templates.tasks

import com.blackbaud.templates.project.RestProject
import org.gradle.api.tasks.TaskAction

class AddRestApiObjectTask extends AbstractTemplateTask {

    AddRestApiObjectTask() {
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
