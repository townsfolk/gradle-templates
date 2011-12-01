buildscript {
	repositories {
		ivy {
		   String tpInstallDir = 'file:///Users/elberry/development/projects/mine/[organization]'
			name = 'gradle_templates'
			artifactPattern "${tpInstallDir}/build/libs/[artifact]-[revision].jar"
		}
	}
	dependencies {
		classpath 'gradle-templates:templates:1.2'
	}
}
// Check to make sure templates.TemplatesPlugin isn't already added.
if (!project.plugins.findPlugin(templates.TemplatesPlugin)) {
	project.apply(plugin: templates.TemplatesPlugin)
}