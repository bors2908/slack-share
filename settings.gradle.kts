rootProject.name = "slack-share"
include("slack-share-base")
include("slack-share-open")
include("slack-share-store")
include("slack-inherited")

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
