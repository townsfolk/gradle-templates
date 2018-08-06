package com.blackbaud.templates.tasks

import com.blackbaud.templates.project.RestProject
import org.gradle.api.tasks.TaskAction

class CreateResourceTask extends AbstractTemplateTask {

    CreateResourceTask() {
        super("Create a SpringBoot resource (options: -PresourceName=?, [-PsuppressEntity, -PaddWireSpec])")
    }

    @TaskAction
    void createResource() {
        boolean addEntity = projectProps.isPropertyDefined("suppressEntity") == false
        boolean addWireSpec = projectProps.isPropertyDefined("addWireSpec")
        String resourceName = getResourceName()
        RestProject restProject = openRestProject()
        restProject.createResource(resourceName, addEntity, addWireSpec)
    }

    private String getResourceName() {
        String resourceName = projectProps.getRequiredProjectProperty("resourceName")
        "${resourceName.capitalize()}"
    }

}
