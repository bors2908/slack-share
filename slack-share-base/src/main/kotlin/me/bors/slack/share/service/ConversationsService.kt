package me.bors.slack.share.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.slack.api.model.Conversation
import com.slack.api.model.ConversationType
import com.slack.api.model.ConversationType.IM
import com.slack.api.model.ConversationType.MPIM
import com.slack.api.model.ConversationType.PRIVATE_CHANNEL
import com.slack.api.model.ConversationType.PUBLIC_CHANNEL
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.bors.slack.share.client.SlackConversationsClient
import me.bors.slack.share.entity.SlackConversation
import me.bors.slack.share.entity.Workspace

@Service
class ConversationsService {
    private val slackClient = SlackConversationsClient()

    private val workspaceService: WorkspaceService = service()

    private val nameCache: Map<Workspace, Map<String, String>> =
        slackClient.getNameCache(workspaceService.getAvailableWorkspaces()) ?: emptyMap()

    private val cache: MutableMap<Workspace, Map<ConversationType, MutableMap<String, SlackConversation>>> =
        mutableMapOf()

    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()

    init {
        updateCache(true)
    }

    fun getConversations(workspace: Workspace): List<SlackConversation> {
        updateWorkspace(workspace)

        return cache.getValue(workspace)
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

        workspacesToDelete.forEach { cache.remove(it) }

        runBlocking {
            cache.keys.forEach { ws ->
                launch(dispatcher) {
                    updateWorkspace(ws)
                }
            }
        }
    }

    private fun updateWorkspace(workspace: Workspace) {
        runBlocking {
            cache
                .computeIfAbsent(workspace) { createEmptyTypeMap() }
                .forEach { (ct, ids) ->
                    launch(dispatcher) {
                        val token = workspace.state.get() ?: return@launch

                        val channels = getChannels(token, ct) ?: return@launch

                        val conversations = channels.associateBy { it.id }

                        val idsToCreate = conversations.keys - ids.keys

                        val idsToDelete = ids.keys - conversations.keys

                        idsToDelete.forEach { ids.remove(it) }

                        if (idsToCreate.isEmpty()) return@launch

                        ids.putAll(
                            parseChannels(
                                idsToCreate.mapNotNull { conversations[it] },
                                ct,
                                workspace
                            ).associateBy { it.id }
                        )
                    }
                }
        }
    }

    private fun createEmptyTypeMap(): Map<ConversationType, MutableMap<String, SlackConversation>> =
        listOf(PRIVATE_CHANNEL, PUBLIC_CHANNEL, MPIM, IM).associateWith { mutableMapOf() }

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
                getUserName(workspace, it.user)
                    ?.let { userName ->
                        SlackConversation(
                            it.id,
                            userName,
                            it.priority ?: 0.0
                        )
                    }
            }

            MPIM -> parseChannelsWithCoroutines(conversations) {
                getMultiUserGroupName(workspace, it.id)
                    ?.let { multiUserGroupName ->
                        SlackConversation(
                            it.id,
                            multiUserGroupName,
                            it.priority ?: 0.0
                        )
                    }
            }

            else -> emptyList()
        }
    }

    private fun parseChannelsWithCoroutines(
        conversations: List<Conversation>,
        parser: (Conversation) -> SlackConversation?
    ): List<SlackConversation> {
        val result = LinkedBlockingQueue<SlackConversation>()

        runBlocking {
            conversations.forEach {
                launch(dispatcher) {
                    parser.invoke(it)?.let { slackConversation ->
                        result.add(slackConversation)
                    }
                }
            }
        }

        return result.toList()
    }

    private fun getMultiUserGroupName(workspace: Workspace, id: String): String? {
        val members = slackClient.getMultiUserGroupMembers(workspace.state.get()!!, id)
            ?: emptyList()

        return members
            .mapNotNull { getUserName(workspace, it) }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(", ")
    }


    private fun getUserName(workspace: Workspace, userId: String): String? {
        return nameCache[workspace]?.get(userId) ?: slackClient.getUserName(workspace.state.get()!!, userId)
    }
}
