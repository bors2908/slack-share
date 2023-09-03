package me.bors.slack.share.service

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import java.io.File
import me.bors.slack.share.client.SlackConnectionTester
import me.bors.slack.share.entity.FileExclusion
import me.bors.slack.share.ui.dialog.ShareDialogWrapper
import me.bors.slack.share.ui.dialog.error.ErrorDialogWrapper
import me.bors.slack.share.ui.dialog.error.SlackOfflineErrorDialogWrapper

@Service
class ActionService {
    private val initService: InitializationService = service()

    private val workspaceService: WorkspaceService = service()

    private val conversationsService: ConversationsService = service()

    private val settingsService: SettingsService = service()

    private val application = ApplicationManager.getApplication()

    fun shareSnippetAction(selectedText: String, snippetFileExtension: String) {
        if (!SlackConnectionTester.isSlackAccessible()) {
            application.invokeLater {
                SlackOfflineErrorDialogWrapper().showAndGet()
            }

            return
        }

        workspaceService.refresh()

        if (workspaceService.getAvailableWorkspaces().isEmpty()) {
            settingsService.showSettings("No workspaces found", "Empty Workspaces")

            return
        }

        application.invokeLater {
            val dialogWrapper = ShareDialogWrapper(
                workspaces = workspaceService.getAvailableWorkspaces(),
                text = selectedText,
                snippetFileExtension = snippetFileExtension,
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
    }

    fun shareFileAction(files: List<File>) {
        if (!SlackConnectionTester.isSlackAccessible()) {
            application.invokeLater {
                SlackOfflineErrorDialogWrapper().showAndGet()
            }

            return
        }

        workspaceService.refresh()

        if (workspaceService.getAvailableWorkspaces().isEmpty()) {
            settingsService.showSettings("No workspaces found", "Empty Workspaces")

            return
        }

        val (validFiles, exclusions) = excludeInvalidFiles(files.toMutableList())

        val filenames = validFiles.map { it.name }

        application.invokeLater {
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
    }

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
