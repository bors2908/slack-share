plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(libs.intellij.remote.robot)

    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
}

repositories {
    maven {
        url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    }
}
