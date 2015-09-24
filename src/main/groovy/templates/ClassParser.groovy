package templates

class ClassParser {

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

}
