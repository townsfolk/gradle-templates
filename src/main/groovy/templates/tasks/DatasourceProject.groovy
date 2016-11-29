package templates.tasks

class DatasourceProject {

    private BasicProject basicProject

    DatasourceProject(BasicProject basicProject) {
        this.basicProject = basicProject
    }

    void initPostgres() {
        basicProject.addDockerPlugin()
        basicProject.applyPlugin("postgres")

        File componentTestPropertiesFile = basicProject.getProjectFile("src/componentTest/resources/application-componentTest.properties")
        componentTestPropertiesFile.append("""
spring.datasource.url=jdbc:postgresql://local.docker:5432/${basicProject.repoName}-test
""")

        File applicationPropertiesFile = basicProject.getProjectFile("src/main/resources/application-local.properties")
        applicationPropertiesFile.append("""
spring.datasource.url=jdbc:postgresql://local.docker:5432/${basicProject.repoName}
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.test-on-borrow=true
spring.datasource.validation-interval=30000
spring.datasource.validation-query=SELECT 1;
""")

        createLiquibaseChangeLog()
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
    }

}
