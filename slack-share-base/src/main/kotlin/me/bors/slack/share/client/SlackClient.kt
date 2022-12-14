package me.bors.slack.share.client

import com.intellij.openapi.diagnostic.Logger
import com.slack.api.Slack
import com.slack.api.methods.SlackApiTextResponse
import com.slack.api.methods.request.PaginatedRequest
import com.slack.api.methods.request.auth.AuthTestRequest
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.conversations.ConversationsListRequestPaginated
import com.slack.api.methods.request.conversations.ConversationsMembersRequestPaginated
import com.slack.api.methods.request.files.FilesUploadRequest
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.methods.request.users.UsersListRequestPaginated
import com.slack.api.methods.response.PaginatedExtractor
import com.slack.api.methods.response.users.UsersListExtractor
import com.slack.api.model.Conversation
import com.slack.api.model.ConversationType
import com.slack.api.model.User
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
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

    fun sendMessage(id: String, text: String, quoteCode: Boolean = false) {
        val builder = ChatPostMessageRequest.builder()
            .token(token)
            .channel(id)

        if (quoteCode) {
            builder
                .mrkdwn(true)
                .blocks(
                    listOf(
                        SectionBlock.builder()
                            .text(
                                MarkdownTextObject.builder()
                                    .text("```$text```")
                                    .build()
                            )
                            .build()
                    )
                )
        } else {
            builder
                .mrkdwn(false)
                .text(text)
        }

        val request = builder.build()

        slack.methods(token).chatPostMessage(request).processErrors()
    }

    fun sendFile(id: String, files: List<File>, text: String) {
        var tagged = false

        for (file: File in files) {
            val builder = FilesUploadRequest.builder()
                .token(token)
                .channels(listOf(id))
                .fileData(file.readBytes())
                .filename(file.name)

            if (text.isNotEmpty() && !tagged) {
                builder.initialComment(text)

                tagged = true
            }

            val request = builder.build()

            slack.methods(token).filesUpload(request).processErrors()
        }
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
        val request = ConversationsMembersRequestPaginated.builder()
            .token(token)
            .channel(id)
            .build()

        val members = processPaginatedRequest<String>(request) { slack.methods(token).conversationsMembers(it) }

        // Removing user's name
        members.removeAt(members.size - 1)

        return members
            .joinToString(", ") { getUserName(it) }
    }

    fun getChannels(
        requestTypes: List<ConversationType>,
    ): List<Conversation> {
        val request = ConversationsListRequestPaginated.builder()
            .token(token)
            .excludeArchived(true)
            .types(requestTypes)
            .build()

        return processPaginatedRequest<Conversation>(request) { slack.methods(token).conversationsList(it) }
    }

    private fun validateToken() {
        val tokenStatus = slack.methods().authTest(AuthTestRequest.builder().token(token).build())

        if (!tokenStatus.isOk) throw SlackTokenValidationException("Error: ${tokenStatus.error}")
    }

    private fun getNameCache(): Map<String, String> {
        val request = UsersListRequestPaginated.builder()
            .token(token)
            .build()

        val members =
            processPaginatedRequest<User>(request) { UsersListExtractor(slack.methods(token).usersList(it as UsersListRequestPaginated)) }

        return members.associate { it.id to (it.realName ?: it.name ?: it.id) }
    }

    // Unfortunately Slack Java API paginated request has no extracted interface with cursor and limit fields.
    private inline fun <reified T> processPaginatedRequest(
        request: PaginatedRequest,
        process: (PaginatedRequest) -> PaginatedExtractor<*, T>,
    ): MutableList<T> {
        val limit = PAGE_SIZE

        val accumulator = mutableListOf<T>()

        var cursor = ""

        do {
            request.setCursor(cursor)
            request.setLimit(limit)

            val response = process.invoke(request)

            cursor = response.nextCursor

            accumulator.addAll(response.collection)
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

open class SlackClientException(message: String) : RuntimeException(message)

class SlackTokenValidationException(message: String) : SlackClientException(message)
