import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.jvm)
}

group = "me.bors"
version = "0.9.15"

val javaVersion: JavaVersion = JavaVersion.VERSION_17
val intellijVersion: String by extra { "2023.3.6" }
val sinceIdeaVersion: String by extra { "233" }
val untilIdeaVersion: String by extra { "252.*" }
val userDescription: String by extra {
    """
        Plugin to share code snippets and files in Slack.
        Select any desired text snippet in any editor or any file in file editor, 
        right-click and share it to Slack.
        Select the Slack conversation, make necessary edits or type additional message,
        add quoting or code highlighting and send it.
        You can either authorize in Slack automatically via oauth, 
        or paste Slack token manually in the plugin options. 
    """
}
val changelog: String by extra {
    """         0.1 - Initial version.<br>
                0.2 - Pagination support, conversation names fix.<br>
                0.3 - Improved conversations load speed.<br>
                0.4 - Alpha release with minor changes.<br>
                0.5 - UI Improvements.<br>
                0.6 - Bug fixes, logging and test improvements.<br>
                0.7 - Improve conversations loading speed further.<br>
                0.8 - Add safe token storage. Improve loading speed again.<br>
                0.9 - Code cleanup, release candidate.<br>
                0.9.5 - Complete automatic auth. Massive refactoring.<br>
                0.9.6 - Compatibility issues fixes.<br>
                0.9.7 - Cache reloading (when wrong client_id is cached). File attachment improvements.
                Dependency updates. IDEA 2022.2.2 Compatibility.<br>
                0.9.8 - Add logo.<br>
                0.9.9 - App creation link in manual auth. Code highlighting. Dependencies update. Fixes.<br>
                0.9.10 - Workspace selection.<br>
                0.9.11 - Small Fixes. Dependencies update. Batched file bug fix.<br>
                0.9.12 - Error handling improvements. IDEA 2023.2 Support.<br>
                0.9.13 - Offline exception fixes. Dependency update. <br>
                0.9.14 - Dependency updates. <br>
                0.9.15 - Dependency updates. <br>
                <br>
                """
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    detekt {
        config.setFrom(files("$rootDir/detekt.yml"))
        buildUponDefaultConfig = true
    }

    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
        }
    }
}
