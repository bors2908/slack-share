package me.bors.slack.share

import com.slack.api.model.ConversationType

class SlackTestConversationsProcessor(slackClient: SlackClient) : SlackConversationsProcessor(slackClient) {
    fun getConversationsSingleThreaded(): List<SlackConversation> {
        val result = mutableListOf<SlackConversation>()

        result.addAll(
            slackClient.getChannels(listOf(ConversationType.IM))
                .map { SlackConversation(it.id, slackClient.getUserName(it.user), it.priority ?: 0.0) }
        )
        result.addAll(
            slackClient.getChannels(listOf(ConversationType.MPIM))
                .map { SlackConversation(it.id, slackClient.getMultiUserGroupName(it.id), it.priority ?: 0.0) }
        )
        result.addAll(
            slackClient.getChannels(listOf(ConversationType.PRIVATE_CHANNEL, ConversationType.PUBLIC_CHANNEL))
                .map { SlackConversation(it.id, it.nameNormalized, it.priority ?: 0.0) }
        )

        return result.sortedByDescending { it.priority }
    }
}