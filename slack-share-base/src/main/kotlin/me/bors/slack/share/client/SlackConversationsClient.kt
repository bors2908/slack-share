package me.bors.slack.share.client

import com.intellij.openapi.diagnostic.Logger
import com.slack.api.Slack
import com.slack.api.methods.request.conversations.ConversationsListRequest
import com.slack.api.methods.request.conversations.ConversationsMembersRequest
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.methods.request.users.UsersListRequest
import com.slack.api.model.Conversation
import com.slack.api.model.ConversationType
import com.slack.api.model.User

open class SlackConversationsClient : SlackClientBase() {
    private val slack = Slack.getInstance()

    fun getUserName(token: String, user: String): String {
        val usersInfoRequest = UsersInfoRequest.builder()
            .token(token)
            .user(user)
            .build()

        val usersInfo = slack.methods(token).usersInfo(usersInfoRequest)
            .processErrors()
            .user

        return usersInfo.realName ?: usersInfo.name ?: usersInfo.id

    }

    fun getMultiUserGroupMembers(token: String, id: String): MutableList<String> {
        val request = ConversationsMembersRequest.builder()
            .token(token)
            .channel(id)
            .build()

        val members = processPaginatedRequest<String> { cursor, limit ->
            request.cursor = cursor
            request.limit = limit

            val response = slack.methods(token).conversationsMembers(request)
                .processErrors()

            response.responseMetadata.nextCursor to response.members
        }

        // Removing user's name
        members.removeAt(members.size - 1)

        return members
    }

    fun getChannels(token: String, requestTypes: List<ConversationType>): List<Conversation> {
        val request = ConversationsListRequest.builder()
            .token(token)
            .excludeArchived(true)
            .types(requestTypes)
            .build()

        return processPaginatedRequest<Conversation> { cursor, limit ->
            request.cursor = cursor
            request.limit = limit

            val response = slack.methods(token).conversationsList(request)
                .processErrors()

            response.responseMetadata.nextCursor to response.channels
        }
    }

    fun getNameCache(tokens: Map<Int, String>): Map<Int, Map<String, String>> {
        return tokens.map { (key, token) ->
            val request = UsersListRequest.builder()
                .token(token)
                .build()

            val members = processPaginatedRequest<User> { cursor, limit ->
                request.cursor = cursor
                request.limit = limit

                val response = slack.methods(token).usersList(request)
                    .processErrors()

                response.responseMetadata.nextCursor to response.members
            }

            key to (members.associate { it.id to (it.realName ?: it.name ?: it.id) })
        }.associate { it.first to it.second }
    }
}
