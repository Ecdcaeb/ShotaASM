### Cleanroom IDF

CleanroomIDF is a template for the development with cleanroom, based on [IdeallandFramework](https://github.com/IdeallandEarthDept/IdeallandFramework), [TemplateDevEnv](https://github.com/CleanroomMC/TemplateDevEnv/tree/master) and [1.12.2-FG6-Template](https://github.com/kappa-maintainer/1.12.2-FG6-Template)


### About the branchs

There are many branchs here.

`main` is the main branch

~~`full` is the main with the codes of idf~~

the branchs start with `_` is the contribution to upstream, do not use!

the branchs start with `__` is the expirement, do not use!

### About the action

The action is used for check.

You can edit it for you own mod build.

### How to use

- Click `use this template` at the top.

- Clone the repository you have created with this template.

- Edit `version.properties`, `gradle.properties` and `build.gradle` until it is ok.

- In the local repository, run the command `gradlew setupDecompWorkspace`

- Open the project folder in IDEA.

- Right-click in IDEA `build.gradle` of your project, and select `Link Gradle Project`, after completion, hit `Refresh All` in the gradle tab on the right.

- Run `gradlew runClient` and `gradlew runServer`, or use the auto-imported run configurations in IntelliJ like `1. Run Client`.
