# Blackbaud Project Templates

This project is used to bootstrap new projects as well as adding to existing projects.  

There are times during development where the easiest way forward is to copy a set of classes and then proceed 
renaming/deleting/etc.  This project exists b/c that process is error prone and just generally sucks.


## Initialization

This project requires a primary workspace directory to be configred - a default root directory for all subsequent operations.
First, you'll need to initialize gradle/custom.properties by executing

```./gradlew initCustomProps```

The default workspace is `..`, the directory where you checked out the gradle-templates project.


## Usage

Run the `./gradlew tasks` command and look at the group `Template tasks` to see a list of the available tasks.

#### createBasicProject

Creates a basic gradle project with a simple build.gradle file.

#### createRestProject

Creates a skeleton SpringBoot REST project (includes build.gradle, application class, and supporting classes)

#### createRestResource

Creates a resource in an existing SpringBoot REST project (includes the Resource, ResourceSpec, ResourceWireSpec, etc)