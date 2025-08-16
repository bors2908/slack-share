package me.bors.slack.share.client

import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.files.FilesUploadV2Request
import com.slack.api.model.Conversation
import com.slack.api.model.ConversationType
import java.nio.charset.Charset
import me.bors.slack.share.SlackShareTestBase

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

        assertTrue(
            conversationsClient.getUserName(token, channels.first { it.isIm && it.user != null }.user).isNotBlank()
        )
    }


    fun testMessageClient() {
        val workspace = workspaceService.getAvailableWorkspaces().first()

        val token = workspace.state.get() ?: throw AssertionError("Token is absent.")

        val channel = getChannels(token).first()

        val messageText = getRandomPayload()

        messageClient.sendMessage(
            token,
            ChatPostMessageRequest.builder()
                .channel(channel.id)
                .mrkdwn(false)
                .text(messageText)
        )

        val lastMessage = testClient.getLastMessages(workspace, channel.id).getLastMessage()

        assertTrue(lastMessage.text.contains(messageText))

        val fileName = "test.txt"
        val filePayload = getRandomPayload()

        messageClient.sendFile(
            token,
            FilesUploadV2Request.builder()
                .channels(listOf(channel.id))
                .fileData(
                    filePayload.toByteArray(Charset.defaultCharset())
                )
                .filename(fileName)
                .snippetType("auto")
        )

        val lastMessage2 = testClient.getLastMessages(workspace, channel.id).getLastMessage()

        val file = lastMessage2.files.first()

        assertEquals(fileName, file.name)
        assertEquals(filePayload, file.preview)
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
            ConversationType.entries
        )
    }
}
