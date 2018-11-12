package com.blackbaud.templates.tasks

import com.blackbaud.templates.project.BasicProject
import com.blackbaud.templates.project.RestProject
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
        BasicProject basicProject = openBasicProject()
        basicProject.addConsumerPact(providerServiceName, objectTypeReturnedByProvider, sasProviderService)
    }
}
