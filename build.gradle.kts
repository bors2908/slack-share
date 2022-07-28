plugins {
    id("io.gitlab.arturbosch.detekt") version "1.20.0" apply true
    id("org.jetbrains.kotlin.jvm") version "1.6.21" apply false
    id("org.jetbrains.intellij") version "1.6.0" apply false
    id("java")
}

group = "me.bors"
version = "0.9.5"

val intellijVersion by extra { "2021.3.3" }
val sinceIdeaVersion by extra { "203" }
val changelog by extra { """ 0.1 - Initial version.<br>
                0.2 - Pagination support, conversation names fix.<br>
                0.3 - Improved conversations load speed.<br>
                0.4 - Alpha release with minor changes.<br>
                0.5 - UI Improvements.<br>
                0.6 - Bug fixes, logging and test improvements.<br>
                0.7 - Improve conversations loading speed further.<br>
                0.8 - Add safe token storage. Improve loading speed again.<br>
                0.9 - Code cleanup, release candidate.<br>""" }

tasks {
    detekt {
        config = files("detekt.yml")
        buildUponDefaultConfig = true
    }
}

allprojects {
    repositories {
        mavenCentral()
    }

    tasks {
        withType<JavaCompile> {
            sourceCompatibility = JavaVersion.VERSION_11.toString()
            targetCompatibility = JavaVersion.VERSION_11.toString()
        }
    }
}

