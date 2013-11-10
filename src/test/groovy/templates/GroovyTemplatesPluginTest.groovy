package templates
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class GroovyTemplatesPluginTest {

    @Rule public TemporaryFolder rootFolder = new TemporaryFolder()

    @Test void 'apply'(){
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'templates'

        assert project.tasks.createGroovyClass
        assert project.tasks.createGroovyProject
        assert project.tasks.exportGroovyTemplates
        assert project.tasks.initGroovyProject
    }

    @Test void 'createBase'(){
        def projectRoot = "${rootFolder.root}/groovyroot"

        GroovyTemplatesPlugin plugin = new GroovyTemplatesPlugin()
        plugin.createBase( projectRoot )

        assert new File( projectRoot ).exists()
        assert new File( projectRoot, 'src' ).exists()
        assert new File( projectRoot, 'src/main' ).exists()
        assert new File( projectRoot, 'src/main/groovy' ).exists()
        assert new File( projectRoot, 'src/main/resources' ).exists()
        assert new File( projectRoot, 'src/test' ).exists()
        assert new File( projectRoot, 'src/test/groovy' ).exists()
        assert new File( projectRoot, 'src/test/resources' ).exists()
        assert new File( projectRoot, 'LICENSE.txt' ).exists()
    }
}
