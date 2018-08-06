package com.blackbaud.templates.tasks

import com.blackbaud.templates.project.RestProject
import org.gradle.api.tasks.TaskAction


class AddJpaObjectTask extends AbstractTemplateTask {

    AddJpaObjectTask() {
        super("Adds a JPA entity object and random builder skeleton (options: -Pname=?)")
    }

    @TaskAction
    void addEntityObject() {
        String name = projectProps.getRequiredProjectProperty("name")
        RestProject restProject = openRestProject()
        restProject.addJpaEntityObject(name)
    }

}
