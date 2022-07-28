package me.bors.slack.share.ui.share.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.service
import me.bors.slack.share.processor.ConversationsProcessor
import me.bors.slack.share.service.InitializationService
import me.bors.slack.share.ui.share.dialog.ShareDialogWrapper

class ShareSnippetAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val initService : InitializationService = service()

        val slackClient = initService.initializeAndGetClient() ?: return

        val selectedText = getSelectedText(e) ?: ""

        val processor = ConversationsProcessor(slackClient)

        val conversations = processor.getConversations()

        val dialogWrapper = ShareDialogWrapper(
            conversations = conversations,
            text = selectedText
        )

        val exitCode = dialogWrapper.showAndGet()

        if (exitCode) {
            slackClient.sendMessage(
                dialogWrapper.getSelectedItem().id,
                dialogWrapper.getEditedText(),
                dialogWrapper.isQuotedCode()
            )
        }
    }

    override fun update(e: AnActionEvent) {
        val selectedText = getSelectedText(e)

        e.presentation.isEnabledAndVisible = selectedText != null
    }

    private fun getSelectedText(e: AnActionEvent): String? {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return ""

        return editor.selectionModel.selectedText
    }

    override fun isDumbAware(): Boolean {
        return true
    }
}
