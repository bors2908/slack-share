@Suppress(
    "DSL_SCOPE_VIOLATION",
    "MISSING_DEPENDENCY_CLASS",
    "UNRESOLVED_REFERENCE_WRONG_RECEIVER",
    "FUNCTION_CALL_EXPECTED"
)
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
    implementation(projects.slackShareBase)
    implementation(projects.slackShareStore)

    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.junit.vintage)
    testImplementation(libs.junit.platform.launcher)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
