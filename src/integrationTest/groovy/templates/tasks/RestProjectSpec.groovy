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
    private RestProject restProject

    def setup() {
        project.ext["blackbaudGradleVersion"] = "2.7-bb.1.0"
        GitRepo repo = GitRepo.init(projectDir.root)
        ProjectProps projectProps = new ProjectProps(project)
        BasicProject basicProject = new BasicProject(projectProps, repo)
        basicProject.initGradleProject()
        restProject = new RestProject(basicProject, "service")
        testGradleBuild = new TestGradleBuild(projectDir.root)
    }

    def "initRestProject should successfully build"() {
        given:
        restProject.initRestProject(true)
        testGradleBuild.initBuildscriptPluginPathString()

        when:
        testGradleBuild.run("build")

        then:
        notThrown(Exception)
    }

    def "createRestResource should successfully build"() {
        given:
        restProject.initRestProject(true)
        restProject.createRestResource("Fubar", true)
        testGradleBuild.initBuildscriptPluginPathString()

        when:
        testGradleBuild.run("build")

        then:
        notThrown(Exception)
    }

}
