package me.bors.slack.share.processor

import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.files.FilesUploadRequest
import com.slack.api.methods.request.files.FilesUploadRequest.FilesUploadRequestBuilder
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import me.bors.slack.share.client.SlackClient
import me.bors.slack.share.entity.MessageStyle
import java.io.File

class MessageProcessor(val client: SlackClient) {
    fun sendMessage(id: String, text: String, formatType: MessageStyle, fileExtension: String = "") {
        val builder = ChatPostMessageRequest.builder()
            .channel(id)

        when (formatType) {
            MessageStyle.NONE -> {
                builder
                    .mrkdwn(false)
                    .text(text)
            }

            MessageStyle.QUOTED -> {
                builder
                    .mrkdwn(true)
                    .text("")
                    .blocks(
                        listOf(
                            SectionBlock.builder()
                                .text(
                                    MarkdownTextObject.builder()
                                        .text("```$text```")
                                        .build()
                                )
                                .build()
                        )
                    )
            }

            MessageStyle.CODE_SNIPPET -> {
                sendSingleFile(id, text.toByteArray(), "snippet.$fileExtension", FilesUploadRequest.builder())

                return
            }
        }

        client.sendMessage(builder)
    }

    fun sendFile(id: String, files: List<File>, text: String) {
        var tagged = false

        for (file: File in files) {
            val builder = FilesUploadRequest.builder()

            if (text.isNotEmpty() && !tagged) {
                builder.initialComment(text)

                tagged = true
            }

            sendSingleFile(id, file.readBytes(), file.name, builder)
        }
    }

    private fun sendSingleFile(
        id: String,
        fileBytes: ByteArray,
        fileName: String,
        builder: FilesUploadRequestBuilder
    ) {
        builder
            .channels(listOf(id))
            .fileData(fileBytes)
            .filename(fileName)
            .filetype("auto")

        client.sendFile(builder)
    }
}
