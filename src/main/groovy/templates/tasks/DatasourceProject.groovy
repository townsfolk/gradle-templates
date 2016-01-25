package templates.tasks

class DatasourceProject {

    private BasicProject basicProject

    DatasourceProject(BasicProject basicProject) {
        this.basicProject = basicProject
    }

    void initPostgres() {
        File buildFile = basicProject.getProjectFileOrFail("build.gradle")

        FileUtils.appendAfterLine(buildFile, "com.blackbaud:gradle-internal:", '        classpath "com.blackbaud:gradle-docker:1.+"')
        FileUtils.appendAfterLine(buildFile, 'apply\\s+plugin:\\s+"blackbaud-internal', 'apply plugin: "docker"')
        buildFile.append("""

docker {
    container {
        imageName "postgres:9.4"
        publish "5432:5432"
        env "POSTGRES_USER=postgres"
        env "POSTGRES_PASSWORD=postgres"
    }
}

componentTest.dependsOn System.getenv("BUILD_NUMBER") ? refreshPostgres : startPostgres
""")

        File applicationPropertiesFile = basicProject.getProjectFile("src/main/resources/application.properties")
        if (applicationPropertiesFile.exists()) {
            applicationPropertiesFile.append("""
spring.datasource.url=jdbc:postgresql://local.docker:5432/
spring.datasource.username=postgres
spring.datasource.password=postgres
""")
        }

        createLiquibaseChangeLog()
    }

    private createLiquibaseChangeLog() {
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
    }

}
