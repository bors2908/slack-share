package me.bors.slack.share.service

import java.awt.event.ActionEvent
import me.bors.slack.share.auth.AutomaticAuthenticator
import me.bors.slack.share.client.SlackConnectionTester
import me.bors.slack.share.ui.dialog.error.ErrorDialogWrapper
import me.bors.slack.share.ui.dialog.error.SlackOfflineErrorDialogWrapper
import me.bors.slack.share.ui.settings.WorkspaceSettingsComponentAutomatic

class SettingsServiceAutomatic : SettingsService() {
    override val settingsComponent: WorkspaceSettingsComponentAutomatic = WorkspaceSettingsComponentAutomatic(
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
            if (!SlackConnectionTester.isSlackAccessible()) {
                SlackOfflineErrorDialogWrapper().showAndGet()
            } else {
                if ((initializationService as InitializationServiceAutomatic).initializedProperly) {
                    addToken((authenticator as AutomaticAuthenticator).authAutomatically())
                } else {
                    ErrorDialogWrapper(
                        "Plugin data was corrupted. <br>" +
                            "Sorry. You wouldn't be able to authenticate in Slack automatically. <br>" +
                            "You can try using 'Reload Caches' button in plugin settings or reinstalling " +
                            "the plugin from Store. <br>"
                    ).showAndGet()
                }
            }
        }
    }

    private fun getReloadCachesListener(): (ActionEvent) -> Unit {
        return {
            (initializationService as InitializationServiceAutomatic).reloadCaches()
        }
    }
}
