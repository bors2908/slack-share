package me.bors.slack.share.service

import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.ProjectManager
import me.bors.slack.share.client.SlackWorkspaceClient
import me.bors.slack.share.entity.Workspace
import me.bors.slack.share.persistence.PersistentState
import me.bors.slack.share.persistence.SlackUserTokenBasicSecretState
import me.bors.slack.share.persistence.WorkspaceSecretState
import me.bors.slack.share.persistence.WorkspaceSecretState.Companion.MAX_ACCOUNTS
import me.bors.slack.share.ui.share.dialog.TokenErrorDialogWrapper
import java.util.*

class WorkspaceService {
    private val client = SlackWorkspaceClient()

    private var workspaces: MutableList<Workspace> = mutableListOf()

    init {
        refresh()

        // TODO To be removed in a couple of updates
        if (workspaces.isEmpty() && SlackUserTokenBasicSecretState.exists()) {
            val errorMessage = addToken(SlackUserTokenBasicSecretState.get()!!)

            //TODO add original token remove

            persist()
        }

        val existingIds = getExistingIds()

        // Removes non-saved credentials to keep states clean.
        getAllowedIds()
            .filter { !existingIds.contains(it) }
            .map { WorkspaceSecretState(it) }
            .forEach { it.remove() }

        if (workspaces.isEmpty()) {
            showSettings("No token found")
        }
    }

    fun getAllWorkspaces(): List<Workspace> {
        return workspaces
    }

    fun getAvailableWorkspaces(): List<Workspace> {
        return workspaces.filter { it.valid }
    }

    fun refresh() {
        workspaces = getExistingWorkspaces()
    }

    fun moveUp(selectedWorkspace: Workspace) {
        val index = workspaces.indexOf(selectedWorkspace)

        if (workspaces.size > 1) {
            if (index > 0) {
                Collections.swap(workspaces, index, index - 1)
            }
        }
    }

    fun moveDown(selectedWorkspace: Workspace) {
        val index = workspaces.indexOf(selectedWorkspace)

        if (workspaces.size > 1) {
            if (index < workspaces.size - 1) {
                Collections.swap(workspaces, index, index + 1)
            }
        }
    }

    fun addToken(token: String): String? {
        val result = client.validate(token)

        if (result.error != null) {
            return result.error
        }

        val occupiedIds = getExistingIds()

        val freeId = getAllowedIds()
            .firstOrNull { !occupiedIds.contains(it) }
            ?: return "No more workspaces available"

        val existentSlackIds = workspaces.map { it.slackId }.toSet()

        if (existentSlackIds.contains(result.slackId)) {
            return "This workspace is already present"
        }

        val state = WorkspaceSecretState(freeId)

        state.set(token)

        workspaces.add(Workspace(freeId, result.slackId, result.name))

        return null
    }

    private fun getExistingIds() = workspaces.map { it.id }.toSet()

    fun delete(workspace: Workspace) {
        workspaces.remove(workspace)
    }

    fun getTokenMap(): Map<Int, String> {
        return workspaces.mapNotNull { workspace ->
            WorkspaceSecretState(workspace.id).get()?.let {
                workspace.id to it
            }
        }.associate {
            it.first to it.second
        }
    }

    fun persist() {
        PersistentState.instance.myState.idOrder = workspaces.map { it.id }

        refresh()
    }

    private fun getAllowedIds() = (0 until MAX_ACCOUNTS)

    private fun getExistingWorkspaces(): MutableList<Workspace> {
        return PersistentState.instance.myState.idOrder
            .map {
                val state = WorkspaceSecretState(it)

                val result = client.validate(state.get()!!)

                if (result.error == null) {
                    Workspace(it, result.slackId, result.name)
                } else {
                    TokenErrorDialogWrapper(result.error, false).showAndGet()

                    Workspace(it, "", "❌ Invalid token", false)
                }

            }
            .sortedBy { it.name }
            .toMutableList()
    }

    private fun showSettings(error: String) {
        if (TokenErrorDialogWrapper(error, true).showAndGet()) {
            val initService: InitializationService = service()

            ShowSettingsUtil.getInstance()
                .editConfigurable(ProjectManager.getInstance().defaultProject, initService.getTokenSettingsConfigurable())
        }
    }
}