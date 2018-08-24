package com.blackbaud.templates.project

import com.blackbaud.templates.CurrentVersions
import org.gradle.api.GradleException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import com.blackbaud.templates.GitRepo
import com.blackbaud.templates.ProjectTemplate

import static com.google.common.base.CaseFormat.LOWER_CAMEL
import static com.google.common.base.CaseFormat.LOWER_HYPHEN
import static com.google.common.base.CaseFormat.UPPER_CAMEL

class BasicProject {

    @Delegate
    private GitRepo gitRepo
    private File targetDir
    String repoName
    private File gradleUserHomeDir
    private String blackbaudGradleVersion
    private ProjectProps projectProps

    BasicProject(ProjectProps projectProps, GitRepo gitRepo) {
        this.gitRepo = gitRepo
        this.repoName = gitRepo.repoDir.name
        this.targetDir = gitRepo.repoDir
        this.projectProps = projectProps
    }

    BasicProject(String blackbaudGradleVersion, GitRepo gitRepo, File gradleUserHomeDir) {
        this.gitRepo = gitRepo
        this.repoName = gitRepo.repoDir.name
        this.targetDir = gitRepo.repoDir
        this.blackbaudGradleVersion = blackbaudGradleVersion
        this.gradleUserHomeDir = gradleUserHomeDir
    }

    GitRepo getGitRepo() {
        gitRepo
    }

    ProjectProps getProjectProps() {
        projectProps
    }

    File getRepoDir() {
        gitRepo.repoDir
    }

    File getTargetDir() {
        targetDir
    }

    String getServiceName() {
        String defaultServiceName = LOWER_HYPHEN.to(UPPER_CAMEL, repoName).capitalize()
        if (projectProps != null) {
            projectProps.getOptionalProjectPropertyOrDefault("serviceName", defaultServiceName)
        } else {
            defaultServiceName
        }
    }

    String getServicePackage() {
        String defaultPackageName = "com.blackbaud.${serviceName.toLowerCase()}"
        if (projectProps != null) {
            projectProps.getOptionalProjectPropertyOrDefault("servicePackageName", defaultPackageName)
        } else {
            defaultPackageName
        }
    }

    String getServicePackagePath() {
        packageToPath(servicePackage)
    }

    private String packageToPath(String pkg) {
        pkg.replaceAll ("\\.", "/" )
    }

    void initGradleProject() {
        if (new File(repoDir, "build.gradle").exists() == false) {
            initBlackbaudGradleWrapper()

            initBasicGradleBuild()
            gitRepo.commitProjectFiles("added build.gradle")

            new File(repoDir, "src/main/java").mkdirs()
            new File(repoDir, "src/test/groovy").mkdirs()
        }
    }

    void initPerformanceTestProject() {
        File performanceTestsBuildFile = getProjectFile("performance-tests/build.gradle")
        if (performanceTestsBuildFile.exists() == false) {
            addPerformanceTestsSubmodule()
            new File("${repoDir}/performance-tests", "src/main/scala").mkdirs()

            applyTemplate("performance-tests/src/main/scala/${servicePackage}") {
                "${LOWER_HYPHEN.to(UPPER_CAMEL, repoName)}Test.scala" template: "/templates/springboot/performancetest/performance-test.scala.tmpl",
                        lowerCamelName: LOWER_HYPHEN.to(LOWER_CAMEL, repoName),
                        upperCamelName: LOWER_HYPHEN.to(UPPER_CAMEL, repoName),
                        lowerCaseName: repoName.toLowerCase().replace("-", "")
            }
        }
    }

    void initBlackbaudGradleWrapper() {
        if (new File(repoDir, "gradle/wrapper/gradle-wrapper.properties").exists() == false) {
            initGradleWrapper()
            initGitignore()
            gitRepo.commitProjectFiles("initial commit, gradle wrapper")

            replaceGradleWrapperDistributionUrl()
            gitRepo.commitProjectFiles("use blackbaud gradle")
        }
    }

    BuildFile getBuildFile() {
        ProjectFile file = getProjectFileOrFail("build.gradle")
        new BuildFile(file)
    }

    void applyPlugin(String pluginName) {
        buildFile.applyPlugin(pluginName)
    }

