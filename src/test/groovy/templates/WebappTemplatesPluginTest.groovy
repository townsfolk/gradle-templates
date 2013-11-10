package templates

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class WebappTemplatesPluginTest {

    @Rule public TemporaryFolder rootFolder = new TemporaryFolder()

    @Test void 'apply'(){
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'templates'

        assert project.tasks.createWebappProject
        assert project.tasks.exportWebappTemplates
        assert project.tasks.initWebappProject
    }

    @Test void 'createBase'(){
        def projectRoot = "${rootFolder.root}/webroot"

        def plugin = new WebappTemplatesPlugin()
        plugin.createBase( projectRoot, 'foo' )

        assert new File( projectRoot ).exists()
        assert new File( projectRoot, 'src' ).exists()
        assert new File( projectRoot, 'src/main' ).exists()
        assert new File( projectRoot, 'src/main/java' ).exists()
        assert new File( projectRoot, 'src/main/resources' ).exists()
        assert new File( projectRoot, 'src/test' ).exists()
        assert new File( projectRoot, 'src/test/java' ).exists()
        assert new File( projectRoot, 'src/test/resources' ).exists()
        assert new File( projectRoot, 'src/main/webapp' ).exists()
        assert new File( projectRoot, 'src/main/webapp/WEB-INF' ).exists()
        assert new File( projectRoot, 'src/main/webapp/WEB-INF/web.xml' ).exists()
        assert new File( projectRoot, 'LICENSE.txt' ).exists()
    }
}
