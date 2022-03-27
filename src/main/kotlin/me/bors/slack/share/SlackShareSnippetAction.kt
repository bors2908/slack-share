package me.bors.slack.share

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys

class SlackShareSnippetAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val slackClient = SlackClient()

        val selectedText = getSelectedText(e) ?: ""

        val conversations = slackClient.getConversations()

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
        val editor = e.getData(PlatformDataKeys.EDITOR)!!

        return editor.selectionModel.selectedText
    }

    override fun isDumbAware(): Boolean {
        return true
    }
}