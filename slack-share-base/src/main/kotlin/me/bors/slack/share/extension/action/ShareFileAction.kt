package me.bors.slack.share.extension.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import me.bors.slack.share.service.ActionService

class ShareFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        service<ActionService>().shareFileAction(e)
    }

    override fun update(e: AnActionEvent) {
        service<ActionService>().fileUpdate(e)
    }

    override fun isDumbAware(): Boolean {
        return true
    }
}
