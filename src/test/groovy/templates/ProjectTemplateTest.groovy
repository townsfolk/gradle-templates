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
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ProjectTemplateTest {

    private static final String PROJECT_NAME = 'some_project'
    @Rule public TemporaryFolder rootFolder = new TemporaryFolder()

    @Test void 'fromRoot(String): empty'(){
        assert !(new File( rootFolder.root, PROJECT_NAME ).exists())

        ProjectTemplate.fromRoot( "${rootFolder.root}/$PROJECT_NAME" )

        assert new File( rootFolder.root, PROJECT_NAME ).exists()
    }

    @Test void 'fromRoot(String): with content'(){
        assert !(new File( rootFolder.root, PROJECT_NAME ).exists())

        ProjectTemplate.fromRoot( "${rootFolder.root}/$PROJECT_NAME" ){
            'stuff' {
                'README.txt' '''
                    This is a generated README file.
                '''
                'deeper' {}
            }
        }

        assert new File( rootFolder.root, PROJECT_NAME ).exists()
        assert new File( rootFolder.root, "$PROJECT_NAME/stuff" ).exists()
        assert new File( rootFolder.root, "$PROJECT_NAME/stuff/deeper" ).exists()

        def readmeFile = new File( rootFolder.root, "$PROJECT_NAME/stuff/README.txt" )
        assert readmeFile.exists()
        assert readmeFile.text.trim() == 'This is a generated README file.'
    }

    @Test void 'fromRoot(File): empty'(){
        assert !(new File( rootFolder.root, PROJECT_NAME ).exists())

        ProjectTemplate.fromRoot( new File( rootFolder.root, PROJECT_NAME ) )

        assert new File( rootFolder.root, PROJECT_NAME ).exists()
    }
}
