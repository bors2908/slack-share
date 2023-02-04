package me.bors.slack.share.extension.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import me.bors.slack.share.service.ActionService

class ShareSnippetAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        service<ActionService>().shareSnippetAction(e)
    }

    override fun update(e: AnActionEvent) {
        service<ActionService>().snippetUpdate(e)
    }

    override fun isDumbAware(): Boolean {
        return true
    }
}
