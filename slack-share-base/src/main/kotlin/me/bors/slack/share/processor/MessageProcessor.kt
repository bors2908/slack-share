package me.bors.slack.share.processor

import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.files.FilesUploadV2Request
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import java.io.File
import me.bors.slack.share.client.SlackMessageClient
import me.bors.slack.share.entity.MessageStyle
import me.bors.slack.share.entity.Workspace
import me.bors.slack.share.ui.dialog.error.ErrorDialogWrapper

class MessageProcessor {
    private val client = SlackMessageClient()

    fun sendMessage(
        workspace: Workspace,
        userId: String,
        text: String,
        formatType: MessageStyle,
        fileExtension: String
    ) {
        val resultingFileExtension = fileExtension.ifBlank { "txt" }

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
                sendSingleFile(
                    token,
                    userId,
                    text.toByteArray(),
                    "snippet.$resultingFileExtension",
                    FilesUploadV2Request.builder()
                )

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
            val builder = FilesUploadV2Request.builder()

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
        builder: FilesUploadV2Request.FilesUploadV2RequestBuilder
    ) {
        builder
            .channels(listOf(userId))
            .fileData(fileBytes)
            .filename(fileName)
            .snippetType("auto")

        client.sendFile(token, builder)
    }
}
