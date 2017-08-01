package com.blackbaud.templates.tasks

import com.blackbaud.gradle.test.AbstractProjectSpecification
import com.blackbaud.gradle.test.TestGradleBuild
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import com.blackbaud.templates.GitRepo
import com.blackbaud.templates.ProjectProps
import spock.lang.Ignore


// TODO: need to investigate, not sure why but the compiler is not picking up javax.persistence.* classes...
@Ignore
class RestProjectSpec extends AbstractProjectSpecification {

    @Rule
    public TemporaryFolder projectDir = new TemporaryFolder()
    private TestGradleBuild testGradleBuild
    private RestProject restProject

    def setup() {
        project.ext["blackbaudGradleVersion"] = "2.14.1-bb.1.0"
        GitRepo repo = GitRepo.init(projectDir.root)
        ProjectProps projectProps = new ProjectProps(project)
        BasicProject basicProject = new BasicProject(projectProps, repo)
        basicProject.initGradleProject()
        restProject = new RestProject(basicProject, "service")
        testGradleBuild = new TestGradleBuild(projectDir.root)
    }

    def "initRestProject should successfully build"() {
        given:
        restProject.initRestProject()
        restProject.initPostgres()
        restProject.initKafka()
        testGradleBuild.initBuildscriptPluginPathString()

        when:
        testGradleBuild.run("build")

        then:
        notThrown(Exception)
    }

    def "createRestResource should successfully build"() {
        given:
        restProject.initRestProject()
        restProject.initPostgres()
        restProject.initKafka()
        restProject.createCrudResource("Fubar", true, false)
        restProject.createBasicResource("Fubaz", true)
        testGradleBuild.initBuildscriptPluginPathString()

        when:
        testGradleBuild.run("build")

        then:
        notThrown(Exception)
    }

}
