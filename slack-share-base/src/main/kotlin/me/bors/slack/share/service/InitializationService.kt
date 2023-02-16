package me.bors.slack.share.service

import com.intellij.openapi.components.service
import me.bors.slack.share.processor.MessageProcessor

open class InitializationService {
    val messageProcessor: MessageProcessor = MessageProcessor()

    private val workspaceService: WorkspaceService = service()

    private val conversationsService: ConversationsService = service()

    init {
        @Suppress("LeakingThis")
        beforeInit()

        conversationsService.refresh()
    }

    open fun beforeInit() {
        // No-op.
    }

    open fun reloadCaches() {
        workspaceService.refresh()

        conversationsService.forceRefresh()
    }
}
