package me.bors.slack.share.service

import me.bors.slack.share.processor.ConversationsProcessor
import me.bors.slack.share.processor.MessageProcessor
import me.bors.slack.share.ui.settings.TokenSettingsConfigurable

abstract class InitializationService {
    val messageProcessor: MessageProcessor = MessageProcessor()

    val conversationsProcessor: ConversationsProcessor = ConversationsProcessor()

    abstract fun getTokenSettingsConfigurable(): TokenSettingsConfigurable

    init {
        beforeInit()
    }

    open fun beforeInit() {
        // No-op.
    }
}
