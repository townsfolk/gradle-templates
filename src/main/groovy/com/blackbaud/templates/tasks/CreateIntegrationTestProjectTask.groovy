package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction

class CreateIntegrationTestProjectTask extends AbstractTemplateTask {

    CreateIntegrationTestProjectTask() {
        super("Create an integration test project (options: -PrepoName=?, [-Pclean])")
    }

    @TaskAction
    void createIntegrationTestProject() {
        boolean clean = projectProps.isPropertyDefined("clean")
        BasicProject basicProject = createBasicProject(clean)
        IntegrationTestProject integrationTestProject = new IntegrationTestProject(basicProject)
        integrationTestProject.initIntegrationTestProject()
    }

}
