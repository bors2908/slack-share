package me.bors.slack.share

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path

class ConversationsTest {
    val token = properties.getProperty("token")

    @Test
    fun sendMessageTest() {
        val slackClient = SlackTestClient(token)

        val conversationsProcessor = SlackConversationsProcessor(slackClient)

        val receiveChannels = conversationsProcessor.getConversations()

        // TODO Replace with user info request
        val id = receiveChannels.first { it.name == "Boris Ivanov" }.id

        slackClient.sendMessage(id, "whatever")
    }

    @Test
    fun sendFileTest() {
        val slackClient = SlackTestClient(token)

        val conversationsProcessor = SlackConversationsProcessor(slackClient)

        val receiveChannels = conversationsProcessor.getConversations()

        // TODO Replace with user info request
        val id = receiveChannels.first { it.name == "Boris Ivanov" }.id

        // Todo add file creation
        val path = Path.of("C:\\Temp\\useless.txt").toFile()

        slackClient.sendFile(id, listOf(path, path), "No way")
    }

    @Test
    fun testCompareMultiThreaded() {
        val slackClient = SlackTestClient(token)

        val conversationsProcessor = SlackTestConversationsProcessor(slackClient)

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
