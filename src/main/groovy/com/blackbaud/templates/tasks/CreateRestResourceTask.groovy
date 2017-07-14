package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction

class CreateRestResourceTask extends AbstractTemplateTask {

    CreateRestResourceTask() {
        super("Create a SpringBoot REST resource (options: -PresourceName=?, [-PsuppressEntity])")
    }

    @TaskAction
    void createRestResource() {
        boolean addEntity = projectProps.isPropertyDefined("suppressEntity") == false
        String resourceName = getResourceName()
        RestProject restProject = openRestProject()
        restProject.createRestResource(resourceName, addEntity)
    }

    private String getResourceName() {
        String resourceName = projectProps.getRequiredProjectProperty("resourceName")
        "${resourceName.capitalize()}"
    }

}
