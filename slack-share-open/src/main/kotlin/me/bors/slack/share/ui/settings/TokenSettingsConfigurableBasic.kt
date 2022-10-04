package me.bors.slack.share.ui.settings

class TokenSettingsConfigurableBasic : TokenSettingsConfigurable() {
    override fun getComponent(): TokenSettingsComponent {
        return TokenSettingsComponentBasic(getManualActionListener(), getRemoveTokenListener())
    }
}
