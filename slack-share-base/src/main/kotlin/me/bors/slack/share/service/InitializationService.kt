package me.bors.slack.share.service

import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.ProjectManager
import me.bors.slack.share.processor.MessageProcessor
import me.bors.slack.share.ui.settings.TokenSettingsConfigurable
import me.bors.slack.share.ui.share.dialog.OpenSettingsErrorDialogWrapper

abstract class InitializationService {
    val messageProcessor: MessageProcessor = MessageProcessor()

    private val workspaceService: WorkspaceService = service()

    private val conversationsService: ConversationsService = service()

    abstract fun getTokenSettingsConfigurable(): TokenSettingsConfigurable

    init {
        beforeInit()

        conversationsService.refresh()
    }

    fun showSettings(error: String, title: String) {
        if (OpenSettingsErrorDialogWrapper(error, title).showAndGet()) {
            ShowSettingsUtil.getInstance()
                .editConfigurable(ProjectManager.getInstance().defaultProject, getTokenSettingsConfigurable())
        }
    }

    open fun beforeInit() {
        // No-op.
    }

    open fun reloadCaches() {
        workspaceService.refresh()

        conversationsService.forceRefresh()
    }
}
