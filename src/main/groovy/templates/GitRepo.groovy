package templates

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.StoredConfig

class GitRepo {

	static GitRepo init(File repoDir) {
		Git git = Git.init().setDirectory(repoDir).call()
		new GitRepo(git)
	}

	static GitRepo open(File repoDir) {
		Git git = Git.open(repoDir)
		new GitRepo(git)
	}

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

}
