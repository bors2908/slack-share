package me.bors.slack.share.extension.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileDocumentManager
import me.bors.slack.share.service.ActionService

@Suppress("ComponentNotRegistered")
class ShareSnippetAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val selectedText = getSelectedText(e) ?: ""

        val snippetFileExtension = getSnippetFileExtension(e)

        service<ActionService>().shareSnippetAction(selectedText, snippetFileExtension)
    }

    override fun update(e: AnActionEvent) {
        val selectedText = getSelectedText(e)

        e.presentation.isEnabledAndVisible = selectedText != null
    }

    override fun isDumbAware(): Boolean {
        return true
    }


    private fun getSelectedText(e: AnActionEvent): String? {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return ""

        return editor.selectionModel.selectedText
    }

    private fun getSnippetFileExtension(e: AnActionEvent): String {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return ""

        return FileDocumentManager.getInstance().getFile(editor.document)?.extension ?: ""
    }

}
