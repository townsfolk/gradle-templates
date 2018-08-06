package com.blackbaud.templates.tasks

import com.blackbaud.templates.project.AsyncProject
import com.blackbaud.templates.project.BasicProject
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction


class AddServiceBusTopicConfigTask extends AbstractTemplateTask {

    AddServiceBusTopicConfigTask() {
        super("Adds a Service Bus topic configuration, message, and random builder skeleton " +
                      "(options: -Ptopic=?, -PtopicType=[datasync, schedule] one of [-Pinternal -Pconsumer -Ppublisher], [-PsessionEnabled])")
    }

    @TaskAction
    void addTopic() {
        String name = projectProps.getRequiredProjectProperty("topic")
        String topicTypeString = projectProps.getRequiredProjectProperty("topicType")
        AsyncProject.TopicType topicType = AsyncProject.TopicType.resolveFromString(topicTypeString)
        boolean internal = projectProps.isPropertyDefined("internal")
        boolean consumer = projectProps.isPropertyDefined("consumer")
        boolean publisher = projectProps.isPropertyDefined("publisher")
        boolean sessionEnabled = projectProps.isPropertyDefined("sessionEnabled")
        assertMutallyExclusive(internal, consumer, publisher)
        BasicProject basicProject = openBasicProject()
        AsyncProject asyncProject = new AsyncProject(basicProject)
        if (internal) {
            asyncProject.addInternalTopic(name, topicType, sessionEnabled)
        } else {
            asyncProject.addExternalTopic(name, topicType, consumer, publisher, sessionEnabled)
        }
    }

    private void assertMutallyExclusive(boolean internal, boolean consumer, boolean publisher) {
        boolean fail
        if (internal) {
            fail = consumer || publisher
        } else if (consumer) {
            fail = internal || publisher
        } else if (publisher) {
            fail = internal || consumer
        } else {
            fail = true
        }
        if (fail) {
            throw new GradleException("Exactly one of internal/consumer/publisher must be true, " +
                                              "got internal=${internal}, consumer=${consumer}, publisher=${publisher}")
        }
    }

}
