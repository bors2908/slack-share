package me.bors.slack.share

import com.slack.api.model.ConversationType
import me.bors.slack.share.client.SlackClient
import me.bors.slack.share.entity.Conversation
import me.bors.slack.share.processor.ConversationsProcessor

class SlackTestConversationsProcessor(slackClient: SlackClient) : ConversationsProcessor(slackClient) {
    fun getConversationsSingleThreaded(): List<Conversation> {
        val result = mutableListOf<Conversation>()

        result.addAll(
            slackClient.getChannels(listOf(ConversationType.IM))
                .map { Conversation(it.id, slackClient.getUserName(it.user), it.priority ?: 0.0) }
        )
        result.addAll(
            slackClient.getChannels(listOf(ConversationType.MPIM))
                .map { Conversation(it.id, slackClient.getMultiUserGroupName(it.id), it.priority ?: 0.0) }
        )
        result.addAll(
            slackClient.getChannels(listOf(ConversationType.PRIVATE_CHANNEL, ConversationType.PUBLIC_CHANNEL))
                .map { Conversation(it.id, it.nameNormalized, it.priority ?: 0.0) }
        )

        return result.sortedByDescending { it.priority }
    }
}
