package templates.tasks

class CreateRestProjectTask extends AbstractTemplateTask {

    CreateRestProjectTask() {
        super("Create a SpringBoot REST project (options: -PrepoName=?, [-Pclean]")
    }

    @Override
    protected void renderTemplate() {
        boolean clean = projectProps.isPropertyDefined("clean")
        BasicProject basicProject = createBasicProject(clean)
        RestProject restProject = new RestProject(basicProject)

        restProject.initRestProject()
    }

}
