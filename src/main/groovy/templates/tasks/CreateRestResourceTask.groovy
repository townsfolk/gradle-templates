package templates.tasks

class CreateRestResourceTask extends AbstractTemplateTask {

    CreateRestResourceTask() {
        super("Create a SpringBoot REST resource (options: -PresourceName=?)")
    }

    @Override
    protected void renderTemplate() {
        String resourceName = getResourceName()
        RestProject restProject = openRestProject()

        restProject.createRestResource(resourceName)
    }

    private String getResourceName() {
        String resourceName = projectProps.getRequiredProjectProperty("resourceName")
        "${resourceName.capitalize()}"
    }

}
