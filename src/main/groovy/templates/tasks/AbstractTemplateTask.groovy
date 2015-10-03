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

package templates.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import templates.GitRepo
import templates.ProjectProps
import templates.TemplatesPlugin

/**
 * Abstract base class for project tasks.
 */
abstract class AbstractTemplateTask extends DefaultTask {

    protected ProjectProps projectProps

    AbstractTemplateTask(final String description) {
        this.group = TemplatesPlugin.group
        this.description = description
        this.projectProps = new ProjectProps(project)
    }

    @TaskAction
    def init() {
        try {
            renderTemplate()
        } catch (Exception ex) {
            ex.printStackTrace()
            throw ex
        }
    }

    protected abstract void renderTemplate()

    protected BasicProject createBasicProject(boolean clean) {
        GitRepo gitRepo = openOrInitGitRepo(clean)
        new BasicProject(projectProps, gitRepo)
    }

    protected BasicProject openBasicProject() {
        GitRepo gitRepo = openGitRepo(projectProps.repoDir)
        new BasicProject(projectProps, gitRepo)
    }

    private GitRepo openOrInitGitRepo(boolean clean) {
        File repoDir = projectProps.repoDir
        if (clean) {
            repoDir.deleteDir()
        }

        repoDir.exists() ? openGitRepo(repoDir) : initGitRepo(repoDir)
    }

    private GitRepo openGitRepo(File repoDir) {
        if (repoDir.exists() == false) {
            throw new GradleException("Cannot open git project, dir=${repoDir.absolutePath}")
        }
        GitRepo.open(repoDir)
    }

    private GitRepo initGitRepo(File repoDir) {
        repoDir.mkdirs()
        GitRepo git = GitRepo.init(repoDir)
        git.setRemoteUrl("origin", "git@github.com:blackbaud/${repoDir.name}.git")
        git
    }

}
