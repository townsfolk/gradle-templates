package com.blackbaud.templates.tasks


import com.blackbaud.templates.project.PactProject
import org.gradle.api.tasks.TaskAction

class AddConsumerPactTask extends AbstractTemplateTask {

    AddConsumerPactTask() {
        super("Adds a pact between this consumer service and the specified provider service " +
                      "(options: -PproviderServiceName=?, =PobjectTypeReturnedByProvider=? [-PsasProviderService])")
    }

    @TaskAction
    void addConsumerPact() {
        String providerServiceName = projectProps.getRequiredProjectProperty("providerServiceName")
        String objectTypeReturnedByProvider = projectProps.getRequiredProjectProperty("objectTypeReturnedByProvider")
        boolean sasProviderService = projectProps.isPropertyDefined("sasProviderService")
        PactProject pactProject = new PactProject(openBasicProject())
        pactProject.addConsumerPact(providerServiceName, objectTypeReturnedByProvider, sasProviderService)
    }
}
