package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction


class AddPostgresContainerTask extends AbstractTemplateTask {

    AddPostgresContainerTask() {
        super("Add a Postgres container and default configuration to an existing project (options: -Pmybatis)")
    }

    @TaskAction
    void addPostgresContainer() {
        RestProject restProject = openRestProject()
        DatasourceProject datasourceProject = new DatasourceProject(restProject)
        datasourceProject.initPostgres()
        if (projectProps.isPropertyDefined("mybatis")) {
            datasourceProject.initMybatis()
        }
    }

}
