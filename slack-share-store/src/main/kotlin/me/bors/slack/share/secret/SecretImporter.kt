package me.bors.slack.share.secret

import me.bors.slack.share.persistence.SlackShareClientId
import me.bors.slack.share.persistence.SlackShareSecret
import org.refcodes.properties.ext.obfuscation.ObfuscationPropertiesSugar
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*

// No fancy-pants encryption. Simple obfuscation to prevent automatic grabbing.
object SecretImporter {
    fun checkAndImport(force: Boolean = false) {
        if (SlackShareClientId.exists() && SlackShareSecret.exists() && !force) return

        val fullPath = SecretImporter::class.java.getResource("SecretImporter.class")?.toURI().toString()

        val decoded = String(Base64.getDecoder().decode(readFileContent(fullPath)))

        val delimiters = arrayOf("=")

        val deserialized = decoded.subSequence(1, decoded.length - 1)
            .split(", ")
            .associate {
                val split = it.split(delimiters = delimiters, limit = 2)

                split[0].substring(1) to split[1]
            }

        val builder = ObfuscationPropertiesSugar.toPropertiesBuilder()

        deserialized.forEach { builder[it.key] = it.value }

        val decrypted = ObfuscationPropertiesSugar.obfuscate(
            builder,
            this.javaClass.`package`.name
        )

        SlackShareClientId.set(decrypted["client_id"])
        SlackShareSecret.set(decrypted["secret"])
    }

    private fun readFileContent(path: String): String {
        return if (path.startsWith("jar:")) {
            readFileContentJar()
        } else {
            readFileContentFS(path)
        }
    }

    private fun readFileContentFS(path: String): String {
        val className = this.javaClass.name.replace(".", "/") + ".class"

        val resultPath = path.replace(className, "")

        val file = File("${resultPath}data.bin")

        if (!file.exists()) throw SlackShareBundledFileException()

        return file.readText(Charsets.UTF_8)
    }

    private fun readFileContentJar(): String {
        return (javaClass.getResourceAsStream("/data.bin") ?: throw SlackShareBundledFileException())
            .use {
                val bufferedReader = BufferedReader(InputStreamReader(it))
                bufferedReader.readLines()
            }.joinToString(System.lineSeparator())
    }
}

class SlackShareBundledFileException :
    RuntimeException("Data file, bundled with plugin, required for authentication was not found.")