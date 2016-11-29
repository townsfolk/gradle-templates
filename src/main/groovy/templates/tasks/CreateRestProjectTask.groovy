package templates.tasks

import org.gradle.api.tasks.TaskAction

class CreateRestProjectTask extends AbstractTemplateTask {

    CreateRestProjectTask() {
        super("Create a SpringBoot REST project (options: -PrepoName=?, [-Pclean, -Ppostgres, -Pkafka])")
    }

    @TaskAction
    void createRestProject() {
        boolean clean = projectProps.isPropertyDefined("clean")
        BasicProject basicProject = createBasicProject(clean)
        RestProject restProject = new RestProject(basicProject, basicProject.serviceName)
        restProject.initRestProject()
        if (projectProps.isPropertyDefined("postgres")) {
            restProject.initPostgres()
        }
        if (projectProps.isPropertyDefined("kafka")) {
            restProject.initKafka()
        }
    }

}
