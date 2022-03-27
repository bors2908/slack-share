package me.bors.slack.share

import java.nio.file.Path
import org.junit.Test

class SlackClientTest {

    //TODO investigate tests
    @Test
    fun sendMessageTest() {
        val slackClient = SlackClient()

        val receiveChannels = slackClient.receiveChannels()

        val id = receiveChannels.first { it.second == "Boris Ivanov" }.first

        slackClient.sendMessage(id, "whatever")
    }

    @Test
    fun sendFileTest() {
        val slackClient = SlackClient()

        val receiveChannels = slackClient.receiveChannels()

        val id = receiveChannels.first { it.second == "Boris Ivanov" }.first

        val path = Path.of("C:\\Temp\\useless.txt").toFile()
        
        slackClient.sendFile(id, listOf(path, path), "No way")
    }
}