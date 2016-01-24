package templates.tasks

import org.gradle.api.tasks.TaskAction

class CreateRestResourceTask extends AbstractTemplateTask {

    CreateRestResourceTask() {
        super("Create a SpringBoot REST resource (options: -PresourceName=?)")
    }

    @TaskAction
    void createRestResource() {
        String resourceName = getResourceName()
        RestProject restProject = openRestProject()
        restProject.createRestResource(resourceName)
    }

    private String getResourceName() {
        String resourceName = projectProps.getRequiredProjectProperty("resourceName")
        "${resourceName.capitalize()}"
    }

}
