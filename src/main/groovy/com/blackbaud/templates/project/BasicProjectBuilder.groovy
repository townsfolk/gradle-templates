package com.blackbaud.templates.project

import com.blackbaud.templates.GitRepo

class BasicProjectBuilder {

    File repoDir;
    String name;
    File gradleUserHome;
    String blackbaudGradleVersion;
    boolean clean = false;
    boolean vsts = false;

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

    public BasicProjectBuilder gradleUserHome(File gradleUserHome) {
        this.gradleUserHome = gradleUserHome
        this
    }

    public BasicProjectBuilder name(String name) {
        this.name = name
        this
    }

    public BasicProjectBuilder clean() {
        this.clean = true
        this
    }

    public BasicProjectBuilder useVsts() {
        this.vsts = true
        this
    }

    public BasicProject build() {
        GitRepo gitRepo;
        if (vsts) {
            gitRepo = GitRepo.initVstsGitRepo(name, repoDir)
        } else {
            gitRepo = GitRepo.initGitHubRepo(name, repoDir)
        }
        BasicProject basicProject = new BasicProject(blackbaudGradleVersion, gitRepo, gradleUserHome)
        basicProject.initGradleProject()
        basicProject
    }

}
