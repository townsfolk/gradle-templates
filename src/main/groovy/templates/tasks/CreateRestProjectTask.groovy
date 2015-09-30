package templates.tasks

import org.gradle.api.Project

import static com.google.common.base.CaseFormat.LOWER_HYPHEN
import static com.google.common.base.CaseFormat.UPPER_CAMEL

class CreateRestProjectTask extends AbstractTemplateTask {

	CreateRestProjectTask() {
		super("Create a SpringBoot REST project")
	}

	@Override
	protected void renderTemplate() {
		RestProject restProject = RestProject.create(project)
		restProject.initRestProject()
	}

	private static class RestProject {

		public static RestProject create(Project project) {
			BasicProject basicProject = BasicProject.create(project)
			new RestProject(basicProject)
		}

		private BasicProject basicProject
		private String serviceName
		private String servicePackage

		public RestProject(BasicProject basicProject) {
			this.basicProject = basicProject
			serviceName = LOWER_HYPHEN.to(UPPER_CAMEL, basicProject.repoName)
			servicePackage = "com.blackbaud.${serviceName.toLowerCase()}"
		}

		void initRestProject() {
			basicProject.initGradleProject()
			createRestBase()
		}


		private void createRestBase() {
			basicProject.applyTemplate {
				'build.gradle' template: "/templates/springboot/build.gradle.tmpl"
				'src' {
					'main' {
						'java' {
							'com' {
								'blackbaud' {
									"${serviceName.toLowerCase()}" {
										"${serviceName}.java" template: "/templates/springboot/application-class.tmpl",
												serviceName: "${serviceName}",
												servicePackage: "${servicePackage}"

										'api' {
											'ResourcePaths.java' template: "/templates/springboot/resourcepaths-class.tmpl",
													packageName: "${servicePackage}.api"
										}
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
			basicProject.commitProjectFiles("initial commit, springboot rest")
		}

	}

}
