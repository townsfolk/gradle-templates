buildscript {
	repositories {
		ivy {
		   String tpInstallDir = project.hasProperty('gradle.templates.dir') ? project.getProperty('gradle.templates.dir') :
				                                                         System.properties['user.dir']
			name = 'gradle_templates'
			artifactPattern "${tpInstallDir}/build/libs/[artifact]-[revision].jar"
		}
	}
	dependencies {
		def templatesVersion = project.hasProperty('gradle.templates.ver') ? project.getProperty('gradle.templates.ver') :
                                                                         project.version
		classpath "gradle-templates:gradle-templates:${templatesVersion}"
	}
}
// Check to make sure templates.TemplatesPlugin isn't already added.
if (!project.plugins.findPlugin(templates.TemplatesPlugin)) {
	project.apply(plugin: templates.TemplatesPlugin)
}