package templates.tasks

import templates.BlackbaudTemplatesPlugin
import org.gradle.api.tasks.TaskAction

class CreateBasicProjectTask extends AbstractTemplateTask {

    CreateBasicProjectTask() {
        super("Create a basic project (options: -PrepoName=?, [-Pclean])")
        group = BlackbaudTemplatesPlugin.GROUP
    }

    @TaskAction
    void createBasicProject() {
        boolean clean = projectProps.isPropertyDefined("clean")
        BasicProject basicProject = createBasicProject(clean)

        basicProject.initGradleProject()
    }

}
