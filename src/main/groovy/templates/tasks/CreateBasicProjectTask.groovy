package templates.tasks

import templates.TemplatesPlugin

class CreateBasicProjectTask extends AbstractTemplateTask {

	CreateBasicProjectTask() {
		super("create a basic project")
		group = TemplatesPlugin.group
	}

	@Override
	protected void renderTemplate() {
		BasicProject basicProject = BasicProject.create(project)
		basicProject.initGradleProject()
	}

}
