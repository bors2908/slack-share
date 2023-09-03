package me.bors.slack.share.service

import com.intellij.openapi.components.service
import me.bors.slack.share.client.SlackConnectionTester
import me.bors.slack.share.processor.MessageProcessor
import me.bors.slack.share.ui.dialog.error.ErrorDialogWrapper

open class InitializationService {
    val messageProcessor: MessageProcessor = MessageProcessor()

    private val workspaceService: WorkspaceService = service()

    private val conversationsService: ConversationsService = service()

    init {
        @Suppress("LeakingThis")
        beforeInit()

        if (SlackConnectionTester.isSlackAccessible()) {
            conversationsService.refresh()
        }
    }

    open fun beforeInit() {
        // No-op.
    }

    open fun reloadCaches() {
        if (SlackConnectionTester.isSlackAccessible()) {
            workspaceService.refresh()

            conversationsService.forceRefresh()
        }
    }
}
