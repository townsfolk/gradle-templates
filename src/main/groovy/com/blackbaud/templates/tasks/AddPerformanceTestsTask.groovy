package com.blackbaud.templates.tasks

import org.gradle.api.tasks.TaskAction

class AddPerformanceTestsTask extends AbstractTemplateTask {

    AddPerformanceTestsTask() {
        super("Add performance tests submodule and default configuration to an existing project");
    }

    @TaskAction
    void addPerformanceTests() {
        BasicProject basicProject = openBasicProject()
        PerformanceTestsProject performanceTestsProject = new PerformanceTestsProject(basicProject)
        performanceTestsProject.initPerformanceTests()
    }

}
