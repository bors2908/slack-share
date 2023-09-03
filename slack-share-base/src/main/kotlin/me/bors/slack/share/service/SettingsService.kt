package me.bors.slack.share.service

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.ProjectManager
import java.awt.event.ActionEvent
import me.bors.slack.share.auth.Authenticator
import me.bors.slack.share.client.SlackConnectionTester
import me.bors.slack.share.entity.Workspace
import me.bors.slack.share.ui.dialog.error.OpenSettingsErrorDialogWrapper
import me.bors.slack.share.ui.dialog.error.SlackOfflineErrorDialogWrapper
import me.bors.slack.share.ui.dialog.error.TokenErrorDialogWrapper
import me.bors.slack.share.ui.settings.WorkspaceSettingsComponent
import me.bors.slack.share.ui.settings.WorkspaceSettingsConfigurable

abstract class SettingsService {
    protected val authenticator: Authenticator = service()

    protected val initializationService: InitializationService = service()

    protected val workspaceService: WorkspaceService = service()

    private val application: Application = ApplicationManager.getApplication()

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
            if (!SlackConnectionTester.isSlackAccessible()) {
                SlackOfflineErrorDialogWrapper().showAndGet()
            } else {
                addToken(authenticator.authManually())
            }
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
        application.executeOnPooledThread {
            if (SlackConnectionTester.isSlackAccessible()) {
                workspaceService.persist()
            }
        }
    }

    fun refresh() {
        application.executeOnPooledThread {
            if (SlackConnectionTester.isSlackAccessible()) {
                workspaceService.refresh()
            }
        }

        refreshWorkspacesList()
    }
}

