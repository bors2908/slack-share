package me.bors.slack.share.service

import me.bors.slack.share.ui.settings.WorkspaceSettingsComponent
import me.bors.slack.share.ui.settings.WorkspaceSettingsComponentBasic

class SettingsServiceBasic : SettingsService() {
    override val settingsComponent: WorkspaceSettingsComponent = WorkspaceSettingsComponentBasic(
        getManualActionListener(),
        getRemoveTokenListener(),
        getMoveUpListener(),
        getMoveDownListener(),
        workspaceService.getAllWorkspaces()
    )
}
