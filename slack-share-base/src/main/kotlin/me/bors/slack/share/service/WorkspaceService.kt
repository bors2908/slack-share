package me.bors.slack.share.service

import com.intellij.openapi.components.Service
import me.bors.slack.share.client.SlackWorkspaceClient
import me.bors.slack.share.entity.Workspace
import me.bors.slack.share.persistence.PersistentState
import me.bors.slack.share.persistence.SlackUserTokenBasicSecretState
import me.bors.slack.share.persistence.WorkspaceSecretState
import me.bors.slack.share.persistence.WorkspaceSecretState.Companion.MAX_ACCOUNTS
import java.util.*

@Service
class WorkspaceService {
    private val client = SlackWorkspaceClient()

    private var workspaces: MutableList<Workspace> = mutableListOf()

    init {
        refresh()

        // TODO To be removed in a couple of updates
        if (workspaces.isEmpty() && SlackUserTokenBasicSecretState.exists()) {
            addToken(SlackUserTokenBasicSecretState.get()!!)

            SlackUserTokenBasicSecretState.remove()

            persist()
        }

        val existingIds = getExistingIds()

        // Removes non-saved credentials to keep states clean.
        getAllowedIds()
            .filter { !existingIds.contains(it) }
            .map { WorkspaceSecretState(it) }
            .forEach { it.remove() }
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

    fun persist() {
        PersistentState.instance.myState.idOrder = workspaces.map { it.id }

        refresh()
    }

    private fun getAllowedIds() = (0 until MAX_ACCOUNTS)

    private fun getExistingWorkspaces(): MutableList<Workspace> {
        return PersistentState.instance.myState.idOrder
            .mapNotNull {
                val state = WorkspaceSecretState(it)

                val token = state.get() ?: return@mapNotNull null

                val result = client.validate(token)

                if (result.error == null) {
                    Workspace(it, result.slackId, result.name)
                } else {
                    Workspace(it, "", "❌ Invalid token: ${result.error}", false)
                }

            }
            .toMutableList()
    }

    fun removeAllTokens() {
        workspaces.clear()
    }
}
