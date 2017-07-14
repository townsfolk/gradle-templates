package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction


class CreateEmbeddedServiceTask extends AbstractTemplateTask {

    CreateEmbeddedServiceTask() {
        super("Create a SpringBoot REST embedded service (options: -PserviceName=?, [-PsuppressEntity])")
    }

    @TaskAction
    void createRestEmbeddedService() {
        boolean addEntity = projectProps.isPropertyDefined("suppressEntity") == false
        String serviceName = getServiceName()
        RestProject restProject = openRestProject(serviceName)
        restProject.createEmbeddedService(addEntity)
    }

    private String getServiceName() {
        String resourceName = projectProps.getRequiredProjectProperty("serviceName")
        "${resourceName.capitalize()}"
    }

}
