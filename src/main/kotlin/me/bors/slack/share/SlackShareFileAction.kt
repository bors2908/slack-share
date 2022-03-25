package me.bors.slack.share

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import java.nio.charset.Charset
import java.nio.file.Files

class SlackShareFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val files = getVirtualFiles(e)!!

        val filesContent =
            files
                .map { it.toNioPath() }
                .map { Files.readAllLines(it, Charset.defaultCharset()) }
                .joinToString()

        Messages.showMessageDialog(filesContent, "File Content to Share", Messages.getInformationIcon())
    }

    override fun update(e: AnActionEvent) {
        val virtualFiles = getVirtualFiles(e)

        e.presentation.isEnabledAndVisible = virtualFiles != null && virtualFiles.isNotEmpty()
    }

    private fun getVirtualFiles(e: AnActionEvent): Array<out VirtualFile>? =
        e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)

    override fun isDumbAware(): Boolean {
        return true
    }
}