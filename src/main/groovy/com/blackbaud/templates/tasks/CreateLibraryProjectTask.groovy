package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction

class CreateLibraryProjectTask extends AbstractTemplateTask {

    CreateLibraryProjectTask() {
        super("Create a library project (options: -PrepoName=?, [-Pclean])")
    }

    @TaskAction
    void createLibraryProject() {
        boolean clean = projectProps.isPropertyDefined("clean")
        BasicProject basicProject = createBasicProject(clean)

        basicProject.initGradleProject()
    }

}
