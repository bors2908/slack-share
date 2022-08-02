package me.bors.slack.share.service

import me.bors.slack.share.ui.settings.TokenSettingsConfigurable
import me.bors.slack.share.ui.settings.TokenSettingsConfigurableAutomatic

class InitializationServiceAutomatic : InitializationService {
    override fun getTokenSettingsConfigurable(): TokenSettingsConfigurable {
        return TokenSettingsConfigurableAutomatic()
    }
}