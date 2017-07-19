package com.blackbaud.templates

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.StoredConfig
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

class GitRepo {

    private static final String VSTS_GIT_BASE_URL = "https://blackbaud.visualstudio.com/Products/_git/"
    private static final String GITHUB_BASE_URL = "https://github.com/blackbaud/"

    private Git git

    private GitRepo(Git git) {
        this.git = git
    }

    File getRepoDir() {
        git.repository.directory.parentFile
    }

    void commitProjectFiles(String commitMessage) {
        git.add().addFilepattern(".").call()
        if (git.status().call().clean == false) {
            git.commit().setMessage(commitMessage).call()
        }
    }

    void setRemoteUrl(String name, String url) {
        StoredConfig config = git.repository.config
        config.setString("remote", name, "url", url)
        config.save()
    }

    void pushProject(String username, String password) {
       git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).setPushAll().call()
    }

    static GitRepo init(File repoDir) {
        Git git = Git.init().setDirectory(repoDir).call()
        new GitRepo(git)
    }

    static GitRepo open(File repoDir) {
        Git git = Git.open(repoDir)
        new GitRepo(git)
    }

    static GitRepo openOrInitGitRepo(File repoDir, boolean clean) {
        if (clean) {
            repoDir.deleteDir()
        }
        repoDir.exists() ? openGitRepo(repoDir) : initGitHubRepo(repoDir.name, repoDir)
    }

    static GitRepo openGitRepo(File repoDir) {
        if (repoDir.exists() == false) {
            throw new RuntimeException("Cannot open git project, dir=${repoDir.absolutePath}")
        }
        open(repoDir)
    }

    static GitRepo initGitHubRepo(String name, File repoDir) {
        repoDir.mkdirs()
        GitRepo git = init(repoDir)
        git.setRemoteUrl("origin", GITHUB_BASE_URL + "${name}.git")
        git
    }

    static GitRepo initVstsGitRepo(String name, File repoDir) {
        repoDir.mkdirs()
        GitRepo git = init(repoDir)
        git.setRemoteUrl("origin", VSTS_GIT_BASE_URL + "${name}")
        git
    }
}
