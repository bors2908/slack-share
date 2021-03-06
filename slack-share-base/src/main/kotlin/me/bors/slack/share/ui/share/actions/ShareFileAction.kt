package me.bors.slack.share.ui.share.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.VirtualFile
import me.bors.slack.share.processor.ConversationsProcessor
import me.bors.slack.share.service.InitializationService
import me.bors.slack.share.ui.share.dialog.ShareDialogWrapper

class ShareFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val initService : InitializationService = service()

        val slackClient = initService.initializeAndGetClient() ?: return

        val files = (getVirtualFiles(e) ?: emptyArray()).asList()

        val filenames = files.map { it.name }

        val processor = ConversationsProcessor(slackClient)

        val conversations = processor.getConversations()

        val dialogWrapper = ShareDialogWrapper(
            conversations = conversations,
            filenames = filenames
        )

        val exitCode = dialogWrapper.showAndGet()

        if (exitCode) {
            slackClient.sendFile(
                id = dialogWrapper.getSelectedItem().id,
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
