package com.blackbaud.templates.tasks

import org.gradle.api.GradleException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import com.blackbaud.templates.GitRepo
import com.blackbaud.templates.ProjectProps
import com.blackbaud.templates.ProjectTemplate

import static com.google.common.base.CaseFormat.LOWER_HYPHEN
import static com.google.common.base.CaseFormat.UPPER_CAMEL

class BasicProject {

    @Delegate
    private GitRepo gitRepo
    private File targetDir
    String repoName
    private String blackbaudGradleVersion
    private ProjectProps projectProps

    BasicProject(ProjectProps projectProps, GitRepo gitRepo) {
        this.gitRepo = gitRepo
        this.repoName = gitRepo.repoDir.name
        this.targetDir = gitRepo.repoDir
        this.projectProps = projectProps
    }

    BasicProject(String blackbaudGradleVersion, GitRepo gitRepo) {
        this.gitRepo = gitRepo
        this.repoName = gitRepo.repoDir.name
        this.targetDir = gitRepo.repoDir
        this.blackbaudGradleVersion = blackbaudGradleVersion
    }

    GitRepo getGitRepo() {
        gitRepo
    }

    String getServiceName() {
        LOWER_HYPHEN.to(UPPER_CAMEL, repoName)
    }

    String getServicePackage() {
        "com.blackbaud.${serviceName.toLowerCase()}"
    }

    String getServicePackagePath() {
        servicePackage.replaceAll ( "\\.", "/" )
    }

    File getRepoDir() {
        gitRepo.repoDir
    }

    File getTargetDir() {
        targetDir
    }

    void initGradleProject() {
        if (new File(repoDir, "build.gradle").exists() == false) {
            initGradleWrapper()
            initGitignore()
            gitRepo.commitProjectFiles("initial commit, gradle wrapper")

            replaceGradleWrapperDistributionUrl()
            gitRepo.commitProjectFiles("use blackbaud gradle")

            initBasicGradleBuild()
            gitRepo.commitProjectFiles("added build.gradle")

            initBasicTest()
            gitRepo.commitProjectFiles("add basic test")
        }
    }

    File getBuildFile() {
        getProjectFileOrFail("build.gradle")
    }

    void addDockerPlugin() {
        File buildFile = getBuildFile()

        if ((buildFile.text =~ /(?ms).*classpath.*gradle-docker.*/).matches() == false) {
            FileUtils.appendAfterLine(buildFile, "com.blackbaud:gradle-internal:", '        classpath "com.blackbaud:gradle-docker:1.+"')
        }
    }

    void applyPlugin(String pluginName) {
        FileUtils.appendAfterLine(getBuildFile(), /apply\s+plugin:\s+"blackbaud-internal/, /apply plugin: "${pluginName}"/)
    }

    private void initBasicGradleBuild() {
        applyTemplate {
            'build.gradle' template: "/templates/basic/build.gradle.tmpl"
        }
    }

    private void replaceGradleWrapperDistributionUrl() {
        File gradleWrapperProperties = new File(repoDir, "gradle/wrapper/gradle-wrapper.properties")
        String text = gradleWrapperProperties.text
        String blackbaudGradleVersion = getBlackbaudGradleVersion()
        String distributionUrl = "https://nexus-oscf-dev.blackbaudcloud.com/content/repositories/releases/com/blackbaud/gradle-blackbaud/${blackbaudGradleVersion}/gradle-blackbaud-${blackbaudGradleVersion}-bin.zip"
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
        ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(repoDir)
                .connect()
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

    private void initBasicTest() {
        applyTemplate {
            "src/test/groovy/${servicePackagePath}/${this.serviceName}Spec.groovy" template: "/templates/test/basic-spec.groovy.tmpl",
                                                                                   serviceName: serviceName, servicePackage: servicePackage
        }
    }

    void applyTemplate(Closure closure) {
        ProjectTemplate.fromRoot(targetDir, closure)
    }

    void applyTemplate(String relativePath, Closure closure) {
        ProjectTemplate.fromRoot(new File(targetDir, relativePath), closure)
    }

    File getProjectFile(String filePath) {
        File file = new File(targetDir, filePath)
        file.parentFile.mkdirs()
        file
    }

    File getProjectFileOrFail(String filePath) {
        File file = getProjectFile(filePath)
        if (file.exists() == false) {
            throw new GradleException("Failed to resolve ${file.name} at expected location=${file.absolutePath}")
        }
        file
    }

    File findFile(String fileName) {
        File matchingFile = null
        targetDir.eachFileRecurse { File file ->
            if (file.name == fileName) {
                matchingFile = file
            }
        }
        if (matchingFile == null) {
            throw new RuntimeException("Failed to find file with name=${fileName} from baseDir=${targetDir.absolutePath}")
        }
        matchingFile
    }

}