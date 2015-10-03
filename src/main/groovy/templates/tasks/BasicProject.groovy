package templates.tasks

import org.gradle.api.Project
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import templates.ProjectProps
import templates.GitRepo
import templates.ProjectTemplate

class BasicProject {

    public static BasicProject create(Project project) {
        ProjectProps customProps = new ProjectProps(project)
        GitRepo gitRepo = openGitRepo(customProps)
        new BasicProject(customProps, gitRepo)
    }

    private static GitRepo openGitRepo(ProjectProps customProps) {
        File repoDir = customProps.repoDir
        if (customProps.isPropertyDefined("clean")) {
            repoDir.deleteDir()
        }

        repoDir.exists() ? GitRepo.open(repoDir) : initGitRepo(repoDir)
    }

    private static GitRepo initGitRepo(File repoDir) {
        repoDir.mkdirs()
        GitRepo git = GitRepo.init(repoDir)
        git.setRemoteUrl("origin", "git@github.com:blackbaud/${repoDir.name}.git")
        git
    }


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