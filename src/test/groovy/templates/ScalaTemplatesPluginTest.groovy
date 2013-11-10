package templates

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class ScalaTemplatesPluginTest {

    @Test void 'apply'(){
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'templates'

        assert project.tasks.createScalaClass
        assert project.tasks.createScalaObject
        assert project.tasks.createScalaProject
        assert project.tasks.exportScalaTemplates
        assert project.tasks.initScalaProject
    }
}
