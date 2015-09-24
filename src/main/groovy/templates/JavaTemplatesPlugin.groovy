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
import templates.tasks.java.CreateJavaClassTask
import templates.tasks.java.CreateJavaProjectTask
import templates.tasks.java.InitJavaProjectTask

/**
 * Adds basic tasks for bootstrapping Java projects. Adds createJavaClass, createJavaProject,
 * exportJavaTemplates, and initJavaProject tasks.
 */
class JavaTemplatesPlugin implements Plugin<Project> {

	/**
	 * Pulls a fully qualified class name into it's parts - package, and name.
	 *
	 * @param fullClassName
	 * @return Map containing the class name, package, and package as a path.
	 */
	static getClassParts(final String fullClassName) {
		def classParts = fullClassName.split(/\./) as List
		[
				className       : classParts.pop(),
				classPackagePath: classParts.join(File.separator),
				classPackage    : classParts.join('.')
		]
	}

	void apply(Project project) {
		project.task 'createJavaClass', type: CreateJavaClassTask
		project.task 'createJavaProject', type: CreateJavaProjectTask
		project.task 'initJavaProject', type: InitJavaProjectTask
	}
}