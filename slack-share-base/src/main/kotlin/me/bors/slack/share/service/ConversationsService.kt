package me.bors.slack.share.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.slack.api.model.Conversation
import com.slack.api.model.ConversationType
import com.slack.api.model.ConversationType.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.bors.slack.share.client.SlackConversationsClient
import me.bors.slack.share.entity.SlackConversation
import me.bors.slack.share.entity.Workspace
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

@Service
class ConversationsService {
    private val slackClient = SlackConversationsClient()

    private val workspaceService: WorkspaceService = service()

    private val nameCache: Map<Workspace, Map<String, String>> = slackClient.getNameCache(workspaceService.getAvailableWorkspaces())

    private val cache: MutableMap<Workspace, Map<ConversationType, MutableMap<String, SlackConversation>>> = mutableMapOf()

    init {
        updateCache(true)
    }

    fun getConversations(workspace: Workspace): List<SlackConversation> {
        return cache
            .getValue(workspace)
            .flatMap { (_, ids) -> ids.values }
            .sortedBy { it.priority }
    }

    fun forceRefresh() {
        updateCache(true)
    }

    private fun updateCache(clean: Boolean = false) {
        if (clean) {
            cache.clear()
        }

        val workspaces = workspaceService.getAvailableWorkspaces().toSet()

        val workspacesToDelete = cache.keys - workspaces

        val workspaceToCreate = workspaces - cache.keys

        workspacesToDelete.forEach { cache.remove(it) }

        workspaceToCreate.forEach { cache[it] = listOf(PRIVATE_CHANNEL, PUBLIC_CHANNEL, MPIM, IM).associateWith { mutableMapOf() } }

        //TODO Add coroutines
        cache.forEach { (ws, cts) ->
            for ((ct, ids) in cts) {
                val token = ws.state.get() ?: continue

                val conversations = getChannels(token, ct).associateBy { it.id }

                val idsToCreate = conversations.keys - ids.keys

                val idsToDelete = ids.keys - conversations.keys

                idsToDelete.forEach { ids.remove(it) }

                ids.putAll(
                    parseChannels(
                        idsToCreate.mapNotNull { conversations[it] },
                        ct,
                        ws
                    ).associateBy { it.id }
                )
            }
        }
    }

    private fun getChannels(token: String, type: ConversationType) = slackClient.getChannels(token, listOf(type))

    private fun parseChannels(
        conversations: List<Conversation>,
        type: ConversationType,
        workspace: Workspace
    ): List<SlackConversation> {
        return when (type) {
            PRIVATE_CHANNEL, PUBLIC_CHANNEL -> parseGroupChannels(conversations)
            IM -> parseIndividualChannels(conversations, workspace)
            MPIM -> parseMultiChannels(conversations, workspace)
            else -> emptyList()
        }
    }

    private fun parseGroupChannels(conversations: List<Conversation>): List<SlackConversation> =
        conversations.map { SlackConversation(it.id, it.nameNormalized, it.priority ?: 0.0) }

    private fun parseIndividualChannels(conversations: List<Conversation>, workspace: Workspace): List<SlackConversation> =
        conversations.map { SlackConversation(it.id, getUserName(workspace, it.user), it.priority ?: 0.0) }

    private fun parseMultiChannels(conversations: List<Conversation>, workspace: Workspace): List<SlackConversation> {
        val result = LinkedBlockingQueue<SlackConversation>()

        val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()

        runBlocking {
            conversations.forEach {
                launch(dispatcher) {
                    result.add(
                        SlackConversation(
                            it.id,
                            getMultiUserGroupName(workspace, it.id),
                            it.priority ?: 0.0
                        )
                    )
                }
            }
        }

        return result.toList()
    }

    private fun getMultiUserGroupName(workspace: Workspace, id: String) =
        slackClient.getMultiUserGroupMembers(workspace.state.get()!!, id).joinToString(", ") { getUserName(workspace, it) }

    private fun getUserName(workspace: Workspace, userId: String): String {
        return nameCache[workspace]?.get(userId) ?: slackClient.getUserName(workspace.state.get()!!, userId)
    }
}
