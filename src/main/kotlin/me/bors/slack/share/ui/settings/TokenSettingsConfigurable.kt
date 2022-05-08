package me.bors.slack.share.ui.settings

import com.intellij.openapi.options.Configurable
import me.bors.slack.share.persistence.SettingsState
import me.bors.slack.share.persistence.SlackUserTokenSecretState
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nls.Capitalization.Title
import javax.swing.JComponent

class TokenSettingsConfigurable : Configurable {
    private lateinit var slackShareSettingsComponent: TokenSettingsComponent

    @Nls(capitalization = Title)
    override fun getDisplayName(): String {
        return "Slack Share Settings"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return slackShareSettingsComponent.preferredFocusedComponent
    }

    override fun createComponent(): JComponent {
        slackShareSettingsComponent = TokenSettingsComponent()

        return slackShareSettingsComponent.panel
    }

    override fun isModified(): Boolean {
        return (slackShareSettingsComponent.addTokenManually != SettingsState.addTokenManually) ||
                (slackShareSettingsComponent.slackShareUserToken != SlackUserTokenSecretState.get())
    }

    override fun apply() {
        SlackUserTokenSecretState.set(slackShareSettingsComponent.slackShareUserToken)
        SettingsState.addTokenManually = slackShareSettingsComponent.addTokenManually
    }

    override fun reset() {
        slackShareSettingsComponent.slackShareUserToken = SlackUserTokenSecretState.get() ?: ""
        slackShareSettingsComponent.addTokenManually = SettingsState.addTokenManually
    }

    override fun disposeUIResources() {
        createComponent()
    }
}
