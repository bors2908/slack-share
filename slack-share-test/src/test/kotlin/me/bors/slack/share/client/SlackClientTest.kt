package me.bors.slack.share.client

import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.files.FilesUploadRequest
import com.slack.api.model.Conversation
import com.slack.api.model.ConversationType
import me.bors.slack.share.SlackShareTestBase
import java.nio.charset.Charset

open class SlackClientTest : SlackShareTestBase() {
    private var conversationsClient = SlackConversationsClient()

    private var messageClient = SlackMessageClient()

    private var workspaceClient = SlackWorkspaceClient()

    fun testConversationClient() {
        conversationsClient.getNameCache(workspaceService.getAvailableWorkspaces())

        val token = workspaceService.getAvailableWorkspaces().first().state.get()!!

        val channels = getChannels(token)

        assertNotEmpty(channels)

        assertNotEmpty(conversationsClient.getMultiUserGroupMembers(token, channels.first { it.isMpim }.id))

        assertTrue(conversationsClient.getUserName(token, channels.first { it.isIm }.id).isNotBlank())
    }


    fun testMessageClient() {
        val token = workspaceService.getAvailableWorkspaces().first().state.get()!!

        val channel = getChannels(token).first()

        messageClient.sendMessage(
            token,
            ChatPostMessageRequest.builder()
                .channel(channel.id)
                .mrkdwn(false)
                .text("Sample text")
        )

        messageClient.sendFile(
            token,
            FilesUploadRequest.builder()
                .channels(listOf(channel.id))
                .fileData("Sample text".toByteArray(Charset.defaultCharset()))
                .filename("test.file")
                .filetype("auto")
        )

        //TODO Check message received, will require additional rights on Slack side.
    }

    fun testWorkspaceClient() {
        val token = workspaceService.getAvailableWorkspaces().first().state.get()!!

        val validation = workspaceClient.validate(token)

        assertNull(validation.error)
        assertTrue(validation.slackId.isNotBlank())
        assertTrue(validation.name.isNotBlank())
    }

    private fun getChannels(token: String): List<Conversation> {
        return conversationsClient.getChannels(
            token,
            ConversationType.values().toList()
        )
    }
}
