package me.bors.slack.share.ui.share.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.util.DocumentUtil
import me.bors.slack.share.service.InitializationService
import me.bors.slack.share.ui.share.dialog.ShareDialogWrapper

class ShareSnippetAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val initService: InitializationService = service()

        if (!initService.initializeIfNot()) return

        val selectedText = getSelectedText(e) ?: ""

        val conversationsProcessor = initService.getConversationsProcessor()

        val conversations = conversationsProcessor.getConversations()

        val dialogWrapper = ShareDialogWrapper(
            conversations = conversations,
            text = selectedText
        )

        val exitCode = dialogWrapper.showAndGet()

        val messageProcessor = initService.getMessageProcessor()

        if (exitCode) {
            messageProcessor.sendMessage(
                dialogWrapper.getSelectedItem().id,
                dialogWrapper.getEditedText(),
                dialogWrapper.getMessageFormatType(),
                getFileExtension(e)
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

    private fun getFileExtension(e: AnActionEvent): String {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return ""

        return FileDocumentManager.getInstance().getFile(editor.document)?.extension ?: ""
    }

    override fun isDumbAware(): Boolean {
        return true
    }
}
