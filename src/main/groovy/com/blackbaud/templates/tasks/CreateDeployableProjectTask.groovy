package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction

class CreateDeployableProjectTask extends AbstractTemplateTask {

    CreateDeployableProjectTask() {
        super("Create a SpringBoot REST project (options: -PrepoName=?, [-Pclean, -Ppostgres, -Pmybatis, -Pkafka, -PdisableAuthFilter -PserviceName=<app name>, -PservicePackageName=<package name>])")
    }

    @TaskAction
    void createDeployableProject() {
        boolean clean = projectProps.isPropertyDefined("clean")
        boolean disableAuthFilter = projectProps.isPropertyDefined("disableAuthFilter")
        BasicProject basicProject = createBasicProject(clean)
        RestProject restProject = new RestProject(basicProject)
        restProject.initRestProject(disableAuthFilter)
        if (projectProps.isPropertyDefined("mybatis")) {
            restProject.initPostgres()
            restProject.initMybatis()
        } else if (projectProps.isPropertyDefined("postgres")) {
            restProject.initPostgres()
        }
        if (projectProps.isPropertyDefined("kafka")) {
            restProject.initKafka()
        }
    }

}
