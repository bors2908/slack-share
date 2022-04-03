package me.bors.slack.share

import com.slack.api.model.ConversationType
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

open class SlackConversationsProcessor(protected val slackClient: SlackClient) {
    fun getConversations(): List<SlackConversation> {
        /* Multi-User conversations require a lot of requests to receive members and form a readable conversation name
           Concurrent execution helps to reduce execution time up to 4x
         */
        val pool = Executors.newFixedThreadPool(
            (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(2)
        )

        val result = LinkedBlockingQueue<SlackConversation>()

        val multiChannels = slackClient.getChannels(listOf(ConversationType.MPIM))

        val latch = CountDownLatch(multiChannels.size)

        multiChannels.forEach {
            pool.submit {
                result.add(SlackConversation(it.id,
                    slackClient.getMultiUserGroupName(it.id),
                    it.priority ?: 0.0))

                latch.countDown()
            }
        }

        result.addAll(
            slackClient.getChannels(listOf(ConversationType.IM))
                .map { SlackConversation(it.id, slackClient.getUserName(it.user), it.priority ?: 0.0) }
        )

        result.addAll(
            slackClient.getChannels(listOf(ConversationType.PRIVATE_CHANNEL, ConversationType.PUBLIC_CHANNEL))
                .map { SlackConversation(it.id, it.nameNormalized, it.priority ?: 0.0) }
        )

        // Await moved lower to allow main thread to process more streamlined IMs and channels while MLIMs are
        // processed in the pool.
        latch.await()

        pool.shutdown()

        return result.sortedByDescending { it.priority }
    }
}
