package templates.tasks

import templates.BlackbaudTemplatesPlugin

class CreateBasicProjectTask extends AbstractTemplateTask {

    CreateBasicProjectTask() {
        super("Create a basic project (options: -PrepoName=?, [-Pclean])")
        group = BlackbaudTemplatesPlugin.GROUP
    }

    @Override
    protected void renderTemplate() {
        boolean clean = projectProps.isPropertyDefined("clean")
        BasicProject basicProject = createBasicProject(clean)

        basicProject.initGradleProject()
    }

}
