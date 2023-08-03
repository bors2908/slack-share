plugins {
    alias(libs.plugins.kotlin.jvm)
    java
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(libs.intellij.remote.robot)
}

repositories {
    maven {
        url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    }
}
