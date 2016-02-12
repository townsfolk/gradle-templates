package templates.tasks

import com.blackbaud.gradle.test.AbstractProjectSpecification
import com.blackbaud.gradle.test.TestGradleBuild
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import templates.GitRepo
import templates.ProjectProps


public class RestProjectSpec extends AbstractProjectSpecification {

    @Rule
    public TemporaryFolder projectDir = new TemporaryFolder()
    private TestGradleBuild testGradleBuild
    private BasicProject basicProject

    def setup() {
        testGradleBuild = new TestGradleBuild(projectDir.root)
        GitRepo repo = GitRepo.init(projectDir.root)
        ProjectProps projectProps = new ProjectProps(project)
        basicProject = new BasicProject(projectProps, repo)
    }

    def "initRestProject should successfully build"() {
        given:
        RestProject restProject = new RestProject(basicProject, basicProject.repoName)
        restProject.initRestProject(true)

        when:
        testGradleBuild.run("build")

        then:
        notThrown(Exception)
    }

}
