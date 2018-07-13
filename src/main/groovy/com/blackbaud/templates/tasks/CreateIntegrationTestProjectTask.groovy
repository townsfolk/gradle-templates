package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction

class CreateIntegrationTestProjectTask extends AbstractTemplateTask {

    CreateIntegrationTestProjectTask() {
        super("Create an integration test project (options: -PrepoName=?, [-Pvsts -Pclean])")
    }

    @TaskAction
    void createIntegrationTestProject() {
        boolean clean = projectProps.isPropertyDefined("clean")
        boolean vsts = projectProps.isPropertyDefined("vsts")
        BasicProject basicProject = createBasicProject(clean)
        IntegrationTestProject integrationTestProject = new IntegrationTestProject(basicProject)
        integrationTestProject.initIntegrationTestProject(vsts)
    }

}
