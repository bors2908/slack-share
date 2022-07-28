package me.bors.slack.share.service

import me.bors.slack.share.ui.settings.TokenSettingsConfigurable
import me.bors.slack.share.ui.settings.TokenSettingsConfigurableBasic

class InitializationServiceBasic: InitializationService {
    override fun getTokenSettingsConfigurable(): TokenSettingsConfigurable {
        return TokenSettingsConfigurableBasic()
    }
}