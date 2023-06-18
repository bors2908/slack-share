package me.bors.slack.share.ui.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import me.bors.slack.share.service.SettingsService
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nls.Capitalization.Title
import javax.swing.JComponent

class WorkspaceSettingsConfigurable : Configurable {
    private val settingsService: SettingsService = service()

    @Nls(capitalization = Title)
    override fun getDisplayName(): String {
        return "Slack Share Settings"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return settingsService.settingsComponent.preferredFocusedComponent
    }

    override fun createComponent(): JComponent {
        return settingsService.settingsComponent.panel
    }

    override fun isModified(): Boolean {
        return settingsService.isModified()
    }

    override fun apply() {
        return settingsService.apply()
    }

    override fun reset() {
        //TODO Refresh after auth
        return settingsService.refresh()
    }
}
