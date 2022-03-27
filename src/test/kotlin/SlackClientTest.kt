package me.bors.slack.share

import java.nio.file.Path
import org.junit.Test

class SlackClientTest {
    //TODO investigate tests
    @Test
    fun sendMessageTest() {
        val slackClient = SlackClient()

        val receiveChannels = slackClient.getConversations()

        val id = receiveChannels.first { it.name == "Boris Ivanov" }.id

        slackClient.sendMessage(id, "whatever")
    }

    @Test
    fun sendFileTest() {
        val slackClient = SlackClient()

        val receiveChannels = slackClient.getConversations()

        val id = receiveChannels.first { it.name == "Boris Ivanov" }.id

        val path = Path.of("C:\\Temp\\useless.txt").toFile()

        slackClient.sendFile(id, listOf(path, path), "No way")
    }
}