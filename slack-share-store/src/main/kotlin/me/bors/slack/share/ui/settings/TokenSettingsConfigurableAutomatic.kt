package me.bors.slack.share.ui.settings

import me.bors.slack.share.secret.SecretImporter
import me.bors.slack.share.ui.settings.dialog.AddTokenAutomaticDialogController
import java.awt.event.ActionEvent

class TokenSettingsConfigurableAutomatic : TokenSettingsConfigurable() {
    private fun getAutomaticActionListener(): (ActionEvent) -> Unit {
        return {
            SecretImporter.checkAndImport()

            AddTokenAutomaticDialogController().show()
        }
    }

    override fun getComponent(): TokenSettingsComponent {
        return TokenSettingsComponentAutomatic(
            getManualActionListener(),
            getAutomaticActionListener(),
            getRemoveTokenAction()
        )
    }
}
