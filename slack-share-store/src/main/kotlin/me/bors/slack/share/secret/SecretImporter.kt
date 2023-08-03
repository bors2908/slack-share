package me.bors.slack.share.secret

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import me.bors.slack.share.persistence.ShareClientId
import me.bors.slack.share.persistence.SlackShareBasicSecret
import org.refcodes.properties.ext.obfuscation.ObfuscationPropertiesSugar

// No fancy-pants encryption. Simple obfuscation to prevent automatic grabbing.
object SecretImporter {
    fun checkAndImport(force: Boolean = false): Boolean {
        if (ShareClientId.exists() && SlackShareBasicSecret.exists() && !force) return true

        val fullPath = SecretImporter::class.java.getResource("SecretImporter.class")?.toURI().toString()

        val fileContent = readFileContent(fullPath) ?: return false

        val decoded = String(Base64.getDecoder().decode(fileContent))

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

        ShareClientId.set(decrypted["client_id"])
        SlackShareBasicSecret.set(decrypted["secret"])

        return true
    }

    private fun readFileContent(path: String): String? {
        return if (path.startsWith("jar:")) {
            readFileContentJar()
        } else {
            readFileContentFS(path) ?: readFileContentJar()
        }
    }

    private fun readFileContentFS(path: String): String? {
        val className = this.javaClass.name.replace(".", "/") + ".class"

        val resultPath = path.replace(className, "")

        val file = File("${resultPath}data.bin")

        if (!file.exists()) return null

        return file.readText(Charsets.UTF_8)
    }

    private fun readFileContentJar(): String? {
        return javaClass.getResourceAsStream("/data.bin")
            ?.use {
                val bufferedReader = BufferedReader(InputStreamReader(it))
                bufferedReader.readLines()
            }
            ?.joinToString(System.lineSeparator())
    }
}
