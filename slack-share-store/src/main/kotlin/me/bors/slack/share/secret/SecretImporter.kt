package me.bors.slack.share.secret

import me.bors.slack.share.persistence.SlackShareClientId
import me.bors.slack.share.persistence.SlackShareSecret
import org.refcodes.properties.ext.obfuscation.ObfuscationPropertiesSugar
import java.io.File
import java.util.*

// No fancy-pants encryption. Simple obfuscation to prevent automatic grabbing.
object SecretImporter {
    fun checkAndImport(force: Boolean = false) {
        if (SlackShareClientId.exists() && SlackShareSecret.exists() && !force) return

        val fullPath =
            SecretImporter::class.java.getResource("SecretImporter.class")?.path
                ?: throw UnsupportedOperationException("No File.")

        val className = this.javaClass.name.replace(".", "/") + ".class"

        val path = fullPath
            .replace("file:/", "")
            .replace(className, "")

        val file = File("${path}data.bin")

        if (!file.exists()) throw IllegalStateException("No file.")

        val decoded = String(Base64.getDecoder().decode(file.readBytes()))

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
            this.javaClass.packageName
        )

        SlackShareClientId.set(decrypted["client_id"])
        SlackShareSecret.set(decrypted["secret"])
    }
}