    private void initBasicGradleBuild() {
        applyTemplate {
            'build.gradle' ([template: "/templates/basic/build.gradle.tmpl"] + CurrentVersions.VERSION_MAP)
            'gradle.properties' template: "/templates/basic/gradle.properties.tmpl",
                    artifactId: repoName
        }
    }

    private void replaceGradleWrapperDistributionUrl() {
        File gradleWrapperProperties = new File(repoDir, "gradle/wrapper/gradle-wrapper.properties")
        String text = gradleWrapperProperties.text
        String blackbaudGradleVersion = getBlackbaudGradleVersion()
        String distributionUrl = "https://raw.githubusercontent.com/blackbaud/blackbaud-gradle-distributions/master/gradle-blackbaud-${blackbaudGradleVersion}-bin.zip"
        gradleWrapperProperties.text = text.replaceFirst(/(?m)^distributionUrl=.*/, /distributionUrl=${distributionUrl}/)
    }

    private String getBlackbaudGradleVersion() {
        if (this.blackbaudGradleVersion != null) {
            return this.blackbaudGradleVersion
        } else if (this.projectProps != null) {
            return projectProps.getRequiredProjectProperty("blackbaudGradleVersion")
        } else {
            throw new RuntimeException("Missing property blackbaudGradleVersion!")
        }
    }

    private void initGradleWrapper() {
        GradleConnector connector = GradleConnector.newConnector()
                .forProjectDirectory(repoDir)

        if (gradleUserHomeDir != null) {
            connector.useGradleUserHomeDir(gradleUserHomeDir)
        }

        ProjectConnection connection = connector.connect()
        try {
            connection.newBuild().forTasks("wrapper").run()
        } finally {
            connection.close()
        }
    }

    private void initGitignore() {
        applyTemplate {
            '.gitignore' template: "/templates/git/gitignore.tmpl"
        }
    }

    void applyTemplate(Closure closure) {
        ProjectTemplate.fromRoot(targetDir, closure)
    }

    void applyTemplate(String relativePath, Closure closure) {
        ProjectTemplate.fromRoot(new File(targetDir, relativePath), closure)
    }

    ProjectFile getProjectFile(String filePath) {
        File file = new File(targetDir, filePath)
        file.parentFile.mkdirs()
        new ProjectFile(file)
    }

    ProjectFile getProjectFileOrFail(String filePath) {
        ProjectFile file = getProjectFile(filePath)
        if (file.exists() == false) {
            throw new GradleException("Failed to resolve ${file.name} at expected location=${file.absolutePath}")
        }
        file
    }

    ProjectFile findFile(String fileName) {
        ProjectFile matchingFile = findOptionalFile(fileName)
        if (matchingFile == null) {
            throw new RuntimeException("Failed to find file with name=${fileName} from baseDir=${targetDir.absolutePath}")
        }
        matchingFile
    }

    ProjectFile findOptionalFile(String fileName) {
        File matchingFile = null
        targetDir.eachFileRecurse { File file ->
            if (file.name == fileName) {
                matchingFile = file
            }
        }
        matchingFile == null ? null : new ProjectFile(matchingFile)
    }

    ProjectFile findComponentTestConfig() {
        ProjectFile configFile = findOptionalFile("ComponentTestConfig.java")
        if (configFile == null) {
            configFile = findFile("TestConfig.java")
        }
        configFile
    }

    void appendServiceToAppDescriptor(String service) {
        ProjectFile appDescriptor = getProjectFileOrFail("src/deploy/cloudfoundry/app-descriptor.yml")
        if (appDescriptor.text.contains("services:")) {
            appDescriptor.appendAfterLine("services:", """\
    - ${service}""")

        } else {
            appDescriptor.appendAfterLine("type:", """\
  services:
    - ${service}""")

        }
    }

    void includeGradleSubmodule(String submoduleName) {
        File gradleSettings = getProjectFile("settings.gradle")
        if (gradleSettings.exists() == false) {
            gradleSettings.text = ""
        }

        if (gradleSettings.text !=~ /${submoduleName}/) {
            gradleSettings.text += "include \"${submoduleName}\"\n"
        }
    }

