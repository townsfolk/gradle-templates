package templates.tasks

class CreateRestResourceTask extends AbstractTemplateTask {

    CreateRestResourceTask() {
        super("Create a SpringBoot REST resource (options: -PrepoName=?, -PresourceName=?)")
    }

    @Override
    protected void renderTemplate() {
        RestProject restProject = RestProject.create(project)
        String resourceName = projectProps.getRequiredProjectProperty("resourceName").capitalize()
        restProject.createRestResource(resourceName)
    }

}
