package me.bors.slack.share

import com.slack.api.model.ConversationType
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

open class SlackConversationsProcessor(protected val slackClient: SlackClient) {
    fun getConversations(): List<SlackConversation> {
        /* Multi-User conversations require a lot of requests to receive members and form a readable conversation name
           Concurrent execution helps to reduce execution time up to 4x
         */
        val result = LinkedBlockingQueue<SlackConversation>()

        val multiChannels = slackClient.getChannels(listOf(ConversationType.MPIM))

        val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()

        runBlocking {
            multiChannels.forEach {
                launch(dispatcher) {
                    result.add(
                        SlackConversation(
                            it.id,
                            slackClient.getMultiUserGroupName(it.id),
                            it.priority ?: 0.0
                        )
                    )
                }
            }

            launch {
                result.addAll(
                    slackClient.getChannels(listOf(ConversationType.IM))
                        .map { SlackConversation(it.id, slackClient.getUserName(it.user), it.priority ?: 0.0) }
                )
            }

            launch {
                result.addAll(
                    slackClient.getChannels(listOf(ConversationType.PRIVATE_CHANNEL, ConversationType.PUBLIC_CHANNEL))
                        .map { SlackConversation(it.id, it.nameNormalized, it.priority ?: 0.0) }
                )
            }
        }

        return result.sortedByDescending { it.priority }
    }
}
