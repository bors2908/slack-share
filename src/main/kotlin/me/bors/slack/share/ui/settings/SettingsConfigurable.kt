package me.bors.slack.share.ui.settings

import com.intellij.openapi.options.Configurable
import me.bors.slack.share.persistence.SettingsState
import me.bors.slack.share.persistence.SlackUserTokenSecretState
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nls.Capitalization.Title
import javax.swing.JComponent

class SettingsConfigurable : Configurable {
    private lateinit var slackShareSettingsComponent: SettingsComponent

    @Nls(capitalization = Title)
    override fun getDisplayName(): String {
        return "Slack Share Settings"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return slackShareSettingsComponent.preferredFocusedComponent
    }

    override fun createComponent(): JComponent {
        slackShareSettingsComponent = SettingsComponent()

        return slackShareSettingsComponent.panel
    }

    override fun isModified(): Boolean {
        val settings = SettingsState
        val tokenSecretState = SlackUserTokenSecretState

        return (slackShareSettingsComponent.addTokenManually != settings.addTokenManually) ||
                (slackShareSettingsComponent.slackShareUserToken != tokenSecretState.get())
    }

    override fun apply() {
        val settings = SettingsState

        SlackUserTokenSecretState.set(slackShareSettingsComponent.slackShareUserToken)
        settings.addTokenManually = slackShareSettingsComponent.addTokenManually
    }

    override fun reset() {
        val settings = SettingsState
        val tokenSecretState = SlackUserTokenSecretState

        slackShareSettingsComponent.slackShareUserToken = tokenSecretState.get() ?: ""
        slackShareSettingsComponent.addTokenManually = settings.addTokenManually
    }

    override fun disposeUIResources() {
        createComponent()
    }
}
