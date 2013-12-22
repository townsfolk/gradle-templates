/*
 * Copyright (c) 2011,2012 Eric Berry <elberry@tellurianring.com>
 * Copyright (c) 2013 Christopher J. Stehno <chris@stehno.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package templates

import org.gradle.api.Plugin
import org.gradle.api.Project
/**
 * Adds basic tasks for bootstrapping Scala projects. Adds createScalaClass, createScalaObject, createScalaProject,
 * exportScalaTemplates, and initScalaProject tasks.
 */
class ScalaTemplatesPlugin implements Plugin<Project> {

	/**
	 * Finds the path to the main java source directory.
	 * @param project The project.
	 * @return The path to the main groovy source directory.
	 */
	static String findMainScalaDir(Project project) {
		File rootDir = project.projectDir
		def mainSrcDir = project.sourceSets?.main?.scala?.srcDirs*.path
		mainSrcDir = mainSrcDir?.first()
		mainSrcDir = mainSrcDir?.minus(rootDir.path)
		return mainSrcDir
	}

	/**
	 * Creates the basic Scala project directory structure.
	 * @param path the root of the project. Optional,defaults to user.dir.
	 */
	void createBase(String path = System.getProperty('user.dir')) {
		ProjectTemplate.fromRoot(path) {
			'src' {
				'main' {
					'scala' {}
					'resources' {}
				}
				'test' {
					'scala' {}
					'resources' {}
				}
			}
			'LICENSE.txt' '// Your License Goes here'
		}
	}

	/**
	 * Initializes or creates the project's build.gradle file.
	 * @param project The project
	 * @param path The path to the root of the project. optional, defaults to user.dir.
	 */
	void setupBuildFile(Project project, String path = System.getProperty('user.dir')) {
		def props = project.properties

        String scalaVersion = props['scalaVersion'] ?: TemplatesPlugin.prompt('Scala Version:', '2.9.0')

		boolean useFastCompiler = props['useFastScalaCompiler'] ?: TemplatesPlugin.promptYesOrNo('Use fast compiler?', false)

        ProjectTemplate.fromRoot(path) {
			'build.gradle' template: '/templates/scala/build.gradle.tmpl', append: true,
					scalaVersion: scalaVersion,
					useFastCompiler: useFastCompiler,
					projectGroup: project.group
			'gradle.properties' content: "version=${project.version == 'unspecified' ? '0.1' : project.version}", append: true
		}
	}

	/**
	 * Creates a Scala class, or object in the currect working directory.
	 * @param project The project.
	 * @param object Is this a Scala class, or object.
	 */
	void createScalaClass(Project project, boolean object) {
		def mainSrcDir = null
		try {
			// get main Scala dir, and check to see if Scala plugin is installed.
			mainSrcDir = findMainScalaDir(project)
		} catch (Exception e) {
			throw new IllegalStateException('It seems that the Scala plugin is not installed, I cannot determine the main scala source directory.', e)
		}
		def props = project.properties
		def type = object ? 'Object' : 'Class'
		def fullClassName = props['newClassName'] ?: props['newObjectName'] ?: TemplatesPlugin.prompt("${type} name (com.example.My${type})")
		if (fullClassName) {
			def classParts = JavaTemplatesPlugin.getClassParts(fullClassName)
			ProjectTemplate.fromUserDir {
				"${mainSrcDir}" {
					"${classParts.classPackagePath}" {
						"${classParts.className}.scala" template: '/templates/scala/scala-class.tmpl',
								className: classParts.className,
								classPackage: classParts.classPackage,
								object: object
					}
				}
			}
		} else {
			println 'No class name provided.'
		}
	}

    @SuppressWarnings( "GroovyAssignabilityCheck" )
    void apply(Project project) {
		project.task( 'createScalaClass',
            group:TemplatesPlugin.group,
            description:'Creates a new Scala class in the current project.'
        ) << {
			createScalaClass(project, false)
		}

		project.task('createScalaObject',
            group: TemplatesPlugin.group,
            description: 'Creates a new Scala object in the current project.'
        ) << {
			createScalaClass(project, true)
		}

        project.task 'createScalaProject', type: CreateScalaProjectTask

        project.task('exportScalaTemplates',
            group: TemplatesPlugin.group,
            description: 'Exports the default scala template files into the current directory.'
        ) << {
			def _ = '/templates/scala'
			def templates = [
                "$_/build.gradle.tmpl",
                "$_/scala-class.tmpl"
			]
			TemplatesPlugin.exportTemplates(templates)
		}

        project.task 'initScalaProject', type: InitScalaProjectTask
    }
}

