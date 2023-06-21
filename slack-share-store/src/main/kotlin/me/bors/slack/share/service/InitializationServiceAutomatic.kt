package me.bors.slack.share.service

import me.bors.slack.share.secret.SecretImporter

class InitializationServiceAutomatic : InitializationService() {
    var initializedProperly: Boolean = false
        private set

    override fun beforeInit() {
        initializedProperly = SecretImporter.checkAndImport()
    }

    override fun reloadCaches() {
        initializedProperly = SecretImporter.checkAndImport(force = true)

        super.reloadCaches()
    }
}
