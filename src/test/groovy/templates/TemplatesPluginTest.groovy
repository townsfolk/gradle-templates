package templates
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class TemplatesPluginTest {

    @Test void 'apply'(){
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'templates'

        assert project.tasks.exportAllTemplates

        assert project.tasks.createGradlePlugin
        assert project.tasks.exportPluginTemplates
        assert project.tasks.initGradlePlugin

        assert project.tasks.createGroovyClass
        assert project.tasks.createGroovyProject
        assert project.tasks.exportGroovyTemplates
        assert project.tasks.initGroovyProject

        assert project.tasks.createJavaClass
        assert project.tasks.createJavaProject
        assert project.tasks.exportJavaTemplates
        assert project.tasks.initJavaProject

        assert project.tasks.createScalaClass
        assert project.tasks.createScalaObject
        assert project.tasks.createScalaProject
        assert project.tasks.exportScalaTemplates
        assert project.tasks.initScalaProject

        assert project.tasks.createWebappProject
        assert project.tasks.exportWebappTemplates
        assert project.tasks.initWebappProject
    }
}
