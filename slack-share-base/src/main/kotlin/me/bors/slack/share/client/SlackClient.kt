package me.bors.slack.share.client

import com.intellij.openapi.diagnostic.Logger
import com.slack.api.Slack
import com.slack.api.methods.SlackApiTextResponse
import com.slack.api.methods.request.auth.AuthTestRequest
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.chat.ChatPostMessageRequest.ChatPostMessageRequestBuilder
import com.slack.api.methods.request.conversations.ConversationsListRequest
import com.slack.api.methods.request.conversations.ConversationsMembersRequest
import com.slack.api.methods.request.files.FilesUploadRequest
import com.slack.api.methods.request.files.FilesUploadRequest.FilesUploadRequestBuilder
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.methods.request.users.UsersListRequest
import com.slack.api.model.Conversation
import com.slack.api.model.ConversationType
import com.slack.api.model.User
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import me.bors.slack.share.error.SlackClientException
import me.bors.slack.share.error.SlackTokenValidationException
import java.io.File

private const val PAGE_SIZE = 200

private val logger: Logger = Logger.getInstance(SlackClient::class.java)

open class SlackClient(private val token: String) {
    private val slack = Slack.getInstance()

    private val nameCache: Map<String, String>

    init {
        validateToken()

        nameCache = getNameCache()
    }

    fun sendMessage(requestBuilder: ChatPostMessageRequestBuilder) {
        val request = requestBuilder.token(token).build()

        slack.methods(token).chatPostMessage(request).processErrors()
    }

    fun sendFile(requestBuilder: FilesUploadRequestBuilder) {
        val request = requestBuilder.token(token).build()

        slack.methods(token).filesUpload(request).processErrors()
    }

    fun getUserName(user: String): String {
        val name = nameCache[user]

        return if (name != null) {
            name
        } else {
            val usersInfoRequest = UsersInfoRequest.builder()
                .token(token)
                .user(user)
                .build()

            val usersInfo = slack.methods(token).usersInfo(usersInfoRequest)
                .processErrors()
                .user

            usersInfo.realName ?: usersInfo.name ?: usersInfo.id
        }
    }

    fun getMultiUserGroupName(id: String): String {
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
            .joinToString(", ") { getUserName(it) }
    }

    fun getChannels(
        requestTypes: List<ConversationType>,
    ): List<Conversation> {
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

    private fun validateToken() {
        val tokenStatus = slack.methods().authTest(AuthTestRequest.builder().token(token).build())

        if (!tokenStatus.isOk) throw SlackTokenValidationException("Error: ${tokenStatus.error}")
    }

    private fun getNameCache(): Map<String, String> {
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

        return members.associate { it.id to (it.realName ?: it.name ?: it.id) }
    }

    // Unfortunately Slack Java API paginated request has no extracted interface with cursor and limit fields.
    private inline fun <reified T> processPaginatedRequest(
        processRequest: (String, Int) -> Pair<String, List<T>>,
    ): MutableList<T> {
        val limit = PAGE_SIZE

        val accumulator = mutableListOf<T>()

        var cursor = ""

        do {
            val pair = processRequest.invoke(cursor, limit)

            cursor = pair.first

            accumulator.addAll(pair.second)
        } while (cursor != "")

        return accumulator
    }

    private inline fun <reified T : SlackApiTextResponse> T.processErrors(): T {
        if (this.warning != null) {
            logger.warn("Warining received from Slack: ${this.warning}")
        }

        if (this.error != null) {
            val needed = if (this.needed != null) "Needed: ${this.needed}" else ""
            val provided = if (this.provided != null) "Provided: ${this.provided}" else ""


            throw SlackClientException(
                "Error occurred, during Slack request execution: " +
                    "${this.error} ${System.lineSeparator()} $needed ${System.lineSeparator()} $provided"
            )
        }

        return this
    }
}
