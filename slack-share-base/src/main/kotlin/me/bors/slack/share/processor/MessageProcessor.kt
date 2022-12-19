package me.bors.slack.share.processor

import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.files.FilesUploadRequest
import com.slack.api.methods.request.files.FilesUploadRequest.FilesUploadRequestBuilder
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import me.bors.slack.share.client.SlackClient
import me.bors.slack.share.entity.MessageFormatType
import java.io.File

class MessageProcessor(val client: SlackClient) {
    fun sendMessage(id: String, text: String, formatType: MessageFormatType, fileExtension: String? = null) {
        val builder = ChatPostMessageRequest.builder()

        when (formatType) {
            MessageFormatType.DEFAULT -> {
                builder
                    .mrkdwn(false)
                    .text(text)
            }

            MessageFormatType.QUOTED -> {
                builder
                    .mrkdwn(true)
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

            MessageFormatType.HIGHLIGHTED -> {
                // TODO Double check if file headers and other stuff are necessary
                sendSingleFile(id, text.toByteArray(), "snippet.$fileExtension", FilesUploadRequest.builder())
            }
        }

        client.sendMessage(builder)
    }

    fun sendFile(id: String, files: List<File>, text: String) {
        var tagged = false

        for (file: File in files) {
            val fileBytes = file.readBytes()
            val fileName = file.name

            val builder = FilesUploadRequest.builder()

            if (text.isNotEmpty() && !tagged) {
                builder.initialComment(text)

                tagged = true
            }

            sendSingleFile(id, fileBytes, fileName, builder)
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


        client.sendFile(builder)
    }
}
