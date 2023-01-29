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

    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()

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

    fun refresh() {
        updateCache()
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

        runBlocking {
            cache.forEach { (ws, cts) ->
                launch(dispatcher) {
                    cts.forEach { (ct, ids) ->
                        launch(dispatcher) {
                            val token = ws.state.get() ?: return@launch

                            val conversations = getChannels(token, ct).associateBy { it.id }

                            val idsToCreate = conversations.keys - ids.keys

                            val idsToDelete = ids.keys - conversations.keys

                            idsToDelete.forEach { ids.remove(it) }

                            if (idsToCreate.isEmpty()) return@launch

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
            PRIVATE_CHANNEL, PUBLIC_CHANNEL -> parseChannelsWithCoroutines(conversations) {
                SlackConversation(
                    it.id,
                    it.nameNormalized,
                    it.priority ?: 0.0
                )
            }

            IM -> parseChannelsWithCoroutines(conversations) {
                SlackConversation(
                    it.id,
                    getUserName(workspace, it.user),
                    it.priority ?: 0.0
                )
            }

            MPIM -> parseChannelsWithCoroutines(conversations) {
                SlackConversation(
                    it.id,
                    getMultiUserGroupName(workspace, it.id),
                    it.priority ?: 0.0
                )
            }

            else -> emptyList()
        }
    }

    private fun parseChannelsWithCoroutines(
        conversations: List<Conversation>,
        parser: (Conversation) -> SlackConversation
    ): List<SlackConversation> {
        val result = LinkedBlockingQueue<SlackConversation>()

        runBlocking {
            conversations.forEach {
                launch(dispatcher) {
                    result.add(
                        parser.invoke(it)
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
