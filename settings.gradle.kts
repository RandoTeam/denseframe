pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DenseFrame"

include(":app")
include(":modules:design-system")
include(":modules:project-store")
include(":modules:capture-api")
include(":modules:capture-arcore")
include(":modules:capture-camerax")
include(":modules:reconstruction-api")
include(":modules:viewer-filament")
include(":modules:export")
include(":modules:diagnostics")
include(":modules:testing-fixtures")
