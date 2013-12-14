package templates

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class JavaTemplatesPluginTest {

    @Rule public TemporaryFolder rootFolder = new TemporaryFolder()

    @Test void 'apply'(){
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'templates'

        assert project.tasks.createJavaClass
        assert project.tasks.createJavaProject
        assert project.tasks.exportJavaTemplates
        assert project.tasks.initJavaProject
    }

    @Test void 'createBase'(){
        def projectRoot = "${rootFolder.root}/javaroot"

        def plugin = new JavaTemplatesPlugin()
        plugin.createBase( projectRoot )

        assert new File( projectRoot ).exists()
        assert new File( projectRoot, 'src' ).exists()
        assert new File( projectRoot, 'src/main' ).exists()
        assert new File( projectRoot, 'src/main/java' ).exists()
        assert new File( projectRoot, 'src/main/resources' ).exists()
        assert new File( projectRoot, 'src/test' ).exists()
        assert new File( projectRoot, 'src/test/java' ).exists()
        assert new File( projectRoot, 'src/test/resources' ).exists()
        assert new File( projectRoot, 'LICENSE.txt' ).exists()
    }
}
