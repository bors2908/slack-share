plugins {
    id("org.jetbrains.intellij") version "1.6.0"
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    id("io.gitlab.arturbosch.detekt") version "1.20.0"
    id("java")
}

group = "me.bors"
version = "0.9"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    implementation("com.slack.api:slack-api-client:1.22.2") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.slf4j:slf4j-api:1.7.36")
}

intellij {
    version.set("2021.3.3")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_11.toString()
        targetCompatibility = JavaVersion.VERSION_11.toString()
    }

    patchPluginXml {
        sinceBuild.set("203")

        changeNotes.set(
            """ 0.1 - Initial version.<br>
                0.2 - Pagination support, conversation names fix.<br>
                0.3 - Improved conversations load speed.<br>
                0.4 - Alpha release with minor changes.<br>
                0.5 - UI Improvements.<br>
                0.6 - Bug fixes, logging and test improvements.<br>
                0.7 - Improve conversations loading speed further.<br>
                0.8 - Add safe token storage. Improve loading speed again.<br>"""
        )
    }

    detekt {
        config = files("src/main/resources/detekt.yml")
        buildUponDefaultConfig = true
    }

    test {
        useJUnitPlatform()
    }
}