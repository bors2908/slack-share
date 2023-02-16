package me.bors.slack.share.processor

import com.slack.api.model.ConversationType
import me.bors.slack.share.SlackShareTestBase
import me.bors.slack.share.client.SlackConversationsClient
import me.bors.slack.share.entity.MessageStyle
import java.io.File
import java.net.URL
import java.util.*

class MessageProcessorTest : SlackShareTestBase() {
    private val processor = MessageProcessor()

    private var conversationsClient = SlackConversationsClient()

    fun test() {
        val workspace = workspaceService.getAvailableWorkspaces().first()

        val conversation = conversationsClient.getChannels(workspace.state.get()!!, ConversationType.values().toList()).first()

        val classResource: URL = MessageProcessorTest::class.java.classLoader.getResource(".")
            ?: throw AssertionError("Null class resource.")

        val fileName = "temp.txt"

        val content = getRandomPayload()

        createTestFileAndCheck(File("${classResource.path}/$fileName"), content) {
            processor.sendFile(
                workspace,
                conversation.id,
                listOf(it),
                content
            )
        }

        val lastMessage = testClient.getLastMessages(workspace, conversation.id).getLastMessage()

        val file = lastMessage.files.first()

        assertEquals(fileName, file.name)
        assertEquals(content, file.preview)

        val messageText = getRandomPayload()

        MessageStyle.values().forEach { style ->
            processor.sendMessage(workspace, conversation.id, messageText, style, "")

            val lastMessage2 = testClient.getLastMessages(workspace, conversation.id).getLastMessage()

            if (style == MessageStyle.CODE_SNIPPET) {
                val file2 = lastMessage2.files.first()

                assertEquals("snippet.txt", file2.name)
                assertEquals(messageText, file2.preview)
            } else {
                assertTrue(lastMessage2.text.contains(messageText))
            }
        }
    }

    private fun getRandomPayload() = "Sample Text " + UUID.randomUUID()
}
