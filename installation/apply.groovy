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

apply plugin: templates.TemplatesPlugin