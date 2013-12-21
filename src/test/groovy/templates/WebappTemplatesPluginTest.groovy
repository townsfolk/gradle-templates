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

package templates

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class WebappTemplatesPluginTest {

    @Rule public TemporaryFolder rootFolder = new TemporaryFolder()

    @Test void 'apply'(){
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'templates'

        assert project.tasks.createWebappProject
        assert project.tasks.exportWebappTemplates
        assert project.tasks.initWebappProject
    }

    @Test void 'createBase'(){
        def projectRoot = "${rootFolder.root}/webroot"

        def plugin = new WebappTemplatesPlugin()
        plugin.createBase( projectRoot, 'foo' )

        assert new File( projectRoot ).exists()
        assert new File( projectRoot, 'src' ).exists()
        assert new File( projectRoot, 'src/main' ).exists()
        assert new File( projectRoot, 'src/main/java' ).exists()
        assert new File( projectRoot, 'src/main/resources' ).exists()
        assert new File( projectRoot, 'src/test' ).exists()
        assert new File( projectRoot, 'src/test/java' ).exists()
        assert new File( projectRoot, 'src/test/resources' ).exists()
        assert new File( projectRoot, 'src/main/webapp' ).exists()
        assert new File( projectRoot, 'src/main/webapp/WEB-INF' ).exists()
        assert new File( projectRoot, 'src/main/webapp/WEB-INF/web.xml' ).exists()
        assert new File( projectRoot, 'LICENSE.txt' ).exists()
    }
}
