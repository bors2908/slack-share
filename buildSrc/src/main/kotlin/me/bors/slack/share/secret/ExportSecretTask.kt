package me.bors.slack.share.secret

import java.util.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.refcodes.properties.PropertiesSugar
import org.refcodes.properties.ext.obfuscation.ObfuscationPropertiesSugar

// No fancy-pants encryption. Simple obfuscation to prevent automatic grabbing.
abstract class ExportSecretTask : DefaultTask() {

    @get:InputFile
    abstract val originalFile: RegularFileProperty

    @get:OutputFile
    abstract val obfuscatedFile: RegularFileProperty

    @get:org.gradle.api.tasks.OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        group = "secret"
        description = "export-secret"
    }

    @TaskAction
    fun run() {
        val originalPath = originalFile.get().asFile
        originalPath.setReadOnly()

        val properties = PropertiesSugar.seekFromJavaProperties(originalPath.absolutePath)
        val originalProps = properties.entries.associate { it.key to it.value }

        outputDir.get().asFile.mkdirs()
        val targetFile = obfuscatedFile.get().asFile
        targetFile.createNewFile()

        val obfuscateProperties = ObfuscationPropertiesSugar.obfuscate(
            properties,
            this.javaClass.`package`.name
        )

        val obfuscatedMap = obfuscateProperties.entries.associate { it.key to it.value }

        val message = { "Secret was not properly obfuscated." }

        assert(obfuscatedMap.entries.all { !it.value.contains(originalProps[it.key]!!) }, message)
        assert(obfuscatedMap.values.all { it.contains("decrypt:") }, message)

        val bytes = Base64.getEncoder()
            .encode(obfuscatedMap.toString().encodeToByteArray())

        targetFile.writeBytes(bytes)

        originalPath.setWritable(true)
    }
}
