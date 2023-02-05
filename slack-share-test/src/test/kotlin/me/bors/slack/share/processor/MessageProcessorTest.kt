package me.bors.slack.share.processor

import com.slack.api.model.ConversationType
import me.bors.slack.share.SlackShareTestBase
import me.bors.slack.share.client.SlackConversationsClient
import me.bors.slack.share.entity.MessageStyle
import java.io.File

class MessageProcessorTest : SlackShareTestBase() {
    private val processor = MessageProcessor()

    private var conversationsClient = SlackConversationsClient()

    fun test() {
        val workspace = workspaceService.getAvailableWorkspaces().first()

        val conversation = conversationsClient.getChannels(workspace.state.get()!!, ConversationType.values().toList()).first()

        createTestFileAndCheck(File("/temp.txt")) {
            processor.sendFile(
                workspace,
                conversation.id,
                listOf(it),
                "Sample Text"
            )

            //TODO Assert received.
        }

        MessageStyle.values().forEach { style ->
            processor.sendMessage(workspace, conversation.id, "Sample Text", style, "")

            //TODO Assert received.
        }
    }
}