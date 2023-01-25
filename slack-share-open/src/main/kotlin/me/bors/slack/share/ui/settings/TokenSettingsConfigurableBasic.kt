package me.bors.slack.share.ui.settings

class TokenSettingsConfigurableBasic : TokenSettingsConfigurable() {
    override fun getComponent(): TokenSettingsComponent {
        val component = TokenSettingsComponentBasic(
            getManualActionListener(),
            getRemoveTokenListener(),
            getMoveUpListener(),
            getMoveDownListener()
        )

        component.setWorkspaces(workspaceService.getAllWorkspaces())

        return component
    }
}
