package templates

import groovy.text.GStringTemplateEngine

/**
 * This class is used to construct a ProjectTemplate. A project template consists of files and directories. This builder
 * can be used to set up the necessary files and directories needed for new projects.
 *
 * Eg.
 * <pre>
 * ProjectTemplate.fromUserDir {
 *    directory('src') { // creates new directory named 'src'
 *       dir('main') { // creates a new directory named 'main'
 *          d('java') { // creates a new directory named 'java'
 *             file('Class1.java') // creates a new file named 'Class1.java'
 *             f('Class2.java') // creates a new file named 'Class2.java'
 *          }
 *       }
 *    }
 * }
 * </pre>
 *
 * Can also be used without method calls for directory and file.
 * Eg.
 * <pre>
 * ProjectTemplate.fromUserDir {
 *    'src/main' { // creates the directories 'src', and 'main'.
 *       'java' {
 *          'Class1.java' 'public class Class1 {}' // creates the file 'Class1.java' with some initial content.
 *       }
 *       'resources' {}
 *    }
 * }
 * </pre>
 * @author: elberry
 * Date: 4/9/11 6:04 PM
 */
class ProjectTemplate {

	private File parent

	/**
	 * Private so that it can't be accessed. Use one of the static 'fromUserDir' methods to start building a template.
	 */
	private ProjectTemplate() {}

	/**
	 * Same as the directory method.
	 * @param name
	 * @param closure
	 * @see #directory(String, Closure)
	 */
	void d(String name, Closure closure = {}) {
		directory(name, closure)
	}
	/**
	 * Same as the directory method.
	 * @param name
	 * @param closure
	 * @see #directory(String, Closure)
	 */
	void dir(String name, Closure closure = {}) {
		directory(name, closure)
	}
	/**
	 * Creates a directory, and it's parents if they don't already exist.
	 * @param name
	 * @param closure
	 * @see #directory(String, Closure)
	 */
	void directory(String name, Closure closure = {}) {
		File oldParent = parent
		if (parent) {
			parent = new File(parent, name)
		} else {
			parent = new File(name)
		}
		parent.mkdirs()
		closure.delegate = this
		closure()
		parent = oldParent
	}

	/**
	 * Same as file method
	 * @param args
	 * @param name
	 * @see #file(Map, String)
	 */
	void f(Map args = [:], String name) {
		file(args, name)
	}
	/**
	 * Same as file method
	 * @param args
	 * @param name
	 * @see #file(String, String)
	 */
	void f(String name, String content) {
		file(name, content)
	}
	/**
	 * Creates a new file with the given name. If a 'content' argument is provided it will be appended, or replace the
	 * content of the current file (if it exists) based on the value of the 'append' argument.
	 * @param args Arguments to be used when creating the new file: [content: String, append: boolean]
	 * @param name Name of the new file to be created.
	 */
	void file(Map args = [:], String name) {
		File file
		if (parent) {
			file = new File(parent, name)
		} else {
			file = new File(name)
		}
		file.exists() ?: file.parentFile.mkdirs() && file.createNewFile()
		def content
		if (args.content) {
			content = args.content.stripIndent()
		} else if (args.template) {
			content = renderTemplate(args, args.template)
		}
		if (content) {
			if (args.append) {
				file.append(content)
			} else {
				file.text = content
			}
		}
	}

	String renderTemplate(Map params = [:], String template) {
		//println "Rendering template - path: ${template}, params: ${params}"
		def tLoc = new File(template)
		if (!tLoc.exists()) { // check given path
			//println '   couldn\'t find file at given path, trying relative path.'
			def rTemplate = template
			if (rTemplate.startsWith('/')) {
				rTemplate = rTemplate - '/'
			}
			tLoc = new File(System.getProperty('user.dir'), rTemplate)
			if (!tLoc.exists()) { // check relative path from current working dir.
				//println '   couldn\'t find at relative path either, using classpath.'
				tLoc = getClass().getResource(template) // last ditch, use classpath.
			}
		}
		def tReader = tLoc?.newReader()
		if (tReader) {
			return new GStringTemplateEngine().createTemplate(tReader)?.make(params)?.toString()
		}
		throw new RuntimeException("Could not locate template: ${template}")
	}

	/**
	 * Calls file([content: content], name)
	 * @param name
	 * @param content
	 * @see #file(Map, String)
	 */
	void file(String name, String content) {
		file([content: content], name)
	}

	/**
	 * Starts the ProjectTemplate in the "user.dir" directory.
	 * @param closure
	 */
	static void fromUserDir(Closure closure = {}) {
		new ProjectTemplate().directory(System.getProperty('user.dir'), closure)
	}

	/**
	 * Starts the ProjectTemplate in the given path.
	 * @param path String path to the root of the new project.
	 * @param closure
	 */
	static void fromRoot(String path, Closure closure = {}) {
		new ProjectTemplate().directory(path, closure)
	}

	/**
	 * Starts the ProjectTemplate in the given file path.
	 * @param pathFile File path to the root of the new project.
	 * @param closure
	 */
	static void fromRoot(File pathFile, Closure closure = {}) {
		new ProjectTemplate().directory(pathFile.path, closure)
	}

	/**
	 * Handles creation of files or directories without the need to specify directly.
	 * @param name
	 * @param args
	 * @return
	 */
	def methodMissing(String name, def args) {
		if (args) {
			def arg = args[0]
			if (arg instanceof Closure) {
				directory(name, arg)
			} else if (arg instanceof Map) {
				file(arg, name)
			} else if (arg instanceof String || arg instanceof GString) {
				file([content: arg], name)
			} else {
				println "Couldn't figure out what to do. name: ${name}, arg: ${arg}, type: ${arg.getClass()}"
			}
		}
	}
}