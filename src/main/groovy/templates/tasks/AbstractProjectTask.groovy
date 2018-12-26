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
import templates.TemplatesPlugin

/**
 * Abstract base class for project tasks.
 */
abstract class AbstractProjectTask extends DefaultTask {

    static final String NEW_PROJECT_NAME = 'newProjectName'
    static final String PROJECT_GROUP = 'projectGroup'
    static final String PROJECT_VERSION = 'projectVersion'
    static final String PROJECT_PARENT_DIR = 'projectParentDir'

    AbstractProjectTask( final String name, final String description ){
        //this.name = name
        this.group = TemplatesPlugin.group
        this.description = description
    }

    /**
     * A solution to allow external generation directory config which also allows unit testing for init.
     * It will try a system property named 'init.dir' and then fallback to the 'user.dir' property value.
     */
    protected String defaultDir(){
        System.getProperty( 'init.dir', System.getProperty('user.dir') )
    }

    /**
     * Determine the project path directory based on the specified project name.
     *
     * If the "projectParentDir" is not specified as a property, the user will be prompted for it.
     *
     * @param projectName the project name
     * @return the project directory path
     */
    protected String projectPath( final String projectName ){
        String parentDir = project.properties[PROJECT_PARENT_DIR]
        if( parentDir ){
            return "$parentDir/$projectName"
        } else {
            String dir = TemplatesPlugin.prompt( 'Project Parent Directory:', defaultDir() )
            return "$dir/$projectName"
        }
    }

    /**
     * Determine the project name to be used. If the 'newProjectName' is not specified as a property, the user will
     * be prompted for it.
     *
     * @return the project name to be used
     */
    protected String projectName(){
        project.properties[NEW_PROJECT_NAME] ?: TemplatesPlugin.prompt('Project Name:')
    }

    /**
     * Determine the project group to be used based on the project name. If the 'projectGroup' is not specified as a
     * property, the user will be prompted for it.
     *
     * @param projectName the project name
     * @return the project group
     */
    protected String projectGroup( final String projectName ){
        project.properties[PROJECT_GROUP] ?: TemplatesPlugin.prompt('Group:', projectName.toLowerCase())
    }

    /**
     * Determine the project version to be used. If the 'projectVersion' is not specified as a property the user will
     * be prompted for it.
     *
     * @return the project version
     */
    protected String projectVersion(){
        project.properties[PROJECT_VERSION] ?: TemplatesPlugin.prompt('Version:', '0.1')
    }

    /**
     * Ensures that the specified file exists under the current default directory. The file reference is returned.
     *
     * @param file the file suffix to ensure
     * @return the existing file
     */
    protected File ensureFileExists( final String file ){
        File buildFile = new File(defaultDir(), file)

        if( !buildFile.exists() ){
            buildFile.createNewFile()
        }

        return buildFile
    }
}
