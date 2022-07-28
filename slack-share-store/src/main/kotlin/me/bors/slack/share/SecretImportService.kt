package me.bors.slack.share

import me.bors.slack.share.persistence.SlackShareClientId
import me.bors.slack.share.persistence.SlackShareSecret

class SecretImportService {
    init {
        checkAndImportSecret()
    }

    private fun checkAndImportSecret() {
        if (!SlackShareClientId.exists() || !SlackShareSecret.exists()) {
            val medium = decodeFile()

            SlackShareClientId.set(medium.first)
            SlackShareSecret.set(medium.second)
        }
    }

    private fun decodeFile(): Pair<String, String> {
        val file = javaClass.classLoader.getResourceAsStream("secret.bin")

        val content = String(file.readAllBytes())

        val lines = content.lines()

        return lines[0] to lines[1]
    }

}