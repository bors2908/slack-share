package me.bors.slack.share.client

import com.slack.api.methods.request.chat.ChatPostMessageRequest.ChatPostMessageRequestBuilder
import com.slack.api.methods.request.files.FilesUploadV2Request

open class SlackMessageClient : SlackClientBase() {
    fun sendMessage(token: String, requestBuilder: ChatPostMessageRequestBuilder) {
        val request = requestBuilder.token(token).build()

        slack.methods(token).chatPostMessage(request).processErrors()
    }

    fun sendFile(token: String, requestBuilder: FilesUploadV2Request.FilesUploadV2RequestBuilder) {
        val request = requestBuilder.token(token).build()

        slack.methods(token).filesUploadV2(request).processErrors()
    }
}
