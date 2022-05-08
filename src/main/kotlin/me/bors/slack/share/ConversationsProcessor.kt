package me.bors.slack.share

import com.slack.api.model.ConversationType
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.bors.slack.share.entity.Conversation

open class ConversationsProcessor(protected val slackClient: SlackClient) {
    fun getConversations(): List<Conversation> {
        /* Multi-User conversations require a lot of requests to receive members and form a readable conversation name
           Concurrent execution helps to reduce execution time up to 4x
         */
        val result = LinkedBlockingQueue<Conversation>()

        val multiChannels = slackClient.getChannels(listOf(ConversationType.MPIM))

        val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()

        runBlocking {
            multiChannels.forEach {
                launch(dispatcher) {
                    result.add(
                        Conversation(
                            it.id,
                            slackClient.getMultiUserGroupName(it.id),
                            it.priority ?: 0.0
                        )
                    )
                }
            }

            launch(dispatcher) {
                result.addAll(
                    slackClient.getChannels(listOf(ConversationType.IM))
                        .map { Conversation(it.id, slackClient.getUserName(it.user), it.priority ?: 0.0) }
                )
            }

            launch(dispatcher) {
                result.addAll(
                    slackClient.getChannels(listOf(ConversationType.PRIVATE_CHANNEL, ConversationType.PUBLIC_CHANNEL))
                        .map { Conversation(it.id, it.nameNormalized, it.priority ?: 0.0) }
                )
            }
        }

        return result.sortedByDescending { it.priority }
    }
}
