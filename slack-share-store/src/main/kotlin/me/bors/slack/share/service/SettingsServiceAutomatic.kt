package me.bors.slack.share.service

import me.bors.slack.share.auth.AutomaticAuthenticator
import me.bors.slack.share.ui.settings.TokenSettingsComponent
import me.bors.slack.share.ui.settings.TokenSettingsComponentAutomatic
import java.awt.event.ActionEvent

class SettingsServiceAutomatic : SettingsService() {
    override val settingsComponent: TokenSettingsComponent = TokenSettingsComponentAutomatic(
        getManualActionListener(),
        getAutomaticActionListener(),
        getRemoveTokenListener(),
        getReloadCachesListener(),
        getMoveUpListener(),
        getMoveDownListener(),
        workspaceService.getAllWorkspaces()
    )

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
}