    void addClientSubmodule(String type) {
        File clientBuildFile = getProjectFile("${type}-client/build.gradle")
        if (clientBuildFile.exists() == false) {
            applyTemplate("${type}-client") {
                "build.gradle" ([template: "/templates/springboot/${type}/build.${type}-client.gradle.tmpl"] + CurrentVersions.VERSION_MAP)
            }

            includeGradleSubmodule("${type}-client")

            buildFile.appendAfterLine(/^dependencies \{/, "    compile project(\"${type}-client\")")
            buildFile.appendBeforeLine(/sharedTest/, "    sharedTestCompile project(path: \"${type}-client\", configuration: \"mainTestRuntime\")")
        }
    }

    void addPerformanceTestsSubmodule() {
        File performanceTestsBuildFile = getProjectFile("performance-tests/build.gradle")
        if (!performanceTestsBuildFile.exists()) {
            applyTemplate("performance-tests") {
                "build.gradle" template: "/templates/springboot/performancetest/build.gradle.tmpl",
                        lowerCamelName: LOWER_HYPHEN.to(LOWER_CAMEL, repoName),
                        upperCamelName: LOWER_HYPHEN.to(UPPER_CAMEL, repoName),
                        lowerCaseName: repoName.toLowerCase().replace("-", "")
            }

            includeGradleSubmodule("performance-tests")
        }
    }

    void addInternalApiObject(String type, String resourceName, boolean upperCamel) {
        String apiPackage = "${servicePackage}.${type.replaceAll("-", "")}"
        addApiObject(type, resourceName, "src", "src/sharedTest", apiPackage, upperCamel)
    }

    void addExternalApiObject(String type, String resourceName, boolean upperCamel) {
        addClientSubmodule(type)
        addApiObject(type, resourceName, "${type}-client/src", "${type}-client/src/mainTest", "${servicePackage}.api", upperCamel)
    }

    private void addApiObject(String type, String resourceName, String srcDir, String testDir, String apiPackage, boolean upperCamel) {
        String apiPackagePath = packageToPath(apiPackage)
        String typeUpperCamelCase = LOWER_HYPHEN.to(UPPER_CAMEL, type)
        String typeLowerCamelCase = LOWER_HYPHEN.to(LOWER_CAMEL, type)
        String resourceNameLowerCamel = UPPER_CAMEL.to(LOWER_CAMEL, resourceName)
        String randomBuilderSupportClassName = "${typeUpperCamelCase}ClientRandomBuilderSupport"

        applyTemplate("${srcDir}/main/java/${apiPackagePath}") {
            "${resourceName}.java" template: "/templates/springboot/${type}/resource-api.java.tmpl",
                                   resourceName: resourceName, packageName: apiPackage,
                                   upperCamel: upperCamel
        }
        applyTemplate("${testDir}/groovy/${apiPackagePath}") {
            "Random${resourceName}Builder.groovy" template: "/templates/test/random-client-builder.groovy.tmpl",
                                                  targetClass: resourceName, packageName: apiPackage,
                                                  qualifier: "${typeUpperCamelCase}Client"
        }

        ProjectFile randomClientBuilderSupport = getProjectFile("${testDir}/groovy/${apiPackagePath}/${randomBuilderSupportClassName}.java")
        if (randomClientBuilderSupport.exists() == false) {
            applyTemplate("${testDir}/groovy/${apiPackagePath}") {
                "${typeUpperCamelCase}ClientARandom.java" template: "/templates/test/client-arandom.java.tmpl",
                        packageName: apiPackage, qualifier: "${typeUpperCamelCase}Client"
                "${typeUpperCamelCase}ClientRandomBuilderSupport.java" template: "/templates/test/random-builder-support.java.tmpl",
                        packageName: apiPackage, qualifier: "${typeUpperCamelCase}Client"
            }

            ProjectFile coreARandom = findFile("CoreARandom.java")
            coreARandom.addImport("${apiPackage}.${randomBuilderSupportClassName}")
            coreARandom.appendAfterLine(/\s+.*CoreRandomBuilderSupport coreRandomBuilderSupport.*/, """\
    @Delegate
    private ${randomBuilderSupportClassName} ${typeLowerCamelCase}ClientRandomBuilderSupport = new ${randomBuilderSupportClassName}();"""
            )
        }
        randomClientBuilderSupport.appendToClass("""

    public Random${resourceName}Builder ${resourceNameLowerCamel}() {
        return new Random${resourceName}Builder();
    }
""")
    }

}