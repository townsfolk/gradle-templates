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

package com.blackbaud.templates.tasks

import com.blackbaud.templates.project.BasicProject
import com.blackbaud.templates.project.RestProject
import org.gradle.api.DefaultTask
import com.blackbaud.templates.GitRepo
import com.blackbaud.templates.project.ProjectProps
import com.blackbaud.templates.BlackbaudTemplatesPlugin

/**
 * Abstract base class for project tasks.
 */
abstract class AbstractTemplateTask extends DefaultTask {

    protected ProjectProps projectProps

    AbstractTemplateTask(String description) {
        this.group = BlackbaudTemplatesPlugin.GROUP
        this.description = description
        this.projectProps = new ProjectProps(project)
    }

    protected BasicProject createBasicProject(boolean clean) {
        GitRepo gitRepo = GitRepo.openOrInitGitRepo(projectProps.repoDir, clean)

        new BasicProject(projectProps, gitRepo)
    }

    protected BasicProject openBasicProject() {
        GitRepo gitRepo = GitRepo.openGitRepo(projectProps.repoDir)
        new BasicProject(projectProps, gitRepo)
    }

    protected RestProject openRestProject() {
        BasicProject basicProject = openBasicProject()
        new RestProject(basicProject)
    }

}
