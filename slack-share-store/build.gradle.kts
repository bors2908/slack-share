import me.bors.slack.share.logo.ExportLogoTask
import me.bors.slack.share.secret.ExportSecretTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellij)
    java
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
    implementation(libs.refcodes.obfuscation)

    implementation(libs.okhttp.tls)

    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.junit.vintage)
    testImplementation(libs.junit.platform.launcher)
}


task<ExportLogoTask>("exportLogo")
task<ExportSecretTask>("exportSecret")

tasks {
    test {
        useJUnitPlatform()
    }

    patchPluginXml {
        sinceBuild.set(sinceIdeaVersion)
        untilBuild.set(untilIdeaVersion)
        pluginDescription.set(userDescription)
        changeNotes.set(changelog)
    }

    classes {
        finalizedBy("exportSecret")
    }

    assemble {
        dependsOn("exportLogo")
    }

    kotlin {
        jar {
            from("build/classes/kotlin/main/") {
                include("**/data.bin")
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
        }
    }

    signPlugin {
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
        certificateChain.set(
            File("${project.rootDir}/secrets/chain.crt").readText(Charsets.UTF_8)
        )
        certificateChain.set(
            File("${project.rootDir}/secrets/private.pem").readText(Charsets.UTF_8)
        )
    }

    publishPlugin {
        token.set(File("${project.rootDir}/secrets/publish.tkn").readText(Charsets.UTF_8))
    }

    runIdeForUiTests {
        systemProperty("robot-server.port", "8580")
    }

    downloadRobotServerPlugin {
        version.set(libs.intellij.remote.robot.get().version)
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
