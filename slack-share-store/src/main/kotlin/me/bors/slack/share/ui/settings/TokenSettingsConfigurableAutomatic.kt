package me.bors.slack.share.ui.settings

import me.bors.slack.share.auth.AutomaticAuthenticator
import me.bors.slack.share.secret.SecretImporter
import java.awt.event.ActionEvent

class TokenSettingsConfigurableAutomatic : TokenSettingsConfigurable() {
    private fun getAutomaticActionListener(): (ActionEvent) -> Unit {
        return {
            //TODO Move out to init service.
            SecretImporter.checkAndImport()

            addToken((authenticator as AutomaticAuthenticator).authAutomatically())
        }
    }

    private fun getReloadCachesListener(): (ActionEvent) -> Unit {
        return {
            SecretImporter.checkAndImport(force = true)
        }
    }

    override fun getComponent(): TokenSettingsComponent {
        val component = TokenSettingsComponentAutomatic(
            getManualActionListener(),
            getAutomaticActionListener(),
            getRemoveTokenListener(),
            getReloadCachesListener(),
            getMoveUpListener(),
            getMoveDownListener()
        )

        component.setWorkspaces(workspaceService.getAllWorkspaces())

        return component
    }
}
