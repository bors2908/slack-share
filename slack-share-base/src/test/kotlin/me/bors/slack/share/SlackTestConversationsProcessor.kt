package me.bors.slack.share

import com.slack.api.model.ConversationType
import me.bors.slack.share.entity.Conversation
import me.bors.slack.share.entity.Workspace
import me.bors.slack.share.processor.ConversationsProcessor

class SlackTestConversationsProcessor : ConversationsProcessor() {
    fun getConversationsSingleThreaded(): List<Conversation> {
        val workspace = Workspace(0, "", "")

        val token = workspace.state.get() ?: TODO()

        val result = mutableListOf<Conversation>()

        result.addAll(
            slackClient.getChannels(token, listOf(ConversationType.IM))
                .map { Conversation(it.id, slackClient.getUserName(token, it.user), it.priority ?: 0.0) }
        )
        result.addAll(
            slackClient.getChannels(token, listOf(ConversationType.MPIM))
                .map { Conversation(it.id, getMultiUserGroupName(token, workspace.id, it.id), it.priority ?: 0.0) }
        )
        result.addAll(
            slackClient.getChannels(token, listOf(ConversationType.PRIVATE_CHANNEL, ConversationType.PUBLIC_CHANNEL))
                .map { Conversation(it.id, it.nameNormalized, it.priority ?: 0.0) }
        )

        return result.sortedByDescending { it.priority }
    }
}
