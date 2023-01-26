package me.bors.slack.share.processor

import com.intellij.openapi.components.service
import com.slack.api.model.ConversationType
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.bors.slack.share.client.SlackConversationsClient
import me.bors.slack.share.entity.Conversation
import me.bors.slack.share.entity.Workspace
import me.bors.slack.share.service.WorkspaceService
import me.bors.slack.share.ui.share.dialog.ErrorDialogWrapper
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

open class ConversationsProcessor {
    protected val slackClient = SlackConversationsClient()

    protected val workspaceService: WorkspaceService = service()

    protected val nameCache: Map<Int, Map<String, String>> = slackClient.getNameCache(workspaceService.getTokenMap())

    fun getConversations(workspace: Workspace): List<Conversation> {
        /* Multi-User conversations require a lot of requests to receive members and form a readable conversation name
           Concurrent execution helps to reduce execution time up to 6x
         */
        val result = LinkedBlockingQueue<Conversation>()

        val token = workspace.state.get()

        if (token == null) {
            ErrorDialogWrapper("Token is missing.").showAndGet()

            return emptyList()
        }

        val multiChannels = slackClient.getChannels(token, listOf(ConversationType.MPIM))

        val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()

        runBlocking {
            multiChannels.forEach {
                launch(dispatcher) {
                    result.add(
                        Conversation(
                            it.id,
                            getMultiUserGroupName(token, workspace.id, it.id),
                            it.priority ?: 0.0
                        )
                    )
                }
            }

            launch(dispatcher) {
                result.addAll(
                    slackClient.getChannels(token, listOf(ConversationType.IM))
                        .map { Conversation(it.id, getUserName(token, workspace.id, it.user), it.priority ?: 0.0) }
                )
            }

            launch(dispatcher) {
                result.addAll(
                    slackClient.getChannels(token, listOf(ConversationType.PRIVATE_CHANNEL, ConversationType.PUBLIC_CHANNEL))
                        .map { Conversation(it.id, it.nameNormalized, it.priority ?: 0.0) }
                )
            }
        }

        return result.sortedByDescending { it.priority }
    }

    fun getMultiUserGroupName(token: String, workspaceId: Int, id: String) =
        slackClient.getMultiUserGroupMembers(token, id).joinToString(", ") { getUserName(token, workspaceId, it) }

    fun getUserName(token: String, workspaceId: Int, userId: String): String {
        return nameCache[workspaceId]?.get(userId) ?: slackClient.getUserName(token, userId)
    }
}
