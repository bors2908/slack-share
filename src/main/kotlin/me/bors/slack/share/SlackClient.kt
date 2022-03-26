package me.bors.slack.share

import com.slack.api.Slack
import com.slack.api.methods.request.conversations.ConversationsListRequest
import com.slack.api.methods.request.conversations.ConversationsMembersRequest
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.methods.request.users.UsersListRequest
import com.slack.api.methods.response.conversations.ConversationsListResponse
import com.slack.api.model.Conversation
import com.slack.api.model.ConversationType
import me.bors.slack.share.Utils.getProperties

class SlackClient {
    private val slack = Slack.getInstance()
    private val props = getProperties("application.properties")
    private val token = props.getProperty("token")

    private val nameCache = getNameCache()

    private fun getNameCache(): Map<String, String> {
        // TODO Add pagination support
        return slack.methods(token).usersList(
            UsersListRequest.builder()
                .token(token)
                .build()
        ).members
            .associate { it.id to (it.realName ?: it.name ?: it.id) }
    }

    fun receiveChannels(): MutableList<Pair<String, String>> {
        val ims = getChannels(listOf(ConversationType.IM))
        val mpims = getChannels(listOf(ConversationType.MPIM))
        val channels = getChannels(listOf(ConversationType.PRIVATE_CHANNEL, ConversationType.PUBLIC_CHANNEL))

        val imMap = ims.map { it.id to getUserName(it.user) }

        val mpimMap = mpims.map { it.id to getMultiUserGroupName(it.id) }
        val channelMap = channels.map { it.id to it.nameNormalized }

        val result = mutableListOf<Pair<String,String>>()
        result.addAll(imMap)
        result.addAll(mpimMap)
        result.addAll(channelMap)

        return result
    }

    private fun getUserName(user: String): String {
        val name = nameCache[user]

        return if (name != null) {
            name
        } else {
            val usersInfoRequest = UsersInfoRequest.builder()
                .token(token)
                .user(user)
                .build()

            val usersInfo = slack.methods(token).usersInfo(usersInfoRequest).user

            usersInfo.realName ?: usersInfo.name ?: usersInfo.id
        }
    }

    private fun getMultiUserGroupName(id: String): String {
        val request = ConversationsMembersRequest.builder()
            .token(token)
            .channel(id)
            .build()

        //TODO Add pagination support
        val members = slack.methods(token).conversationsMembers(request).members

        members.removeAt(members.size - 1)

        return members
            .joinToString(", ") { getUserName(it) }
    }

    private fun getChannels(
        requestTypes: List<ConversationType>
    ): List<Conversation> {
        val request = ConversationsListRequest.builder()
            .token(token)
            .excludeArchived(true)
            .types(requestTypes)
            .build()

        //TODO Add pagination support
        val conversationsList: ConversationsListResponse = slack.methods(token).conversationsList(request)

        return conversationsList.channels
    }
}

fun main() {
    SlackClient()
}