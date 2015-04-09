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

        File templateFile = rootFolder.newFile( 'template.tmpl' )
        templateFile.text = 'The answer is ${bar}'

        File binaryFile = rootFolder.newFile( 'binaryFile.bin' )
        binaryFile.bytes = [0, 3, 0, 4, 1, 9, 9, 8] as byte[]

        ProjectTemplate.fromRoot( "${rootFolder.root}/$PROJECT_NAME" ){
            'stuff' {
                'README.txt' '''
                    This is a generated README file.
                '''
                'deeper' {}
                'foo' template:templateFile.toString(), bar:42
                'someBinaryFile.bin' file:binaryFile.getAbsolutePath()
            }
        }

        assert new File( rootFolder.root, PROJECT_NAME ).exists()
        assert new File( rootFolder.root, "$PROJECT_NAME/stuff" ).exists()
        assert new File( rootFolder.root, "$PROJECT_NAME/stuff/deeper" ).exists()

        assertFileText rootFolder.root, "$PROJECT_NAME/stuff/README.txt", 'This is a generated README file.'
        assertFileText rootFolder.root, "$PROJECT_NAME/stuff/foo", 'The answer is 42'
        assertFileBytes rootFolder.root, "$PROJECT_NAME/stuff/someBinaryFile.bin", [0, 3, 0, 4, 1, 9, 9, 8] as byte[]
    }

    private void assertFileBytes( File dir, String path, byte[] bytes ) {
        def targetFile = new File( dir, path )
        assert targetFile.exists()
        assert targetFile.bytes == bytes
    }

    private void assertFileText( File dir, String path, String expectedText ){
        def targetFile = new File( dir, path )
        assert targetFile.exists()
        assert targetFile.text.trim() == expectedText
    }

    @Test void 'fromRoot(File): empty'(){
        assert !(new File( rootFolder.root, PROJECT_NAME ).exists())

        ProjectTemplate.fromRoot( new File( rootFolder.root, PROJECT_NAME ) )

        assert new File( rootFolder.root, PROJECT_NAME ).exists()
    }
}
