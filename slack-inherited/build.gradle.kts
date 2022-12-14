plugins {
    java
    `java-library`
}

group = rootProject.group
version = rootProject.version

dependencies {
    api(libs.slack.api.client) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    compileOnly(libs.lombok)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.launcher)
    testImplementation(libs.slf4j.api)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}