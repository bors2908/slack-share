package me.bors.slack.share

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages

class SlackShareSnippetAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val selectedText = getSelectedText(e)

        Messages.showMessageDialog(selectedText, "Text to Share", Messages.getInformationIcon())
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