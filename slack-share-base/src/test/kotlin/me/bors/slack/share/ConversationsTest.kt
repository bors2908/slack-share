package me.bors.slack.share

import me.bors.slack.share.entity.MessageStyle
import me.bors.slack.share.processor.MessageProcessor
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.nio.file.Path

@Disabled
class ConversationsTest {
    private val token = ""

    private val slackClient = SlackTestClient(token)

    private val messageProcessor = MessageProcessor(slackClient)

    private val conversationsProcessor = SlackTestConversationsProcessor(slackClient)

    @Disabled
    @Test
    fun sendMessageTest() {
        val receiveChannels = conversationsProcessor.getConversations()

        // TODO Replace with user info request
        val id = receiveChannels.first { it.name == "bors2908" }.id

        messageProcessor.sendMessage(id, "whatever", MessageStyle.NONE)
    }

    @Disabled
    @Test
    fun sendFileTest() {
        val receiveChannels = conversationsProcessor.getConversations()

        // TODO Replace with user info request
        val id = receiveChannels.first { it.name == "bors2908" }.id

        // Todo add file creation
        val path = Path.of("C:\\Temp\\useless.txt").toFile()

        messageProcessor.sendFile(id, listOf(path, path), "No way")
    }

    @Disabled
    @Test
    fun testCompareMultiThreaded() {
        val times: MutableList<Long> = ArrayList(3)

        times.add(System.currentTimeMillis())

        val conversations = conversationsProcessor.getConversationsSingleThreaded()

        times.add(System.currentTimeMillis())

        val conversationsConcurrent = conversationsProcessor.getConversations()

        times.add(System.currentTimeMillis())

        Assertions.assertTrue(conversationsConcurrent.containsAll(conversations))

        val single = times[1] - times[0]
        val concurrent = times[2] - times[1]

        Assertions.assertTrue(concurrent < single)
    }
}
