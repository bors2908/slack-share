package me.bors.slack.share.client

import com.slack.api.Slack
import com.slack.api.methods.request.auth.AuthTestRequest
import me.bors.slack.share.entity.ValidationResult
import me.bors.slack.share.error.SlackClientException

open class SlackWorkspaceClient : SlackClientBase() {
    private val slack = Slack.getInstance()

    fun validate(token: String): ValidationResult {
        val response = slack.methods(token)
            .authTest(AuthTestRequest.builder().token(token).build())
            ?: throw SlackClientException("Null response.")

        return if (response.isOk) {
            ValidationResult(name = response.team ?: response.url, slackId = response.teamId)
        } else {
            ValidationResult(error = response.error)
        }
    }
}
