package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction

class CreateCrudResourceTask extends AbstractTemplateTask {

    CreateCrudResourceTask() {
        super("Create a SpringBoot CRUD resource (options: -PresourceName=?, [-PsuppressEntity, -PaddWireSpec])")
    }

    @TaskAction
    void createCrudResource() {
        boolean addEntity = projectProps.isPropertyDefined("suppressEntity") == false
        boolean addWireSpec = projectProps.isPropertyDefined("addWireSpec")
        String resourceName = getResourceName()
        RestProject restProject = openRestProject()
        restProject.createCrudResource(resourceName, addEntity, addWireSpec)
    }

    private String getResourceName() {
        String resourceName = projectProps.getRequiredProjectProperty("resourceName")
        "${resourceName.capitalize()}"
    }

}
