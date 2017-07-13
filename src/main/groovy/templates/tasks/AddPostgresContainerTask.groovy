package templates.tasks

import org.gradle.api.tasks.TaskAction


class AddPostgresContainerTask extends AbstractTemplateTask {

    AddPostgresContainerTask() {
        super("Add a Postgres container and default configuration to an existing project")
    }

    @TaskAction
    void addPostgresContainer() {
        BasicProject basicProject = openBasicProject()
        DatasourceProject datasourceProject = new DatasourceProject(basicProject)
        datasourceProject.initPostgres()
    }

}
