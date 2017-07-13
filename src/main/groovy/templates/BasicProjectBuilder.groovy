package templates

import templates.tasks.BasicProject

class BasicProjectBuilder {

    File repoDir;
    String blackbaudGradleVersion;
    boolean clean = false;

    private BasicProjectBuilder() {}

    public static BasicProjectBuilder getInstance() {
        new BasicProjectBuilder()
    }

    public BasicProjectBuilder repoDir(File repoDir) {
        this.repoDir = repoDir
        this
    }

    public BasicProjectBuilder blackbaudGradleVersion(String blackbaudGradleVersion) {
        this.blackbaudGradleVersion = blackbaudGradleVersion
        this
    }

    public BasicProjectBuilder clean() {
        this.clean = true
        this
    }

    public BasicProject build() {
        GitRepo gitRepo = GitRepo.openOrInitGitRepo(repoDir, clean)
        BasicProject basicProject = new BasicProject(blackbaudGradleVersion, gitRepo)
        basicProject.initGradleProject()
        basicProject
    }

}
