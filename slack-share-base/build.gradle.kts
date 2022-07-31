plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
    java
}

group = rootProject.group
version = rootProject.version

val intellijVersion: String by rootProject.extra

intellij {
    version.set(intellijVersion)
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib")

    api("com.slack.api:slack-api-client:1.22.2") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.slf4j:slf4j-api:1.7.36")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}