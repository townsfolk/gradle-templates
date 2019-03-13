package com.blackbaud.templates.tasks

import com.blackbaud.templates.project.PactProject
import org.gradle.api.tasks.TaskAction

class AddProviderPactTask extends AbstractTemplateTask {

    AddProviderPactTask() {
        super("Initializes the project as a Pact provider")
    }

    @TaskAction
    void addProviderPact() {
        PactProject pactProject = new PactProject(openBasicProject())
        pactProject.addProviderPact()
    }

}
