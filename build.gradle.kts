import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress(
    "DSL_SCOPE_VIOLATION",
    "MISSING_DEPENDENCY_CLASS",
    "UNRESOLVED_REFERENCE_WRONG_RECEIVER",
    "FUNCTION_CALL_EXPECTED"
)
plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.jvm)
}

group = "me.bors"
version = "0.9.9"

val intellijVersion by extra { "2022.2.2" }
val sinceIdeaVersion by extra { "203" }
val userDescription by extra {
    """
        <![CDATA[
        Plugin to share code snippets and files in Slack.<br>
        <br>
        Simply right-click on the desired file or text snippet in 
        file selector or any editor and then select "Share to Slack".
        <br>
        Select the Slack conversation, make necessary edits on text 
        and send it.
        <br>
        You can either authorize in Slack automatically via oauth, 
        or paste Slack token manually in the plugin options. 
    """
}
val changelog by extra {
    """         0.1 - Initial version.<br>
                0.2 - Pagination support, conversation names fix.<br>
                0.3 - Improved conversations load speed.<br>
                0.4 - Alpha release with minor changes.<br>
                0.5 - UI Improvements.<br>
                0.6 - Bug fixes, logging and test improvements.<br>
                0.7 - Improve conversations loading speed further.<br>
                0.8 - Add safe token storage. Improve loading speed again.<br>
                0.9 - Code cleanup, release candidate.
                0.9.5 - Complete automatic auth. Massive refactoring.<br>
                0.9.6 - Compatibility issues fixes.<br>
                0.9.7 - Cache reloading (when wrong client_id is cached). File attachment improvements.
                Dependency updates. IDEA 2022.2.2 Compatibility.<br>
                0.9.8 - Add logo.<br>
                """
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    repositories {
        mavenCentral()
    }

    tasks {
        withType<JavaCompile> {
            sourceCompatibility = JavaVersion.VERSION_11.toString()
            targetCompatibility = JavaVersion.VERSION_11.toString()
        }

        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_11.toString()
            }
        }

        detekt {
            config = files("$rootDir/detekt.yml")
            buildUponDefaultConfig = true
        }
    }
}

