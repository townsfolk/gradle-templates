package templates.tasks

import org.gradle.api.GradleException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import templates.ProjectProps
import templates.GitRepo
import templates.ProjectTemplate

class BasicProject {

    @Delegate
    private GitRepo gitRepo
    private ProjectProps projectProps
    String repoName

    BasicProject(ProjectProps projectProps, GitRepo gitRepo) {
        this.projectProps = projectProps
        this.gitRepo = gitRepo
        repoName = gitRepo.repoDir.name
    }

    File getRepoDir() {
        gitRepo.repoDir
    }

    boolean isPropertyDefined(String name) {
        projectProps.isPropertyDefined(name)
    }

    void initGradleProject() {
        if (new File(repoDir, "build.gradle").exists() == false) {
            initGradleWrapper()
            initGitignore()
            gitRepo.commitProjectFiles("initial commit, gradle wrapper")

            replaceGradleWrapperDistributionUrl()
            gitRepo.commitProjectFiles("use blackbaud gradle")

            initBasicGradleBuild()
            gitRepo.commitProjectFiles("added build.gradle")
        }
    }

    private void initBasicGradleBuild() {
        applyTemplate {
            'build.gradle' template: "/templates/basic/build.gradle.tmpl"
        }
    }

    private void replaceGradleWrapperDistributionUrl() {
        File gradleWrapperProperties = new File(repoDir, "gradle/wrapper/gradle-wrapper.properties")
        String text = gradleWrapperProperties.text
        String blackbaudGradleVersion = projectProps.getRequiredProjectProperty("blackbaudGradleVersion")
        String distributionUrl = "https://nexus-releases.blackbaudcloud.com/content/repositories/releases/com/blackbaud/gradle-blackbaud/${blackbaudGradleVersion}/gradle-blackbaud-${blackbaudGradleVersion}-bin.zip"
        gradleWrapperProperties.text = text.replaceFirst(/(?m)^distributionUrl=.*/, /distributionUrl=${distributionUrl}/)
    }

    private void initGradleWrapper() {
        ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(repoDir)
                .connect()
        try {
            connection.newBuild().forTasks("wrapper").run()
        } finally {
            connection.close()
        }
    }

    private void initGitignore() {
        applyTemplate {
            '.gitignore' template: "/templates/git/gitignore.tmpl"
        }
    }

    void applyTemplate(Closure closure) {
        ProjectTemplate.fromRoot(repoDir, closure)
    }

    void applyTemplate(String relativePath, Closure closure) {
        ProjectTemplate.fromRoot(new File(repoDir, relativePath), closure)
    }

    File getProjectFile(String filePath) {
        new File(repoDir, filePath)
    }

    File getProjectFileOrFail(String filePath) {
        File file = getProjectFile(filePath)
        if (file.exists() == false) {
            throw new GradleException("Failed to resolve ${file.name} at expected location=${file.absolutePath}")
        }
        file
    }

}