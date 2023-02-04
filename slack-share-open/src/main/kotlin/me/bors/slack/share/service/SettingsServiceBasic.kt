package me.bors.slack.share.service

import me.bors.slack.share.ui.settings.TokenSettingsComponent
import me.bors.slack.share.ui.settings.TokenSettingsComponentBasic

class SettingsServiceBasic : SettingsService() {
    override val settingsComponent: TokenSettingsComponent = TokenSettingsComponentBasic(
        getManualActionListener(),
        getRemoveTokenListener(),
        getMoveUpListener(),
        getMoveDownListener(),
        workspaceService.getAllWorkspaces()
    )
}
