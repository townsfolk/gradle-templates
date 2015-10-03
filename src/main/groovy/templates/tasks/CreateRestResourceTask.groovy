package templates.tasks

class CreateRestResourceTask extends AbstractTemplateTask {

    CreateRestResourceTask() {
        super("Create a SpringBoot REST resource (options: -PrepoName=?, -PresourceName=?)")
    }

    @Override
    protected void renderTemplate() {
        String resourceName = getResourceName()
        BasicProject basicProject = openBasicProject()
        RestProject restProject = new RestProject(basicProject)

        restProject.createRestResource(resourceName)
    }

    private String getResourceName() {
        String resourceName = projectProps.getRequiredProjectProperty("resourceName")
        resourceName.capitalize()
    }

}
