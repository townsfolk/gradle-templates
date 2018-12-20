package com.blackbaud.templates.tasks

import com.blackbaud.templates.project.BasicProject
import com.blackbaud.templates.project.IntegrationTestProject
import org.gradle.api.tasks.TaskAction

class CreateIntegrationTestProjectTask extends AbstractTemplateTask {

    CreateIntegrationTestProjectTask() {
        super("Create an integration test project (options: -PrepoName=?, [-Pclean, -Pgeb])")
    }

    @TaskAction
    void createIntegrationTestProject() {
        boolean clean = projectProps.isPropertyDefined("clean")
        boolean includeGeb = projectProps.isPropertyDefined("geb")
        BasicProject basicProject = createBasicProject(clean)
        IntegrationTestProject integrationTestProject = new IntegrationTestProject(basicProject)
        integrationTestProject.initIntegrationTestProject(includeGeb)
    }

}
