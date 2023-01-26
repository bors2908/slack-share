package me.bors.slack.share.ui.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import me.bors.slack.share.auth.Authenticator
import me.bors.slack.share.entity.Workspace
import me.bors.slack.share.service.InitializationService
import me.bors.slack.share.service.WorkspaceService
import me.bors.slack.share.ui.share.dialog.TokenErrorDialogWrapper
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nls.Capitalization.Title
import java.awt.event.ActionEvent
import javax.swing.JComponent

abstract class TokenSettingsConfigurable : Configurable {
    private lateinit var slackShareSettingsComponent: TokenSettingsComponent

    protected val authenticator: Authenticator = service()

    protected val initializationService: InitializationService = service()

    protected val workspaceService: WorkspaceService = service()

    private var previousState: List<Workspace> = workspaceService.getAllWorkspaces()

    abstract fun getComponent(): TokenSettingsComponent

    protected fun getManualActionListener(): (ActionEvent) -> Unit {
        return {
            addToken(authenticator.authManually())
        }

    }

    protected fun addToken(token: String?) {
        if (token == null) return

        val errorMessage = workspaceService.addToken(token)

        errorMessage?.let { TokenErrorDialogWrapper(it, false).showAndGet() }

        refreshWorkspacesList()
    }

    protected fun getRemoveTokenListener(): (ActionEvent) -> Unit {
        return {
            val selectedWorkspace = slackShareSettingsComponent.getSelectedWorkspace()

            if (selectedWorkspace != null) {
                workspaceService.delete(selectedWorkspace)

                refreshWorkspacesList()
            }
        }
    }

    protected fun getMoveUpListener(): (ActionEvent) -> Unit {
        return {
            val selectedWorkspace = slackShareSettingsComponent.getSelectedWorkspace()

            if (selectedWorkspace != null) {
                workspaceService.moveUp(selectedWorkspace)

                refreshWorkspacesList()
            }
        }
    }

    protected fun getMoveDownListener(): (ActionEvent) -> Unit {
        return {
            val selectedWorkspace = slackShareSettingsComponent.getSelectedWorkspace()

            if (selectedWorkspace != null) {
                workspaceService.moveDown(selectedWorkspace)

                refreshWorkspacesList()
            }
        }
    }

    private fun refreshWorkspacesList() {
        val workspaces = workspaceService.getAllWorkspaces()

        slackShareSettingsComponent.setWorkspaces(workspaces)

        previousState = workspaces
    }

    @Nls(capitalization = Title)
    override fun getDisplayName(): String {
        return "Slack Share Settings"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return slackShareSettingsComponent.preferredFocusedComponent
    }

    override fun createComponent(): JComponent {
        slackShareSettingsComponent = getComponent()

        return slackShareSettingsComponent.panel
    }

    override fun isModified(): Boolean {
        return previousState == workspaceService.getAllWorkspaces()
    }

    override fun apply() {
        workspaceService.persist()
    }

    override fun reset() {
        workspaceService.refresh()

        refreshWorkspacesList()
    }
}
