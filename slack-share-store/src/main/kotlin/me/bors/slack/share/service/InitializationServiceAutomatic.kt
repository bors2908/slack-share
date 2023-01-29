package me.bors.slack.share.service

import me.bors.slack.share.secret.SecretImporter
import me.bors.slack.share.ui.settings.TokenSettingsConfigurable
import me.bors.slack.share.ui.settings.TokenSettingsConfigurableAutomatic

class InitializationServiceAutomatic : InitializationService() {
    override fun getTokenSettingsConfigurable(): TokenSettingsConfigurable {
        return TokenSettingsConfigurableAutomatic()
    }

    override fun beforeInit() {
        SecretImporter.checkAndImport()
    }

    override fun reloadCaches() {
        SecretImporter.checkAndImport(force = true)

        super.reloadCaches()
    }
}
