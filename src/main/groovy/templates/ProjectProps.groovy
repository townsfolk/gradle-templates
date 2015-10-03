package templates

import org.gradle.api.GradleException
import org.gradle.api.Project

class ProjectProps {

    private Project project
    private File file

    ProjectProps(Project project) {
        this.project = project
        file = project.file("gradle/custom.gradle")
    }

    File getFile() {
        file
    }

    boolean isCustomPropertiesInitialized() {
        file.exists()
    }


    void applyCustomPropertiesFile() {
        if (isCustomPropertiesInitialized()) {
            project.apply from: file
        }
    }

    void initCustomPropertiesFile() {
        file.delete()
        file << """ext.workspaceDir="~/workspace"
"""
    }

    File getWorkspaceDir() {
        String workspaceDir = expandPath(getRequiredProjectProperty('workspaceDir'))
        new File(workspaceDir)
    }

    String getRepoName() {
        getRequiredProjectProperty("repoName")
    }

    File getRepoDir() {
        String repoName = getRepoName()
        new File(getWorkspaceDir(), repoName)
    }

    boolean isPropertyDefined(String name) {
        project.properties.containsKey(name)
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
