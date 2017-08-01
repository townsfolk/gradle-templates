package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction

class CreateBasicResourceTask extends AbstractTemplateTask {

    CreateBasicResourceTask() {
        super("Create a SpringBoot REST resource (options: -PresourceName=?, [-PaddWireSpec])")
    }

    @TaskAction
    void createBasicResource() {
        boolean addWireSpec = projectProps.isPropertyDefined("addWireSpec")
        String resourceName = getResourceName()
        RestProject restProject = openRestProject()
        restProject.createBasicResource(resourceName, addWireSpec)
    }

    private String getResourceName() {
        String resourceName = projectProps.getRequiredProjectProperty("resourceName")
        "${resourceName.capitalize()}"
    }

}
