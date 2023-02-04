package me.bors.slack.share.processor

import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.files.FilesUploadRequest
import com.slack.api.methods.request.files.FilesUploadRequest.FilesUploadRequestBuilder
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import me.bors.slack.share.client.SlackMessageClient
import me.bors.slack.share.entity.MessageStyle
import me.bors.slack.share.entity.Workspace
import me.bors.slack.share.ui.dialog.error.ErrorDialogWrapper
import java.io.File

class MessageProcessor {
    private val client = SlackMessageClient()

    fun sendMessage(
        workspace: Workspace,
        userId: String,
        text: String,
        formatType: MessageStyle,
        fileExtension: String = ""
    ) {
        val builder = ChatPostMessageRequest.builder()
            .channel(userId)

        val token = workspace.state.get()

        if (token == null) {
            ErrorDialogWrapper("Token is missing.").showAndGet()

            return
        }

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
                sendSingleFile(token, userId, text.toByteArray(), "snippet.$fileExtension", FilesUploadRequest.builder())

                return
            }
        }

        client.sendMessage(token, builder)
    }

    fun sendFile(
        workspace: Workspace,
        userId: String,
        files: List<File>,
        text: String
    ) {
        var tagged = false

        val token = workspace.state.get()

        if (token == null) {
            ErrorDialogWrapper("Token is missing.").showAndGet()

            return
        }

        for (file: File in files) {
            val builder = FilesUploadRequest.builder()

            if (text.isNotEmpty() && !tagged) {
                builder.initialComment(text)

                tagged = true
            }

            sendSingleFile(token, userId, file.readBytes(), file.name, builder)
        }
    }

    private fun sendSingleFile(
        token: String,
        userId: String,
        fileBytes: ByteArray,
        fileName: String,
        builder: FilesUploadRequestBuilder
    ) {
        builder
            .channels(listOf(userId))
            .fileData(fileBytes)
            .filename(fileName)
            .filetype("auto")

        client.sendFile(token, builder)
    }
}
