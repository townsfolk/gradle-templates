package com.blackbaud.templates.tasks;

class PerformanceTestsProject {

    private BasicProject basicProject

    PerformanceTestsProject(BasicProject basicProject) {
        this.basicProject = basicProject
    }

    void initPerformanceTests() {
        basicProject.initPerformanceTestProject()
        File ignoreFile = basicProject.getProjectFileOrFail(".gitignore")
        ignoreFile.append("\n\n# Performance test results\nresults/")
    }

}
