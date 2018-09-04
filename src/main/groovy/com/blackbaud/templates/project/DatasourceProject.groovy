package com.blackbaud.templates.project

import com.blackbaud.templates.CurrentVersions

class DatasourceProject {

    private RestProject restProject
    private BasicProject basicProject

    DatasourceProject(RestProject restProject) {
        this.restProject = restProject
        this.basicProject = restProject.basicProject
    }

    String getServiceName() {
        restProject.serviceName
    }

    String getServiceId() {
        restProject.serviceId
    }

    String getServicePackage() {
        restProject.servicePackage
    }

    String getServicePackagePath() {
        restProject.servicePackagePath
    }

    void initMybatis() {
        basicProject.applyPlugin("mybatis")

        basicProject.applyTemplate("src/main/resources") {
            "mybatis-generator-config.xml" template: "/templates/mybatis/mybatis-generator-config.xml.tmpl",
                    serviceId: serviceId, serviceName: serviceName.toLowerCase()
        }
    }

    void initPostgres() {
        basicProject.applyPlugin("postgres")

        applyEntityScan()
        applyPostgresCompileDependencies()
        applyPostgresApplicationProperties()
        applyTestCleanupSql()
        basicProject.appendServiceToAppDescriptor("postgres-shared")
    }

    private void applyTestCleanupSql() {
        basicProject.getProjectFile("src/sharedTest/resources/db/test_cleanup.sql") << ""

        basicProject.applyTemplate("src/sharedTest/groovy/${servicePackagePath}") {
            "PersistenceTest.java" template: "/templates/springboot/rest/persistence-test-annotation.java.tmpl",
                                   packageName: servicePackage
        }

        applyPersistenceTestAnnotationIfFileExists("ComponentTest.java")
        applyPersistenceTestAnnotationIfFileExists("IntegrationTest.java")
    }

    private void applyPersistenceTestAnnotationIfFileExists(String fileName) {
        ProjectFile testFile = basicProject.findOptionalFile(fileName)
        if (testFile != null) {
            testFile.appendAfterLine(/@Retention/,"@PersistenceTest")
        }
    }

    private void applyPostgresApplicationProperties() {
        File componentTestPropertiesFile = basicProject.getProjectFile("src/sharedTest/resources/application-test.properties")
        componentTestPropertiesFile.addPropertyWithSeparator("spring.datasource.url", "jdbc:postgresql://docker.localhost:5432/\${spring.application.name}-test")

        File applicationPropertiesFile = basicProject.getProjectFile("src/main/resources/application-local.properties")
        applicationPropertiesFile.append("""
spring.datasource.url=jdbc:postgresql://docker.localhost:5432/\${spring.application.name}
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.test-on-borrow=true
spring.datasource.validation-interval=30000
spring.datasource.validation-query=SELECT 1;
""")
    }

    private applyPostgresCompileDependencies() {
        basicProject.buildFile.appendAfterLine('compile "com.blackbaud:common-spring-boot-rest:', '''\
    compile "com.blackbaud:common-spring-boot-persistence:${commonSpringBootVersion}"
    compile "postgresql:postgresql:9.0-801.jdbc4"
    compile "org.liquibase:liquibase-core"'''
        )
    }

    private void applyEntityScan() {
        ProjectFile applicationClassFile = basicProject.findFile("${serviceName}.java")
        applicationClassFile.addImport("org.springframework.boot.autoconfigure.domain.EntityScan")
        applicationClassFile.addClassAnnotation("@EntityScan({\"${servicePackage}\", \"com.blackbaud.boot.converters\"})")
    }

    private createLiquibaseChangeLog() {
        getLiquibaseChangeLog().parentFile.mkdirs()
        getLiquibaseChangeLog().text = """databaseChangeLog:
"""
    }

    private File getLiquibaseChangeLog() {
        basicProject.getProjectFile("src/main/resources/db/changelog/db.changelog-master.yaml")
    }

    void addCreateTableScript(String tableName) {
        basicProject.applyTemplate("src/main/resources/db") {
            "create_${tableName}.sql" template: "/templates/liquibase/create-table.sql.tmpl",
                    tableName: tableName
        }

        File liquibaseChangelog = getLiquibaseChangeLog()
        if (liquibaseChangelog.exists() == false) {
            createLiquibaseChangeLog()
        }
        liquibaseChangelog.append("""
  - include:
      file: db/create_${tableName}.sql
""")

        File cleanupSql = basicProject.findOptionalFile("test_cleanup.sql")
        if (cleanupSql != null) {
            cleanupSql.append("truncate table ${tableName};\n")
        }
    }

    void initCosmos() {
        basicProject.applyPlugin("mongo")

        addCosmosConfig()
        applyCommonCosmosCompileDependencies()
        applyCosmosApplicationProperties()
    }

    private applyCommonCosmosCompileDependencies() {
        BuildFile buildFile = basicProject.buildFile

        if (buildFile.text.contains("commonSpringBootMajorVersion") == false) {
            buildFile.appendAfterLine(/springBootVersion\s*=/, """\
        commonSpringBootMajorVersion = "${CurrentVersions.COMMON_SPRING_BOOT_MAJOR_VERSION}\"""")
            buildFile.replaceLine(/commonSpringBootVersion\s*=/, '''\
        commonSpringBootVersion = "${springBootVersion}-${commonSpringBootMajorVersion}.+"''')
        }
        buildFile.appendAfterLine(/commonSpringBootVersion\s*=/, """\
        commonCosmosVersion = "\${springBootVersion}-\${commonSpringBootMajorVersion}-${CurrentVersions.COMMON_COSMOS_MAJOR_VERSION}.+\"""")

        // TODO: for some reason, if spring-data-commons and spring-data-mongodb are not included (they are transitive
        // deps of common-cosmos), the wrong version will be pulled in... this is possibly due to default versions
        // pulled in by the spring-boot plugin.  need to investigate.
        buildFile.appendAfterLine('compile "com.blackbaud:common-spring-boot-rest:', '''\
    compile "com.blackbaud:common-cosmos:${commonCosmosVersion}"
    compile "org.springframework.data:spring-data-commons:1.13.8.RELEASE"
    compile "org.springframework.data:spring-data-mongodb:1.10.11.RELEASE"''')

        ProjectFile applicationClass = basicProject.findFile("${basicProject.serviceName}.java")
        applicationClass.addImport("${servicePackage}.config.CosmosConfig")
        applicationClass.addImport("org.springframework.context.annotation.Import")
        applicationClass.addConfigurationImport("CosmosConfig.class")
    }

    private void addCosmosConfig() {
        basicProject.applyTemplate("src/main/java/${servicePackagePath}/config") {
            "CosmosConfig.java" template: "/templates/cosmos/cosmos-config.java.tmpl",
                    servicePackage: "${servicePackage}.config", basePackage: "${servicePackage}.core.domain"
        }
    }

    private void applyCosmosApplicationProperties() {
        ProjectFile componentTestPropertiesFile = basicProject.getProjectFile("src/componentTest/resources/application-componentTest.properties")
        componentTestPropertiesFile.addPropertyWithSeparator("spring.data.mongodb.database", '${spring.application.name}-test')

        ProjectFile applicationPropertiesFile = basicProject.getProjectFile("src/main/resources/application-local.properties")
        applicationPropertiesFile.addPropertyWithSeparator("spring.data.mongodb.uri", "mongodb://docker.localhost:27017")
    }

}
