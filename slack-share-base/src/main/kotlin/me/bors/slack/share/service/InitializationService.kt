package me.bors.slack.share.service

import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.ProjectManager
import me.bors.slack.share.auth.Authenticator
import me.bors.slack.share.client.SlackClient
import me.bors.slack.share.client.SlackTokenValidationException
import me.bors.slack.share.ui.settings.TokenSettingsConfigurable
import me.bors.slack.share.ui.share.dialog.TokenErrorDialogWrapper

interface InitializationService {
    fun getTokenSettingsConfigurable(): TokenSettingsConfigurable

    fun beforeInit() {
        // No-op.
    }

    /***
     * @return SlackClient or null if unsuccessful.
     */
    fun initializeAndGetClient(): SlackClient? {
        beforeInit()

        val authenticator: Authenticator = service()

        if (!authenticator.isTokenPresent()) {
            showSettings("No token found")

            return null
        }

        val token = authenticator.getToken() ?: throw IllegalArgumentException("No token provided.")

        return try {
            SlackClient(token)
        } catch (e: SlackTokenValidationException) {
            showSettings(e.message ?: "Unknown error.")

            null
        }
    }

    private fun showSettings(error: String) {
        if (TokenErrorDialogWrapper(error).showAndGet()) {
            ShowSettingsUtil.getInstance()
                .editConfigurable(ProjectManager.getInstance().defaultProject, getTokenSettingsConfigurable())
        }
    }
}
