package me.bors.slack.share.secret

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.refcodes.properties.PropertiesSugar
import org.refcodes.properties.ext.obfuscation.ObfuscationPropertiesSugar
import java.io.File
import java.util.*

// No fancy-pants encryption. Simple obfuscation to prevent automatic grabbing.
open class ExportSecretTask : DefaultTask() {
    init {
        group = "secret"
        description = "export-secret"
    }

    @TaskAction
    fun run() {
        val I = System.getProperty("file.separator")

        val originalPath = "${project.rootDir}${I}secret.properties"

        val originalFile = File(originalPath)

        originalFile.setReadOnly()

        val properties = PropertiesSugar.seekFromJavaProperties(originalPath)

        val originalProps = properties.entries.associate { it.key to it.value }

        val dirPath = "${project.projectDir}${I}build${I}classes${I}kotlin${I}main"

        val dir = File(dirPath)

        dir.mkdirs()

        val path = "$dirPath${I}data.bin"

        val file = File(path)

        file.createNewFile()

        val obfuscateProperties = ObfuscationPropertiesSugar.obfuscate(
            properties,
            this.javaClass.packageName
        )

        val obfuscatedMap = obfuscateProperties.entries.associate { it.key to it.value }

        assert(obfuscatedMap.entries.firstOrNull { it.value.contains(originalProps[it.key]!!) } == null)

        val bytes = Base64.getEncoder()
            .encode(obfuscatedMap.toString().encodeToByteArray())

        file.writeBytes(bytes)

        originalFile.setWritable(true)
    }
}