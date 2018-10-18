package com.blackbaud.templates.project

class PerformanceTestsProject {

    private BasicProject basicProject

    PerformanceTestsProject(BasicProject basicProject) {
        this.basicProject = basicProject
    }

    void initPerformanceTests() {
        basicProject.initPerformanceTestProject()
        File ignoreFile = basicProject.getProjectFileOrFail(".gitignore")
        ignoreFile.append("""

# Performance test results
results/
""")
    }

}
