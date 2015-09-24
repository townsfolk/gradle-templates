package templates.tasks

import org.eclipse.jgit.api.Git
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import templates.CustomProps
import templates.ProjectTemplate
import templates.TemplatesPlugin

import static com.google.common.base.CaseFormat.*;

class CreateRestProjectTask extends DefaultTask {

	private CustomProps customProps

	CreateRestProjectTask() {
		group = TemplatesPlugin.group
		customProps = new CustomProps(project)
	}

	@TaskAction
	def init() {
		File repoDir = getRepoDir()
		if (isDefined("clean")) {
			repoDir.deleteDir()
		}
		if (repoDir.exists() == false) {
			RestProject restProject = new RestProject(repoDir)
			initRestProject(restProject)
		}
	}

	private File getRepoDir() {
		String repoName = acquireProjectProperty("repoName")
		new File(customProps.getWorkspaceDir(), repoName)
	}

	private boolean isDefined(String name) {
		project.properties.keySet().contains(name)
	}

	private String acquireProjectProperty(String name) {
		if (project.hasProperty(name) == false) {
			throw new GradleException("Expected project property '${name}' not defined")
		}
		project.ext[name]
	}

	private void initGradleProject(File repoDir) {
		repoDir.mkdirs()

		ProjectConnection connection = GradleConnector.newConnector()
				.forProjectDirectory(repoDir)
				.connect()
		try {
			connection.newBuild().forTasks("wrapper").run()
		} finally {
			connection.close()
		}

		initGitignore(repoDir)
		Git.init().setDirectory(repoDir).call()

		commitProjectFiles(repoDir, "initial commit")
	}

	private initGitignore(File repoDir) {
		new File(repoDir, ".gitignore").write """# Gradle
.gradle/
build/

# IDEA
.idea/
*.iml
*.ipr
*.iws

# OS
.DS_Store
"""
	}

	private void commitProjectFiles(File repoDir, String commitMessage) {
		Git git = Git.open(repoDir)
		git.add().addFilepattern(".").call()
		git.commit().setMessage(commitMessage).call()
	}

	private void initRestProject(RestProject restProject) {
		initGradleProject(restProject.repoDir)
		createRestBase(restProject)
	}



	private void createRestBase(RestProject restProject) {
		ProjectTemplate.fromRoot(restProject.repoDir) {
			'build.gradle' template: "/templates/springboot/build.gradle.tmpl"
			'src' {
				'main' {
					'java' {
						'com' {
							'blackbaud' {
								"${restProject.serviceName.toLowerCase()}" {
									"${restProject.serviceName}.java" template: "/templates/springboot/application-class.tmpl",
											serviceName: "${restProject.serviceName}",
											servicePackage: "${restProject.servicePackage}"
								}
							}
						}
					}
					'resources' {
						'application.properties' content: """server.port=8080
management.port=8081
"""
					}
				}
				'test' {
					"groovy" {}
				}
				'componentTest' {
					'resources' {
						'logback.xml' template: "/templates/logback/logback.tmpl"
					}
				}
			}
		}
	}


	private static class RestProject {

		private File repoDir
		private String repoName
		private String serviceName
		private String servicePackage

		RestProject(File repoDir) {
			this.repoDir = repoDir
			repoName = repoDir.name
			serviceName = LOWER_HYPHEN.to(UPPER_CAMEL, repoName)
			servicePackage = "com.blackbaud.${serviceName.toLowerCase()}"
		}

	}

}
