import me.bors.slack.share.logo.ExportLogoTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellij)
}

val intellijVersion: String by rootProject.extra
val sinceIdeaVersion: String by rootProject.extra
val untilIdeaVersion: String by rootProject.extra
val userDescription: String by rootProject.extra
val changelog: String by rootProject.extra

group = rootProject.group
version = rootProject.version

intellij {
    version.set(intellijVersion)
}

dependencies {
    implementation(projects.slackShareBase)
}

tasks.register<ExportLogoTask>("exportLogo") {
    originalFile.set(rootProject.layout.projectDirectory.file("logo/logo_40x40.svg"))
    resultingFile.set(rootProject.layout.projectDirectory.file("src/main/resources/META-INF/pluginIcon.svg"))
}

tasks {
    patchPluginXml {
        sinceBuild.set(sinceIdeaVersion)
        untilBuild.set(untilIdeaVersion)
        pluginDescription.set(userDescription)
        changeNotes.set(changelog)
    }

    assemble {
        dependsOn("exportLogo")
    }
}
