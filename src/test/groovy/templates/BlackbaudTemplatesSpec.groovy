package templates

import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.util.stream.Collectors

class BlackbaudTemplatesSpec extends Specification {

    private String gradleVersion = "2.14.1-bb.1.0"
    private TemporaryFolder rootFolder

    def setup() {
        rootFolder = new TemporaryFolder();
        rootFolder.create()
        GitRepo.init(rootFolder.root)
    }

    def cleanup() {
        rootFolder.delete()
    }

    def "should be able to create a basic project"() {
        given:
        def expectedFileList = [".git", ".gitignore", ".gradle", "build.gradle", "gradle", "gradlew", "gradlew.bat", "src"]

        when:
        BasicProjectBuilder.instance
                .repoDir(rootFolder.root)
                .blackbaudGradleVersion(gradleVersion)
                .clean()
                .build()

        and:
        File[] files = rootFolder.root.listFiles()

        then:
        Arrays.asList(files).stream().map{f -> f.name}.collect(Collectors.toList()).containsAll(expectedFileList)
    }

    def "should be able to create a rest project"() {
        given:
        def expectedFileList = [".git", ".gitignore", ".gradle", "build.gradle", "gradle", "gradlew", "gradlew.bat", "src"]

        when:
        RestProjectBuilder.instance
                .repoDir(rootFolder.root)
                .blackbaudGradleVersion(gradleVersion)
                .useMybatis()
                .useKafka()
                .clean()
                .build()

        and:
        File[] files = rootFolder.root.listFiles()

        then:
        Arrays.asList(files).stream().map{f -> f.name}.collect(Collectors.toList()).containsAll(expectedFileList)
    }

}
