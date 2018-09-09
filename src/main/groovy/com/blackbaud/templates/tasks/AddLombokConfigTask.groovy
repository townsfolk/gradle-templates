package com.blackbaud.templates.tasks

import com.blackbaud.templates.project.BasicProject
import org.gradle.api.tasks.TaskAction

class AddLombokConfigTask extends AbstractTemplateTask {

    AddLombokConfigTask() {
        super("Initialize lombok.config in project root directory")
    }

    @TaskAction
    void addLombokConfig() {
        BasicProject basicProject = openBasicProject()
        basicProject.addLombokConfig()
    }

}
