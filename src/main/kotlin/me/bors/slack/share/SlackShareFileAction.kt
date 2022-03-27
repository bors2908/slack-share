package me.bors.slack.share

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile

class SlackShareFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val slackClient = SlackClient()

        val files = (getVirtualFiles(e) ?: emptyArray()).asList()

        val filenames = files.map { it.name }

        val dialogWrapper = ShareDialogWrapper(
            slackClient = slackClient,
            filenames = filenames
        )

        val exitCode = dialogWrapper.showAndGet()

        if (exitCode) {
            slackClient.sendFile(
                id = dialogWrapper.getSelectedItem().first,
                files = files.map { it.toNioPath().toFile() },
                text = dialogWrapper.getEditedText()
            )
        }
    }

    override fun update(e: AnActionEvent) {
        val virtualFiles = getVirtualFiles(e)

        e.presentation.isEnabledAndVisible = virtualFiles != null && virtualFiles.isNotEmpty()
    }

    private fun getVirtualFiles(e: AnActionEvent): Array<VirtualFile>? =
        e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)

    override fun isDumbAware(): Boolean {
        return true
    }
}