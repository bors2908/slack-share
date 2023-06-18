package me.bors.slack.share.service

import me.bors.slack.share.secret.SecretImporter

class InitializationServiceAutomatic : InitializationService() {
    override fun beforeInit() {
        SecretImporter.checkAndImport()
    }

    override fun reloadCaches() {
        SecretImporter.checkAndImport(force = true)

        super.reloadCaches()
    }
}
