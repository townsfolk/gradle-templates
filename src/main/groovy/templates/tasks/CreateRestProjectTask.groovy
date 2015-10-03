package templates.tasks

class CreateRestProjectTask extends AbstractTemplateTask {

    CreateRestProjectTask() {
        super("Create a SpringBoot REST project (options: -PrepoName=?, [-Pclean]")
    }

    @Override
    protected void renderTemplate() {
        RestProject restProject = RestProject.create(project)
        restProject.initRestProject()
    }

}
