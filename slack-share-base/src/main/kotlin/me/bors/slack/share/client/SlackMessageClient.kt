package me.bors.slack.share.client

import com.slack.api.methods.request.chat.ChatPostMessageRequest.ChatPostMessageRequestBuilder
import com.slack.api.methods.request.files.FilesUploadRequest.FilesUploadRequestBuilder

open class SlackMessageClient : SlackClientBase() {
    fun sendMessage(token: String, requestBuilder: ChatPostMessageRequestBuilder) {
        val request = requestBuilder.token(token).build()

        wrapOfflineException { slack.methods(token).chatPostMessage(request).processErrors() }
    }

    fun sendFile(token: String, requestBuilder: FilesUploadRequestBuilder) {
        val request = requestBuilder.token(token).build()

        wrapOfflineException { slack.methods(token).filesUpload(request).processErrors() }
    }
}
