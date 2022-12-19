package me.bors.slack.share.service

import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.ProjectManager
import me.bors.slack.share.auth.Authenticator
import me.bors.slack.share.client.SlackClient
import me.bors.slack.share.error.SlackTokenValidationException
import me.bors.slack.share.processor.ConversationsProcessor
import me.bors.slack.share.processor.MessageProcessor
import me.bors.slack.share.ui.settings.TokenSettingsConfigurable
import me.bors.slack.share.ui.share.dialog.TokenErrorDialogWrapper

abstract class InitializationService {
    @Volatile
    private var init: Boolean = false

    private lateinit var slackClient: SlackClient

    private lateinit var messageProcessor: MessageProcessor

    private lateinit var conversationsProcessor: ConversationsProcessor

    abstract fun getTokenSettingsConfigurable(): TokenSettingsConfigurable

    fun beforeInit() {
        // No-op.
    }

    //TODO Rearrange returns
    @Suppress("ReturnCount")
    fun initializeIfNot(): Boolean {
        if (init) {
            return true
        } else {
            beforeInit()

            val authenticator: Authenticator = service()

            if (!authenticator.isTokenPresent()) {
                showSettings("No token found")

                return false
            }

            val token = authenticator.getToken() ?: throw IllegalArgumentException("No token provided.")

            try {
                slackClient = SlackClient(token)

                messageProcessor = MessageProcessor(slackClient)

                conversationsProcessor = ConversationsProcessor(slackClient)

            } catch (e: SlackTokenValidationException) {
                showSettings(e.message ?: "Unknown error.")

                return false
            }

            init = true

            return true
        }
    }

    fun getSlackClient(): SlackClient {
        initializeIfNot()

        return slackClient
    }

    fun getMessageProcessor(): MessageProcessor {
        initializeIfNot()

        return messageProcessor
    }

    fun getConversationsProcessor(): ConversationsProcessor {
        initializeIfNot()

        return conversationsProcessor
    }

    private fun showSettings(error: String) {
        if (TokenErrorDialogWrapper(error).showAndGet()) {
            ShowSettingsUtil.getInstance()
                .editConfigurable(ProjectManager.getInstance().defaultProject, getTokenSettingsConfigurable())
        }
    }
}
