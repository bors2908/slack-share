package me.bors.slack.share.ui.share.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileDocumentManager
import me.bors.slack.share.service.ConversationsService
import me.bors.slack.share.service.InitializationService
import me.bors.slack.share.service.WorkspaceService
import me.bors.slack.share.ui.share.dialog.ShareDialogWrapper

class ShareSnippetAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val initService: InitializationService = service()

        val selectedText = getSelectedText(e) ?: ""

        val workspaceService: WorkspaceService = service()

        workspaceService.refresh()

        if (workspaceService.getAvailableWorkspaces().isEmpty()) {
            initService.showSettings("No workspaces found", "Empty Workspaces")

            return
        }

        val conversationsService: ConversationsService = service()

        val dialogWrapper = ShareDialogWrapper(
            workspaces = workspaceService.getAvailableWorkspaces(),
            text = selectedText,
            snippetFileExtension = getSnippetFileExtension(e),
            conversationProcessing = { conversationsService.getConversations(it) }
        )

        val exitCode = dialogWrapper.showAndGet()

        val messageProcessor = initService.messageProcessor

        if (exitCode) {
            messageProcessor.sendMessage(
                dialogWrapper.getSelectedWorkspace(),
                dialogWrapper.getSelectedConversation().id,
                dialogWrapper.getEditedText(),
                dialogWrapper.getMessageFormatType(),
                dialogWrapper.getEditedSnippetFileExtension()
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

    private fun getSnippetFileExtension(e: AnActionEvent): String {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return ""

        return FileDocumentManager.getInstance().getFile(editor.document)?.extension ?: ""
    }

    override fun isDumbAware(): Boolean {
        return true
    }
}
