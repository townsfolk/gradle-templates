package templates.tasks

import org.gradle.api.tasks.TaskAction


class CreateEmbeddedServiceTask extends AbstractTemplateTask {

    CreateEmbeddedServiceTask() {
        super("Create a SpringBoot REST embedded service (options: -PserviceName=?)")
    }

    @TaskAction
    void createRestEmbeddedService() {
        String serviceName = getServiceName()
        RestProject restProject = openRestProject(serviceName)
        restProject.createEmbeddedService()
    }

    private String getServiceName() {
        String resourceName = projectProps.getRequiredProjectProperty("serviceName")
        "${resourceName.capitalize()}"
    }

}
