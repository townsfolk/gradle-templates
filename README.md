## Introduction

The Gradle Templates plugin helps you get started using Gradle by providing convenient tasks for creating new projects that work with the Gradle build system. Eg. To create a new Java project you can run:

  gradle createJavaProject

Which will prompt you for your new project's name and create a new directory with it. It will also create a standard directory structure in your project's directory that works with Gradle's default configurations.

The plugin also makes it easy to create your own templates which can be useful in creating new projects, or creating components within your projects. Eg. It's easy to create a simple task to generate a new GSP that fits your company's standard layout. Or to create a more complex task to generate a new servlet and add the entry into your webapp's web.xml file.
## Installation
### Binaries
Gradle M3 and above

To install the plugin with Gradle M3 and above you should use the apply script provided on the Launchpad website.

To use the apply script, add this to your 'build.gradle' file:

apply from: 'http://launchpad.net/gradle-templates/trunk/1.2/+download/apply.groovy'

You can use the 'latest' apply script to automatically get updated when there are new releases.

apply from: 'http://launchpad.net/gradle-templates/trunk/latest/+download/apply.groovy'

This apply script will always point to the latest release available.
Gradle M3 and below

These instructions will work with Gradle M3, but not M4, M5, etc…

There are binary files provided on the Launchpage project page:

    All Downloads (Current release is 1.1)

	 It's recommended that you use the unversioned file if installing the pre-packaged binary file so that upgrading will be easier.

	 Once downloaded, copy the 'templates.jar' file to your Gradle install's plugin directory. Eg. '[gradle]/lib/plugins'.
	 Installing from source
	 Requirements

	     Gradle 1.0-milestone-3 (or greater) installed (Current version is 1.0-milestone-3)
		      Bazaar VCS installed (Current version is 2.3.3)

				Getting the source

				The source is hosted on Launchpad.net in a Bazaar repository.

				Branch the project via the following command:

				bzr branch lp:gradle-templates

				Installing from source M3 and above

				Build the plugin:

				cd gradle-templates
				gradle build

				Use the apply script that is found under the installation directory.

				apply from: 'file:///[gradle-templates]/installation/apply-local.groovy'

				Installing from source M3 and below

				Execute the 'installPlugin' target:

				cd gradle-templates
				gradle installPlugin

				This will result in the 'templates-[version].jar' (Current version is 1.1) being installed under your '[gradle distribution]/lib/plugins' directory.
				Quick Start

				After you've installed the plugin you need only to create a new 'build.gradle' file and apply the plugin.

				apply plugin: "templates"

				Entering 'gradle tasks' now will show you the new 'Templates' tasks:

				Template tasks
				--------------
				createGradlePlugin - Creates a new Gradle Plugin project in a new directory named after your project.
				createGroovyClass - Creates a new Groovy class in the current project.
				createGroovyProject - Creates a new Gradle Groovy project in a new directory named after your project.
				createJavaClass - Creates a new Java class in the current project.
				createJavaProject - Creates a new Gradle Java project in a new directory named after your project.
				createScalaClass - Creates a new Scala class in the current project.
				createScalaObject - Creates a new Scala object in the current project.
				createScalaProject - Creates a new Gradle Scala project in a new directory named after your project.
				createWebappProject - Creates a new Gradle Webapp project in a new directory named after your project.
				initGradlePlugin - Initializes a new Gradle Plugin project in the current directory.
				initGroovyProject - Initializes a new Gradle Groovy project in the current directory.
				initJavaProject - Initializes a new Gradle Java project in the current directory.
				initScalaProject - Initializes a new Gradle Scala project in the current directory.
				initWebappProject - Initializes a new Gradle Webapp project in the current directory.

				The main difference between the 'create*Project' and 'init*Project' tasks is that the 'create' tasks end up creating a new directory for your new project, and the 'init' tasks will create the default directory structure under the current directory.

				Eg. Running 'createGroovyProject' will prompt you for your new project's name:

				[10:48:18 elberry@pulse:~/development/projects]
				[543] gradle createGroovyProject
				> Building > :createGroovyProject
				?> Project Name: TestGroovyProject
				:createGroovyProject

				BUILD SUCCESSFUL

				and create a directory like:

				TestGroovyProject
				   /src
					      /main
							         /groovy
										         /resources
													      /test
															         /groovy
																		         /resources

																					Running the 'initGroovyProject' task will generate the default 'groovy project' directory structure in the current directory.

																					./src
																					   /main
																						      /groovy
																								      /resources
																										   /test
																											      /groovy
																													      /resources

																															Note that both of these tasks will also generate a 'build.gradle' file, and a 'LICENSE.txt' file in their respective roots.

																															This results in this basic directory hierarchy:

																															[project root]
																															   /src
																																      /main
																																		         /groovy
																																					         /resources
																																								      /test
																																										         /groovy
																																													         /resources
																																																   build.gradle
																																																	   LICENSE.txt

																																																		Which matches the default Gradle configuration.
																																																		Getting only what you need

																																																		The general Templates plugin actually wraps 3 other smaller plugins. If you are only working with Java projects you can simply change the plugin 'apply' method to use the 'java-templates' plugin instead.

																																																		apply plugin: "templates"

																																																		Becomes:

																																																		apply plugin: "java-templates"

																																																		Then only the '*Java*' tasks will be available.

																																																		Template tasks
																																																		--------------
																																																		createJavaClass - Creates a new Java class in the current project.
																																																		createJavaProject - Creates a new Gradle Java project in a new directory named after your project.
																																																		initJavaProject - Initializes a new Gradle Java project in the current directory.

																																																		Installing for global use

																																																		Using Gradle init scripts, it's possible to install the Templates plugin globally - Allowing you to execute 'gradle createJavaProject' anywhere, even when there isn't a build.gradle file present in the directory.

																																																		Simply add the following to your '~/.gradle/init.gradle' script:

																																																		gradle.beforeProject { prj ->
																																																		   prj.apply plugin: 'templates'
																																																		}

																																																		A smaller quick start is available on my blog.
																																																		Creating your own Project Templates

																																																		All the template plugins make use of the ProjectTemplate class. With it you can easy create your own tasks to generate your own files and directories.
																																																		Creating a simpler Java project

																																																		Gradle defines a pretty standard configuration for a standard Java project. It's a directory structure that not only Gradle understands, but Maven understands as well. However, perhaps you don't need your project work this way, and you just want a very simple directory structure for your project.

																																																		Eg. Say we want a directory structure like so:

																																																		[project root]
																																																		   /src
																																																			   /build.gradle

																																																				Where all our java classes simply go under the 'src' directory. Let's also say that you have a base package of 'com.example', so that all Java classes basically go under 'src/com/example'.

																																																				Gradle already makes it very easy to change the default directory structure, so all we need is a new task to help us quickly generate the basic directory structures.

																																																				import templates.*
																																																				 
																																																				 task "createMyProject" << {
																																																					    String projectName = TemplatesPlugin.prompt("Project Name:")
																																																						    ProjectTemplate.fromRoot(projectName) {
																																																								       "src/com/example" {}
																																																										       "build.gradle" """
																																																												       apply plugin: 'java'
																																																														       sourceSets {
																																																																	          main {
																																																																					             java {
																																																																										                srcDir 'src'
																																																																															             }
																																																																																			          }
																																																																																						       }
																																																																																								       """
																																																																																										    }
																																																				 }

																																																				 When you run 'gradle createMyProject' now you'll be prompted for your new project's name, and then a new directory will be created based on that name.

																																																				 The import statement simply imports the TemplatesPlugin, and ProjectTemplate classes into your build script.

																																																				 We then create a new task named 'createMyProject' which uses the TemplatesPlugin.prompt method to ask for the new project's name.

																																																				 After that we use the ProjectTemplate.fromRoot method to start generating the required directories and files.

																																																				 In this example we're creating just a single file 'build.gradle' and using a multi-line string to pass the default contents.

																																																				 You can also use a template, and parameters if you don't want the content to be directly in your build script.

																																																				 For example, we could specify a default Java class template to be used when generating Java classes.

																																																				 package ${classPackage};
																																																				  
																																																				  /**
																																																				   * @author ${System.getProperty("user.name")}
																																																					 * Created: ${new Date()}
																																																					  */
																																																					  public class ${className} {
																																																						     public ${className}() {
																																																								   
																																																									   }
																																																					  }

																																																					  Here we create a new GString template file called 'java-class.tmpl'. Which we use in a new 'createJavaClass' task:

																																																					  task "createJavaClass" << {
																																																						     String projectName = TemplatesPlugin.prompt("Project Name:")
																																																							     String className = TemplatesPlugin.prompt("Class Name:")
																																																								     ProjectTemplate.fromRoot(projectName) {
																																																										        "src/com/example" {
																																																													           "${className}.java" template: "./java-class.tmpl",
																																																																                 classPackage: "com.example",
																																																																					                 className: className
																																																																										        }
																																																																												     }
																																																					  }

																																																					  Executing 'gradle createJavaClass' prompts us for our project name, and new Java class name. The class has the default package 'com.example', and our newly generated java file looks like this:

																																																					  package com.example;
																																																					   
																																																						/**
																																																						 * @author elberry
																																																						  * Created: Mon Apr 18 12:45:30 PDT 2011
																																																						   */
																																																							public class MyClass {
																																																								   public MyClass() {
																																																										 
																																																										    }
																																																							}

																																																							Modifying default Template files

																																																							The Templates plugin comes with GString template files for all files generated through the tasks. These template files can be exported (Templates Plugin v1.1+) to the local file system and then customized to fit your needs.
																																																							Template file lookup

																																																							The Templates plugin will look for template files first by absolute path, then within the current working directory, and finally in the classpath.

																																																							Eg. The 'java-templates' plugin uses the ”/templates/java/java-class.tmpl” file to generate the new Java Class during the 'createJavaClass' task execution.

																																																							The Templates plugin will first look on the file system for /templates/java/java-class.tmpl, then ./templates/java/java-class.tmpl, and finally in the classpath for /templates/java/java-class.tmpl.

																																																							You can place your own ”java-class.tmpl” file in either of the two file system locations and it'll override the default one provided in the templates.jar file.
																																																							Exporting template files (Templates Plugin v1.1+)

																																																							Run the 'exportAllTemplates' to copy all templates files into the current working directory. This will create a subdirectory called 'templates' containing all the .tmpl files. Each template plugin provides their own export task, eg. The 'java-templates' plugin provides the 'exportJavaTemplates' task.
																																																							Extra stuff

																																																							The TemplatesPlugin contains a few helpful methods and properties like the prompt method.
																																																							Properties

																																																							group: Can be used when defining tasks to have all template tasks grouped together.

																																																							task "createJavaProject"(group: TemplatesPlugin.group) << { ... }

																																																							Methods

																																																							prependPlugin: Can be used in tasks to help add plugins to project build scripts.

																																																							task "createJavaProject" << {
																																																								   ...
																																																									   TemplatesPlugin.prependPlugin 'java', new File(projectName, "build.gradle")
																																																							}

																																																							prompt: Can be used in tasks to prompt the user for some input.

																																																							task "favoriteColor" << {
																																																								   String color = TemplatesPlugin.prompt("What is your favorite color?")
																																																									   println "Your favorite color is ${color}"
																																																							}

																																																							Also takes a default value.

																																																							task "favoriteColor" << {
																																																								   String color = TemplatesPlugin.prompt("What is your favorite color?", "Green")
																																																									   println "Your favorite color is ${color}"
																																																							}

																																																							promptOptions: Can be used to prompt the user for a choice of 1 of a list of options.

																																																							task "bestColor" << {
																																																								   def colors = ["red", "blue", "green"]
																																																									   int choice = TemplatesPlugin.promptOptions("Which color do you like best?", colors)
																																																										   println "You like ${colors[choice]} best."
																																																							}

																																																							Or you can pass in a default value:

																																																							// Default value is 1 based - matches the numeric values displayed to the user.
																																																							task "bestColor" << {
																																																								   def colors = ["red", "blue", "green"]
																																																									   int choice = TemplatesPlugin.promptOptions("Which color do you like best?", 1, colors)
																																																										   println "You like ${colors[choice]} best."
																																																							}

																																																							promptYesOrNo: Can be used in tasks to prompt for boolean 'yes', or 'no' answers.

																																																							task "useful" << {
																																																								   if(TemplatesPlugin.promptYesOrNo("Is the templates plugin useful to you?")) {
																																																										      println "Great! I'm glad to hear that!"
																																																												   } else {
																																																														      println "Awww, I'm sorry to hear that."
																																																																   }
																																																							}

																																																							Gradle Properties for tasks

																																																							The Templates tasks are listed below with the available -P properties. Supplying the -P properties on the command line will avoid being prompted for the values during task execution.
																																																							createGradlePlugin

																																																							Has the same properties as 'initGradlePlugin' plus the following.
																																																							property 	description
																																																							newProjectName 	Your new Gradle Plugin's name
																																																							exportPluginTemplates

																																																							There are no prompts for this task.
																																																							initGradlePlugin
																																																							property 	description
																																																							projectGroup 	Your project's group
																																																							projectVersion 	The version of your project
																																																							pluginApplyLabel 	The apply label for use in 'apply plugin: “label”'
																																																							pluginClassName 	The classname of your new plugin. Can include package, eg. 'com.example.MyClass'
																																																							initGroovyProject

																																																							There are no prompts for this task.
																																																							createGroovyProject
																																																							property 	description
																																																							newProjectName 	Your new Gradle Plugin's name
																																																							Contributing

																																																							To contribute code to the Templates plugin you should be familiar with Bazaar VCS, or have experience with another distributed version control system (DVCS) like Git, or Mercurial.

																																																							You should also have a launchpad account. Then you can create your own branch from the main one, commit your changes to it, and then propose a merge.

																																																							The general process looks like this for creating a new branch and fixing some code.

																																																							bzr branch lp:gradle-templates
																																																							cd gradle-templates

																																																							... fix/add code ...

																																																							bzr ci -m "Fixing Eric's Code"
																																																							bzr push lp:~userid/gradle-templates/trunk

																																																							Just replace 'userid' with your Launchpad user id (eg. elberry). You can also change 'trunk' to be anything.

																																																							Then visit your branch on launchpad, eg: https://code.launchpad.net/~elberry/gradle-templates/test

																																																							You should see a link that says “Propose for merging”. Click that and follow the instructions there.
																																																							Getting Help

																																																							To ask questions or report bugs, please use the Launchpad.net project.

																																																							    Project Page: https://launchpad.net/gradle-templates
																																																								         Asking Questions: https://answers.launchpad.net/gradle-templates
																																																											        Reporting Bugs: https://bugs.launchpad.net/gradle-templates

