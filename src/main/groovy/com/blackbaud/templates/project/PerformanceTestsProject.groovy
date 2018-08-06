package com.blackbaud.templates.project

import com.blackbaud.templates.project.BasicProject;

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
