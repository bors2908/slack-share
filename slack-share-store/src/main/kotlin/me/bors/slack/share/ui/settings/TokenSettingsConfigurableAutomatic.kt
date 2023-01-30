package me.bors.slack.share.ui.settings

import me.bors.slack.share.auth.AutomaticAuthenticator
import me.bors.slack.share.service.InitializationServiceAutomatic
import java.awt.event.ActionEvent

class TokenSettingsConfigurableAutomatic : TokenSettingsConfigurable() {
    private fun getAutomaticActionListener(): (ActionEvent) -> Unit {
        return {
            addToken((authenticator as AutomaticAuthenticator).authAutomatically())
        }
    }

    private fun getReloadCachesListener(): (ActionEvent) -> Unit {
        return {
            (initializationService as InitializationServiceAutomatic).reloadCaches()
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
