rootProject.name = "slack-share"
include("slack-share-base")
include("slack-share-open")
include("slack-share-store")
include("slack-share-ui-tests")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include("slack-share-ui-tests")
