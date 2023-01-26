package me.bors.slack.share.ui.share.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.VirtualFile
import me.bors.slack.share.entity.FileExclusion
import me.bors.slack.share.service.InitializationService
import me.bors.slack.share.service.WorkspaceService
import me.bors.slack.share.ui.share.dialog.ShareDialogWrapper
import java.io.File

class ShareFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val initService: InitializationService = service()

        val workspaceProcessor: WorkspaceService = service()

        workspaceProcessor.refresh()

        val conversationsProcessor = initService.conversationsProcessor

        val files = (getVirtualFiles(e) ?: emptyArray()).asList()
            .map { it.toNioPath().toFile() }
            .toMutableList()

        val (validFiles, exclusions) = excludeInvalidFiles(files)

        val filenames = validFiles.map { it.name }

        val dialogWrapper = ShareDialogWrapper(
            workspaces = workspaceProcessor.getAvailableWorkspaces(),
            filenames = filenames,
            fileExclusions = exclusions,
            conversationProcessing = { conversationsProcessor.getConversations(it) }
        )

        val exitCode = dialogWrapper.showAndGet()

        val messageProcessor = initService.messageProcessor

        if (exitCode) {
            messageProcessor.sendFile(
                workspace = dialogWrapper.getSelectedWorkspace(),
                userId = dialogWrapper.getSelectedConversation().id,
                files = validFiles,
                text = dialogWrapper.getEditedText()
            )
        }
    }

    fun excludeInvalidFiles(files: MutableList<File>): Pair<List<File>, List<FileExclusion>> {
        val exclusions = mutableListOf<FileExclusion>()
        val validFiles = mutableListOf<File>()

        files.forEach {
            if (!it.exists()) {
                exclusions.add(FileExclusion(it, "File not found."))
            } else if (it.isDirectory) {
                exclusions.add(FileExclusion(it, "Cannot send directory."))
            } else {
                validFiles.add(it)
            }
        }

        return validFiles to exclusions
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
