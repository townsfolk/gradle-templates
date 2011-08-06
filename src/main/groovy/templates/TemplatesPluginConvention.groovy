package templates

/**
 * @author: elberry
 * Date: 4/12/11 12:27 AM
 */
class TemplatesPluginConvention {
	String gradlePluginApplyLabel
	String gradlePluginClassName
	String sourceBasePackage

	def templates(Closure closure) {
		closure.delegate = this
		closure()
	}
}
