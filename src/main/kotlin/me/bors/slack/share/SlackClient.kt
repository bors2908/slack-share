package me.bors.slack.share

import com.slack.api.Slack
import com.slack.api.methods.SlackApiTextResponse
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.conversations.ConversationsListRequest
import com.slack.api.methods.request.conversations.ConversationsMembersRequest
import com.slack.api.methods.request.files.FilesUploadRequest
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.methods.request.users.UsersListRequest
import com.slack.api.model.Conversation
import com.slack.api.model.ConversationType
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import java.io.File
import java.io.FileNotFoundException
import me.bors.slack.share.Utils.getProperties

//TODO Add multithreading to reduce loading time.
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
        )
            .processErrors()
            .members
            .associate { it.id to (it.realName ?: it.name ?: it.id) }
    }

    // TODO Sort by relevance
    fun receiveChannels(): MutableList<Pair<String, String>> {
        val ims = getChannels(listOf(ConversationType.IM))
        val mpims = getChannels(listOf(ConversationType.MPIM))
        val channels = getChannels(listOf(ConversationType.PRIVATE_CHANNEL, ConversationType.PUBLIC_CHANNEL))

        val imMap = ims.map { it.id to getUserName(it.user) }

        val mpimMap = mpims.map { it.id to getMultiUserGroupName(it.id) }
        val channelMap = channels.map { it.id to it.nameNormalized }

        val result = mutableListOf<Pair<String, String>>()
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

            val usersInfo = slack.methods(token).usersInfo(usersInfoRequest)
                .processErrors()
                .user

            usersInfo.realName ?: usersInfo.name ?: usersInfo.id
        }
    }

    private fun getMultiUserGroupName(id: String): String {
        val request = ConversationsMembersRequest.builder()
            .token(token)
            .channel(id)
            .build()

        //TODO Add pagination support
        val members = slack.methods(token).conversationsMembers(request)
            .processErrors()
            .members

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
        return slack.methods(token).conversationsList(request)
            .processErrors()
            .channels
    }

    fun sendMessage(id: String, text: String, quoteCode: Boolean = false) {
        // TODO make it quote
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
                                    .text(processMarkdownAndQuote(text))
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

        //TODO Remove
        println("Sending message to channel $id")

        val response = slack.methods(token).chatPostMessage(request).processErrors()
    }

    private fun processMarkdownAndQuote(text: String): String {
        val result = text
            .replace("<", "&lt;")
            .replace(">", "&rt;")
            .replace("&", "&amp;")

        return "```$result```"
    }

    fun sendFile(id: String, files: List<File>, text: String) {
        var tagged = false

        // TODO Switch to File blocks message markdown
        for (file: File in files) {
            if (!file.exists()) {
                throw FileNotFoundException("File not found [$file].")
            }

            //TODO make it use path instead of loading bytes
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

            val response = slack.methods(token).filesUpload(request).processErrors()
        }
    }
}

fun <T : SlackApiTextResponse> T.processErrors(): T {
    if (this.warning != null) {
        //TODO Uncomment after logging stuff is resolved
        //log.warn { "Warining received from Slack: ${this.warning}" }
    }

    if (this.error != null) {
        val needed = if (this.needed != null) "Needed: ${this.needed}" else ""
        val provided = if (this.provided != null) "Provided: ${this.provided}" else ""


        throw RuntimeException(
            "Error occurred, during Slack request execution: " +
                    "${this.error} ${System.lineSeparator()} $needed ${System.lineSeparator()} $provided"
        )
    }

    return this
}