import me.bors.slack.share.logo.ExportLogoTask

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

val intellijVersion: String by rootProject.extra
val sinceIdeaVersion: String by rootProject.extra
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

task<ExportLogoTask>("exportLogo")

tasks {
    patchPluginXml {
        sinceBuild.set(sinceIdeaVersion)
        pluginDescription.set(userDescription)
        changeNotes.set(changelog)
    }

    assemble {
        dependsOn("exportLogo")
    }
}
