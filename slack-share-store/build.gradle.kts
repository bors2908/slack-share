import me.bors.slack.share.secret.*

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
    java
}

val intellijVersion: String by rootProject.extra
val sinceIdeaVersion: String by rootProject.extra
val changelog: String by rootProject.extra

group = rootProject.group
version = rootProject.version

intellij {
    version.set(intellijVersion)
}

dependencies {
    implementation(project(":slack-share-base"))
    implementation("org.refcodes:refcodes-properties-ext-obfuscation:2.2.2")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))
    implementation("com.squareup.okhttp3:okhttp-tls")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.slf4j:slf4j-api:1.7.36")
}

task<ExportSecretTask>("export")

tasks {
    test {
        useJUnitPlatform()
    }

    patchPluginXml {
        sinceBuild.set(sinceIdeaVersion)
        changeNotes.set(changelog)
    }

    classes {
        finalizedBy("export")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
