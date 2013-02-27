buildscript {
	repositories {
		ivy {
			name = 'gradle_templates'
			artifactPattern 'http://tellurianring.com/projects/gradle-plugins/[module]/[revision]/[artifact]-[revision].[ext]'
			ivyPattern 'http://tellurianring.com/projects/gradle-plugins/[module]/[revision]/[artifact]-[revision].[ext]'
		}
	}
	dependencies {
		classpath 'gradle-templates:gradle-templates:1.3'
	}
}
// Check to make sure templates.TemplatesPlugin isn't already added.
if (!project.plugins.findPlugin(templates.TemplatesPlugin)) {
	project.apply(plugin: templates.TemplatesPlugin)
}