package me.bors.slack.share.client

import com.intellij.openapi.diagnostic.Logger
import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest.ChatPostMessageRequestBuilder
import com.slack.api.methods.request.files.FilesUploadRequest.FilesUploadRequestBuilder

open class SlackMessageClient : SlackClientBase() {
    private val slack = Slack.getInstance()

    fun sendMessage(token: String, requestBuilder: ChatPostMessageRequestBuilder) {
        val request = requestBuilder.token(token).build()

        slack.methods(token).chatPostMessage(request).processErrors()
    }

    fun sendFile(token: String, requestBuilder: FilesUploadRequestBuilder) {
        val request = requestBuilder.token(token).build()

        slack.methods(token).filesUpload(request).processErrors()
    }
}
