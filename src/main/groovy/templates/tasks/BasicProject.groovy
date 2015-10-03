package templates.tasks

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import templates.ProjectProps
import templates.GitRepo
import templates.ProjectTemplate

class BasicProject {

    @Delegate
    private GitRepo gitRepo
    private ProjectProps customProps
    String repoName

    BasicProject(ProjectProps customProps, GitRepo gitRepo) {
        this.customProps = customProps
        this.gitRepo = gitRepo
        repoName = gitRepo.repoDir.name
    }

    File getRepoDir() {
        gitRepo.repoDir
    }

    boolean isPropertyDefined(String name) {
        customProps.isPropertyDefined(name)
    }

    void initGradleProject() {
        if (new File(repoDir, "build.gradle").exists() == false) {
            initGradleWrapper()
            initGitignore()
            gitRepo.commitProjectFiles("initial commit, gradle wrapper")
        }
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
}