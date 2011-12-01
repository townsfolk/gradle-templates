buildscript {
	repositories {
		ivy {
			name = 'gradle_templates'
			artifactPattern "http://launchpad.net/[organization]/trunk/[revision]/+download/[artifact]-[revision].jar"
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