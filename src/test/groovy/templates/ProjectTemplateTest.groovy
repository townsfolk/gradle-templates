package templates
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ProjectTemplateTest {

    private static final String PROJECT_NAME = 'some_project'
    @Rule public TemporaryFolder rootFolder = new TemporaryFolder()

    @Test void 'fromRoot(String): empty'(){
        assert !(new File( rootFolder.root, PROJECT_NAME ).exists())

        ProjectTemplate.fromRoot( "${rootFolder.root}/$PROJECT_NAME" )

        assert new File( rootFolder.root, PROJECT_NAME ).exists()
    }

    @Test void 'fromRoot(String): with content'(){
        assert !(new File( rootFolder.root, PROJECT_NAME ).exists())

        ProjectTemplate.fromRoot( "${rootFolder.root}/$PROJECT_NAME" ){
            'stuff' {
                'README.txt' '''
                    This is a generated README file.
                '''
                'deeper' {}
            }
        }

        assert new File( rootFolder.root, PROJECT_NAME ).exists()
        assert new File( rootFolder.root, "$PROJECT_NAME/stuff" ).exists()
        assert new File( rootFolder.root, "$PROJECT_NAME/stuff/deeper" ).exists()

        def readmeFile = new File( rootFolder.root, "$PROJECT_NAME/stuff/README.txt" )
        assert readmeFile.exists()
        assert readmeFile.text.trim() == 'This is a generated README file.'
    }

    @Test void 'fromRoot(File): empty'(){
        assert !(new File( rootFolder.root, PROJECT_NAME ).exists())

        ProjectTemplate.fromRoot( new File( rootFolder.root, PROJECT_NAME ) )

        assert new File( rootFolder.root, PROJECT_NAME ).exists()
    }
}
