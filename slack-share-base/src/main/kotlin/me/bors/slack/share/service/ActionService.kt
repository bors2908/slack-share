package me.bors.slack.share.service

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile
import me.bors.slack.share.entity.FileExclusion
import me.bors.slack.share.ui.dialog.ShareDialogWrapper
import java.io.File

@Service
class ActionService {
    private val initService: InitializationService = service()

    private val workspaceService: WorkspaceService = service()

    private val conversationsService: ConversationsService = service()

    private val settingsService: SettingsService = service()

    fun shareSnippetAction(e: AnActionEvent) {
        val selectedText = getSelectedText(e) ?: ""

        workspaceService.refresh()

        if (workspaceService.getAvailableWorkspaces().isEmpty()) {
            settingsService.showSettings("No workspaces found", "Empty Workspaces")

            return
        }

        val dialogWrapper = ShareDialogWrapper(
            workspaces = workspaceService.getAvailableWorkspaces(),
            text = selectedText,
            snippetFileExtension = getSnippetFileExtension(e),
            conversationProcessing = { conversationsService.getConversations(it) }
        )

        val exitCode = dialogWrapper.showAndGet()

        val messageProcessor = initService.messageProcessor

        if (exitCode) {
            messageProcessor.sendMessage(
                dialogWrapper.getSelectedWorkspace(),
                dialogWrapper.getSelectedConversation().id,
                dialogWrapper.getEditedText(),
                dialogWrapper.getMessageFormatType(),
                dialogWrapper.getEditedSnippetFileExtension()
            )
        }
    }

    fun shareFileAction(e: AnActionEvent) {
        workspaceService.refresh()

        if (workspaceService.getAvailableWorkspaces().isEmpty()) {
            settingsService.showSettings("No workspaces found", "Empty Workspaces")

            return
        }

        val files = (getVirtualFiles(e) ?: emptyArray()).asList()
            .map { it.toNioPath().toFile() }
            .toMutableList()

        val (validFiles, exclusions) = excludeInvalidFiles(files)

        val filenames = validFiles.map { it.name }

        val dialogWrapper = ShareDialogWrapper(
            workspaces = workspaceService.getAvailableWorkspaces(),
            filenames = filenames,
            fileExclusions = exclusions,
            conversationProcessing = { conversationsService.getConversations(it) }
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

    fun snippetUpdate(e: AnActionEvent) {
        val selectedText = getSelectedText(e)

        e.presentation.isEnabledAndVisible = selectedText != null
    }

    fun fileUpdate(e: AnActionEvent) {
        val virtualFiles = getVirtualFiles(e)

        e.presentation.isEnabledAndVisible = !virtualFiles.isNullOrEmpty()
    }

    private fun getSelectedText(e: AnActionEvent): String? {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return ""

        return editor.selectionModel.selectedText
    }

    private fun getSnippetFileExtension(e: AnActionEvent): String {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return ""

        return FileDocumentManager.getInstance().getFile(editor.document)?.extension ?: ""
    }

    private fun getVirtualFiles(e: AnActionEvent): Array<VirtualFile>? =
        e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)


    private fun excludeInvalidFiles(files: MutableList<File>): Pair<List<File>, List<FileExclusion>> {
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
}
