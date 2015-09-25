package templates.tasks

import templates.TemplatesPlugin

class CreateBasicProjectTask extends AbstractProjectTask {

	CreateBasicProjectTask() {
		super("create a basic project")
		group = TemplatesPlugin.group
	}

	@Override
	protected void execTask() {
		BasicProject basicProject = BasicProject.create(project)
		basicProject.initGradleProject()
	}

}
