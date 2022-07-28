plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
    java
}

val intellijVersion: String by rootProject.extra
val sinceIdeaVersion: String by rootProject.extra
val changelog: String by rootProject.extra

group = rootProject.group
version = rootProject.version

intellij {
    version.set(intellijVersion)
}

dependencies {
    implementation(project(":slack-share-base"))
}

tasks {
    patchPluginXml {
        sinceBuild.set(sinceIdeaVersion)
        changeNotes.set(changelog)
    }
}
