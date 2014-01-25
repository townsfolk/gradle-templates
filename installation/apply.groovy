
buildscript {
    repositories {
        maven {
            url 'http://dl.bintray.com/cjstehno/public'
        }
    }
    dependencies {
        classpath 'gradle-templates:gradle-templates:1.5'
    }
}

// Check to make sure templates.TemplatesPlugin isn't already added.
if (!project.plugins.findPlugin(templates.TemplatesPlugin)) {
	project.apply(plugin: templates.TemplatesPlugin)
}