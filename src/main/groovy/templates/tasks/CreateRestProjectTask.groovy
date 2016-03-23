package templates.tasks

import org.gradle.api.tasks.TaskAction

class CreateRestProjectTask extends AbstractTemplateTask {

    CreateRestProjectTask() {
        super("Create a SpringBoot REST project (options: -PrepoName=?, [-Pclean, -Ppostgres])")
    }

    @TaskAction
    void createRestProject() {
        boolean clean = projectProps.isPropertyDefined("clean")
        boolean postgres = projectProps.isPropertyDefined("postgres")
        BasicProject basicProject = createBasicProject(clean)
        RestProject restProject = new RestProject(basicProject, basicProject.serviceName)
        restProject.initRestProject(postgres)
    }

}
