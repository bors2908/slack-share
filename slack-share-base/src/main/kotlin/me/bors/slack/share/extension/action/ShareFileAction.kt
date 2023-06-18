package me.bors.slack.share.extension.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.VirtualFile
import me.bors.slack.share.service.ActionService

@Suppress("ComponentNotRegistered")
class ShareFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val files = (getVirtualFiles(e) ?: emptyArray()).asList()
            .map { it.toNioPath().toFile() }

        service<ActionService>().shareFileAction(files)
    }

    override fun update(e: AnActionEvent) {
        val virtualFiles = getVirtualFiles(e)

        e.presentation.isEnabledAndVisible = !virtualFiles.isNullOrEmpty()
    }

    private fun getVirtualFiles(e: AnActionEvent): Array<VirtualFile>? =
        e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)

    override fun isDumbAware(): Boolean {
        return true
    }
}
