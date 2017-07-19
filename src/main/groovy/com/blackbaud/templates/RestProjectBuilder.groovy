package com.blackbaud.templates

import com.blackbaud.templates.tasks.BasicProject
import com.blackbaud.templates.tasks.RestProject

class RestProjectBuilder {

    File repoDir;
    String name;
    String blackbaudGradleVersion;
    boolean mybatis = false;
    boolean postgres = false;
    boolean kafka = false;
    boolean clean = false;
    boolean vsts = false;

    private RestProjectBuilder() {}

    public static RestProjectBuilder getInstance() {
        new RestProjectBuilder();
    }

    public RestProjectBuilder repoDir(File repoDir) {
        this.repoDir = repoDir
        this
    }

    public RestProjectBuilder blackbaudGradleVersion(String blackbaudGradleVersion) {
        this.blackbaudGradleVersion = blackbaudGradleVersion
        this
    }

    public RestProjectBuilder name(String name) {
        this.name = name;
        this
    }

    public RestProjectBuilder useMybatis() {
        this.mybatis = true
        this
    }

    public RestProjectBuilder usePostgres() {
        this.postgres = true
        this
    }

    public RestProjectBuilder useKafka() {
        this.kafka = true
        this
    }

    public RestProjectBuilder clean() {
        this.clean = true
        this
    }

    public RestProjectBuilder useVsts() {
        this.vsts = true;
        this
    }

    public RestProject build() {
        BasicProject basicProject = createBasicProject()
        RestProject restProject = new RestProject(basicProject, basicProject.serviceName)
        restProject.initRestProject()
        if (mybatis) {
            restProject.initPostgres()
            restProject.initMybatis()
        } else if (postgres) {
            restProject.initPostgres()
        }
        if (kafka) {
            restProject.initKafka()
        }
        restProject
    }

    private BasicProject createBasicProject() {
        BasicProjectBuilder basicProjectBuilder = BasicProjectBuilder.getInstance()
                .name(name)
                .repoDir(repoDir)
                .blackbaudGradleVersion(blackbaudGradleVersion)
        if(clean) {
            basicProjectBuilder.clean()
        }
        if (vsts) {
            basicProjectBuilder.useVsts()
        }
        basicProjectBuilder.build()
    }

}
