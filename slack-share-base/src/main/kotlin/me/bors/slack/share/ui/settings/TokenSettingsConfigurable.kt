package me.bors.slack.share.ui.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import me.bors.slack.share.auth.Authenticator
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nls.Capitalization.Title
import java.awt.event.ActionEvent
import javax.swing.JComponent
import kotlin.properties.Delegates

abstract class TokenSettingsConfigurable : Configurable {
    private lateinit var slackShareSettingsComponent: TokenSettingsComponent

    private var previousState by Delegates.notNull<Boolean>()

    protected val authenticator : Authenticator = service()

    abstract fun getComponent(): TokenSettingsComponent

    protected fun getManualActionListener(): (ActionEvent) -> Unit {
        return {
            authenticator.authManually()
        }
    }

    protected fun getRemoveTokenListener(): (ActionEvent) -> Unit {
        return {
            authenticator.remove()
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
        previousState = authenticator.isTokenPresent()

        slackShareSettingsComponent = getComponent()

        return slackShareSettingsComponent.panel
    }

    override fun isModified(): Boolean {
        val exists = authenticator.isTokenPresent()

        slackShareSettingsComponent.setStatus(exists)

        return exists == previousState
    }

    override fun apply() {
        previousState = authenticator.isTokenPresent()
    }

    override fun reset() {
        slackShareSettingsComponent.setStatus(previousState)
    }

    override fun disposeUIResources() {
        createComponent()
    }
}
