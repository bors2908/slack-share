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


    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.slf4j:slf4j-api:1.7.36")
}

tasks {
    test {
        useJUnitPlatform()
    }

    patchPluginXml {
        sinceBuild.set(sinceIdeaVersion)
        changeNotes.set(changelog)
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}