package templates.tasks

import templates.TemplatesPlugin

class CreateBasicProjectTask extends AbstractTemplateTask {

    CreateBasicProjectTask() {
        super("Create a basic project (options: -PrepoName=?, [-Pclean]")
        group = TemplatesPlugin.group
    }

    @Override
    protected void renderTemplate() {
        boolean clean = projectProps.isPropertyDefined("clean")
        BasicProject basicProject = createBasicProject(clean)

        basicProject.initGradleProject()
    }

}
