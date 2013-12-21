# Gradle Templates Plugin

> Note: this project is in the process of being transitioned to a new caretaker.

## Introduction

The Gradle Templates plugin helps you get started using Gradle by providing convenient tasks for creating new projects that work with the Gradle build system.
Eg. To create a new Java project you can run:

```gradle createJavaProject```

Which will prompt you for the name of your new project and then create a new directory with it. It will also create a standard directory structure in your
project's directory that works with Gradle's default configurations.

The plugin also makes it easy to create your own templates which can be useful in creating new projects, or creating components within your projects. Eg.
It's easy to create a simple task to generate a new GSP that fits your company's standard layout. Or to create a more complex task to generate a new servlet
and add the entry into your webapp's web.xml file.

## Installation

The standard way to install this plugin is by adding the following to your build.gradle file:

```groovy
buildscript {
    repositories {
        maven {
			url 'http://dl.bintray.com/cjstehno/public'
		}
    }
    dependencies {
        classpath 'gradle-templates:gradle-templates:1.4.1'
    }
}

apply plugin:'templates'
```

Other methods of installation are documented on the project [installation](https://github.com/cjstehno/gradle-templates/wiki/Installation) page.

## Usage

Run the `gradle tasks` command to see a list of "create", "init", and "export" tasks provided by the default plugin templates.

Running a create or init task will prompt the user for information and then generate the appropriate content.

The main difference between the `create*Project` and `init*Project` tasks is that the create tasks end up creating a new directory
for your new project, and the init tasks will create the default directory structure under the current directory.

The `export*` tasks cause the templates to be exported to the local project.

Other usage documentation can be found on the project [Usage](https://github.com/cjstehno/gradle-templates/wiki/Usage) page.

## Details

* Version: 1.4.1
* Project Site: [http://cjstehno.github.io/gradle-templates](http://cjstehno.github.io/gradle-templates)
* Project Repo: [https://github.com/cjstehno/gradle-templates](https://github.com/cjstehno/gradle-templates)
* Wiki/Documentation: [https://github.com/cjstehno/gradle-templates/wiki](https://github.com/cjstehno/gradle-templates/wiki)
* License: [Apache 2](http://www.apache.org/licenses/LICENSE-2.0.html)
* Questions, bugs, issues: [https://github.com/cjstehno/gradle-templates/issues](https://github.com/cjstehno/gradle-templates/issues)

[![Build Status](https://drone.io/github.com/cjstehno/gradle-templates/status.png)](https://drone.io/github.com/cjstehno/gradle-templates/latest)

