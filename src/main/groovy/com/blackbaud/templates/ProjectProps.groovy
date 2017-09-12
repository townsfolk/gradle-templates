package com.blackbaud.templates

import org.gradle.api.GradleException
import org.gradle.api.Project

class ProjectProps {

    private Project project

    ProjectProps(Project project) {
        this.project = project
    }

    boolean isThisProjectGradleTemplates() {
        project.hasProperty("artifactId") && project.ext.artifactId == "gradle-templates"
    }

    void applyCustomPropertiesFile() {
        if (customPropertiesFile.exists() == false) {
            initCustomPropertiesFile()
        }
        project.apply from: customPropertiesFile
    }

    private File getCustomPropertiesFile() {
        project.file("gradle/custom.gradle")
    }

    private void initCustomPropertiesFile() {
        customPropertiesFile << """ext.workspaceDir=".."
"""
    }

    File getWorkspaceDir() {
        String workspaceDir = expandPath(getRequiredProjectProperty('workspaceDir'))
        new File(workspaceDir)
    }

    /**
     * If this is the gradle-templates project, the target repo directory is determined via a required project
     * property 'repoName'.  Otherwise, returns the current project directory.
     */
    File getRepoDir() {
        isThisProjectGradleTemplates() ? new File(getWorkspaceDir(), getRepoName()) : project.projectDir
    }

    private String getRepoName() {
        getRequiredProjectProperty("repoName")
    }

    boolean isPropertyDefined(String name) {
        project.properties.containsKey(name)
    }

    String getOptionalProjectPropertyOrDefault(String propertyName, String defaultValue) {
        String value = defaultValue
        if (project.properties.containsKey(propertyName)) {
            value = project.ext[propertyName]
        }
        value
    }

    String getRequiredProjectProperty(String propertyName) {
        if (!project.properties.containsKey(propertyName)) {
            throw new GradleException("Required property '${propertyName}' is not set")
        }
        project.ext[propertyName]
    }

    private String expandPath(String path) {
        String expandedPath = path.startsWith("~" + File.separator) ? System.getProperty("user.home") + path.substring(1) : path

        if (!expandedPath.startsWith(File.pathSeparator)) {
            File projectFile = project.file(expandedPath)
            if (projectFile.exists()) {
                expandedPath = projectFile.absolutePath
            }
        }
        expandedPath
    }

}
