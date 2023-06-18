package me.bors.slack.share.service

import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.ProjectManager
import me.bors.slack.share.auth.Authenticator
import me.bors.slack.share.entity.Workspace
import me.bors.slack.share.ui.dialog.error.OpenSettingsErrorDialogWrapper
import me.bors.slack.share.ui.dialog.error.TokenErrorDialogWrapper
import me.bors.slack.share.ui.settings.WorkspaceSettingsComponent
import me.bors.slack.share.ui.settings.WorkspaceSettingsConfigurable
import java.awt.event.ActionEvent

abstract class SettingsService {
    protected val authenticator: Authenticator = service()

    protected val initializationService: InitializationService = service()

    protected val workspaceService: WorkspaceService = service()

    private var previousState: List<Workspace> = workspaceService.getAllWorkspaces()

    abstract val settingsComponent: WorkspaceSettingsComponent

    fun showSettings(error: String, title: String) {
        if (OpenSettingsErrorDialogWrapper(error, title).showAndGet()) {
            ShowSettingsUtil.getInstance()
                .editConfigurable(ProjectManager.getInstance().defaultProject, WorkspaceSettingsConfigurable())
        }
    }

    protected fun addToken(token: String?) {
        if (token == null) return

        val errorMessage = workspaceService.addToken(token)

        errorMessage?.let { TokenErrorDialogWrapper(it).showAndGet() }

        refreshWorkspacesList()
    }

    protected fun getManualActionListener(): (ActionEvent) -> Unit {
        return {
            addToken(authenticator.authManually())
        }
    }

    protected fun getRemoveTokenListener(): (ActionEvent) -> Unit {
        return {
            val selectedWorkspace = settingsComponent.getSelectedWorkspace()

            if (selectedWorkspace != null) {
                workspaceService.delete(selectedWorkspace)

                refreshWorkspacesList()
            }
        }
    }

    protected fun getMoveUpListener(): (ActionEvent) -> Unit {
        return {
            val selectedWorkspace = settingsComponent.getSelectedWorkspace()

            if (selectedWorkspace != null) {
                workspaceService.moveUp(selectedWorkspace)

                refreshWorkspacesList()
            }
        }
    }

    protected fun getMoveDownListener(): (ActionEvent) -> Unit {
        return {
            val selectedWorkspace = settingsComponent.getSelectedWorkspace()

            if (selectedWorkspace != null) {
                workspaceService.moveDown(selectedWorkspace)

                refreshWorkspacesList()
            }
        }
    }

    private fun refreshWorkspacesList() {
        val workspaces = workspaceService.getAllWorkspaces()

        settingsComponent.setWorkspaces(workspaces)

        previousState = workspaces
    }

    fun isModified(): Boolean {
        return previousState == workspaceService.getAllWorkspaces()
    }

    fun apply() {
        workspaceService.persist()
    }

    fun refresh() {
        workspaceService.refresh()

        refreshWorkspacesList()
    }

}

