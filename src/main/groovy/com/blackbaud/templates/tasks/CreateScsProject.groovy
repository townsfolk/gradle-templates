package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction

class CreateScsProject extends AbstractTemplateTask {

    CreateScsProject() {
        super("Create an SCS project (options: -PrepoName=?, [-Pclean])")
    }

    @TaskAction
    void createScsProject() {
        boolean clean = projectProps.isPropertyDefined("clean")
        BasicProject basicProject = createBasicProject(clean)

        basicProject.initBlackbaudGradleWrapper()
        basicProject.applyTemplate {
            'build.gradle' template: "/templates/scs/build.gradle.tmpl"
            'gateway.yaml' template: "/templates/scs/gateway.yaml.tmpl"
        }
        basicProject.gitRepo.commitProjectFiles("added build.gradle and gateway.yaml")
    }

}
