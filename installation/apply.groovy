buildscript {
	repositories {
		ivy {
			name = 'gradle_templates'
			//artifactPattern "http://launchpad.net/[organization]/trunk/[revision]/+download/[artifact]-[revision].jar"
			artifactPattern "file:///Users/elberry/development/projects/mine/[organization]/build/libs/[artifact]-[revision].jar"
		}
	}
	dependencies {
		classpath 'gradle-templates:templates:1.1'
	}
}
// Check to make sure templates.TemplatesPlugin isn't already added.
if (!project.plugins.findPlugin(templates.TemplatesPlugin)) {
	project.apply(plugin: templates.TemplatesPlugin)
}