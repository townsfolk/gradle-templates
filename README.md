# Blackbaud Project Templates

This project is used to bootstrap new projects as well as adding to existing projects.  

There are times during development where the easiest way forward is to copy a set of classes and then proceed 
renaming/deleting/etc.  This project exists b/c that process is error prone and just generally sucks.


## Initialization

This project requires a primary workspace directory to be configured - a default root directory for all subsequent operations.

The default workspace is `..`, the directory where you checked out the gradle-templates project.  This value is stored
in `gradle/custom.gradle` which is initialized when the plugin is first applied.  Edit this file if you want to change 
the workspace directory.


## Usage

There are two intended uses for this project - as a creator of new projects and augmentor of existing projects.

Run the `./gradlew tasks` command and look at the group `Template tasks` to see a list of the available tasks.


### Project Creation Tasks

The project creation tasks are only available when run from this project.  All tasks require the project property
`repoName` to be specified in order to identify the target directory.  

#### createLibraryProject

Creates a basic gradle project with a simple build.gradle file.

Supported task options:
* clean - if the target directory already exists, delete it

#### createDeployableProject

Creates a skeleton SpringBoot project (includes build.gradle, application class, and supporting classes)

You WILL get an error when running `./gradlew bootRun`:
* After creating your project, update application.properties `server.port` and `management.port` values following
the convention described in the [Blackbaud Wiki](https://wiki.blackbaud.com/display/LUM/Microservice+Port+Mapping+Registry)

Supported task options:
* clean - if the target directory already exists, delete it
* postgres - initializes the project with a postgres container and supporting files
* mybatis - initializes the project with a mybatis generator config and applies the mybatis plugin; also, applies the postgres option above
* kafka - initializes the project with a kafka container and supporting files
* serviceName - the name of the SpringBoot application entrypoint, defaults to the repository name, converted to upper camel-case
* servicePackageName - the name of the core package (contains the application entrypoint), defaults to "com.blackbaud.${serviceName.toLowercase()}"

### Project Augmentation Tasks

The project augmentation tasks are available when run from this project but also when the `blackbaud-templates` plugin
is applied to another project like so...  
```
buildscript {
    dependencies {
        classpath "com.blackbaud:gradle-templates:2+"
    }
}

apply plugin: "blackbaud-templates"
```

In this case, the target directory will be the project which applies the plugin and the project property `repoName` 
will be ignored.

NOTE: new projects will generate the following block...
```
  ext {
        // used by the various gradle-templates task to determine the base service package
        servicePackageName="com.blackbaud.reponame"
  }
```
This property is used by gradle-templates to determine the name of the base package in which to place the generated files.
If the target project is old and does not have this line, you may need to add it if your package name
does not correspond to your repo name.


#### addKafkaContainer

Add a Kafka docker container, dependencies and default configuration to an existing project

#### addPostgresContainer

Adds a Postgres docker container, dependencies and default configuration to an existing project

#### createResource

Creates a resource in an existing SpringBoot REST project (includes the Resource, ResourceSpec, ResourceWireSpec, optional JPA objects, etc).
* You may need to update the package for application.properties swagger.resource.package if your resource does not live in com.blackbaud.<servicePackageName>.resources

Required task options:
* resourceName - name of the resource to create (excluding the trailing Resource as this will be appended automatically)

Optional task options:
* suppressEntity - do not create a corresponding JPA objects and supporting classes
* addWireSpec - generate a stub ResourceWireSpec in addition to the standard ResourceSpec class

#### addRestApiObject

Creates a REST Api object and corresponding random builder.  Will also create the rest-client submodule and random 
builder supporting classes if not already created.

Required task options:
* name - name of the api object to create

Optional task options:
* upperCamel - use UpperCamelCaseStrategy when reading/writing the JSON properties

#### addJpaObject

Creates a Jpa Entity, Repository, random builder, initial liquibase create table script, and adds the table to the test_cleanup.sql file.

Required task options:
* name - name of the resource to create (excluding the trailing Entity as this will be appended automatically)

#### addKafkaMessage

Creates a Kafka message and corresponding random builder.  Will also create the kafka-client submodule and random
builder supporting classes if not already created.

Required task options:
* name - name of the message to create (excluding the trailing Message as this will be appended automatically)

