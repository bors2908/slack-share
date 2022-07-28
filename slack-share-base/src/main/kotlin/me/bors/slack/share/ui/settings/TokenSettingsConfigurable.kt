package me.bors.slack.share.ui.settings

import com.intellij.openapi.options.Configurable
import me.bors.slack.share.persistence.SlackUserTokenSecretState
import me.bors.slack.share.ui.settings.dialog.AddTokenManualDialogController
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nls.Capitalization.Title
import java.awt.event.ActionEvent
import javax.swing.JComponent
import kotlin.properties.Delegates

abstract class TokenSettingsConfigurable : Configurable {
    private lateinit var slackShareSettingsComponent: TokenSettingsComponent

    private var previousState by Delegates.notNull<Boolean>()

    abstract fun getComponent(): TokenSettingsComponent

    protected fun getManualActionListener(): (ActionEvent) -> Unit {
        return {
            AddTokenManualDialogController().show()
        }
    }

    protected fun getRemoveTokenAction(): (ActionEvent) -> Unit {
        return {
            SlackUserTokenSecretState.remove()
        }
    }

    @Nls(capitalization = Title)
    override fun getDisplayName(): String {
        return "Slack Share Settings"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return slackShareSettingsComponent.preferredFocusedComponent
    }

    override fun createComponent(): JComponent {
        previousState = SlackUserTokenSecretState.exists()

        slackShareSettingsComponent = getComponent()

        return slackShareSettingsComponent.panel
    }

    override fun isModified(): Boolean {
        val exists = SlackUserTokenSecretState.exists()

        slackShareSettingsComponent.setStatus(exists)

        return exists == previousState
    }

    override fun apply() {
        previousState = SlackUserTokenSecretState.exists()
    }

    override fun reset() {
        slackShareSettingsComponent.setStatus(previousState)
    }

    override fun disposeUIResources() {
        createComponent()
    }
}
