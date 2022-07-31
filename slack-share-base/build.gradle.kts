plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellij)
    java
}

group = rootProject.group
version = rootProject.version

val intellijVersion: String by rootProject.extra

intellij {
    version.set(intellijVersion)
}

dependencies {
    api(libs.kotlin.stdlib)

    api(libs.slack.api.client) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.launcher)
    testImplementation(libs.slf4j.api)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}