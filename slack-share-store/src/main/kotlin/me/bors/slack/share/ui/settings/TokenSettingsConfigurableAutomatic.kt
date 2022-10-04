package me.bors.slack.share.ui.settings

import me.bors.slack.share.auth.AutomaticAuthenticator
import me.bors.slack.share.secret.SecretImporter
import java.awt.event.ActionEvent

class TokenSettingsConfigurableAutomatic : TokenSettingsConfigurable() {
    private fun getAutomaticActionListener(): (ActionEvent) -> Unit {
        return {
            SecretImporter.checkAndImport()

            (authenticator as AutomaticAuthenticator).authAutomatically()
        }
    }

    private fun getReloadCachesListener(): (ActionEvent) -> Unit {
        return {
            SecretImporter.checkAndImport(force = true)
        }
    }

    override fun getComponent(): TokenSettingsComponent {
        return TokenSettingsComponentAutomatic(
            getManualActionListener(),
            getAutomaticActionListener(),
            getRemoveTokenListener(),
            getReloadCachesListener()
        )
    }
}
