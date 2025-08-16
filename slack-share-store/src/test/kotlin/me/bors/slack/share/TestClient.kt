package me.bors.slack.share

import com.slack.api.methods.request.conversations.ConversationsHistoryRequest
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import me.bors.slack.share.client.SlackClientBase
import me.bors.slack.share.entity.Workspace

class TestClient : SlackClientBase() {
    fun getLastMessages(
        workspace: Workspace,
        channel: String,
        after: Instant = Instant.now().minus(1, ChronoUnit.MINUTES),
        wait: Duration = ChronoUnit.SECONDS.duration,
        limit: Int = 10
    ): ConversationsHistoryResponse? {
        val token = workspace.state.get() ?: throw AssertionError("Token is absent.")

        val conversationsHistoryRequest = ConversationsHistoryRequest.builder()
            .token(token)
            .channel(channel)
            .oldest(after.epochSecond.toString())
            .limit(limit)
            .build()

        Thread.sleep(wait.toMillis())

        return slack.methods(token).conversationsHistory(conversationsHistoryRequest)
            .processErrors()
    }
}
