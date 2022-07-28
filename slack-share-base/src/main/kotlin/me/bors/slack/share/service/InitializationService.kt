package me.bors.slack.share.service

import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.ProjectManager
import me.bors.slack.share.client.SlackClient
import me.bors.slack.share.client.SlackTokenValidationException
import me.bors.slack.share.persistence.SlackUserTokenSecretState
import me.bors.slack.share.ui.settings.TokenSettingsConfigurable
import me.bors.slack.share.ui.share.dialog.TokenErrorDialogWrapper

abstract class InitializationService {
    abstract fun getTokenSettingsConfigurable(): TokenSettingsConfigurable

    /***
     * @return SlackClient or null if unsuccessful.
     */
    fun initializeAndGetClient() : SlackClient? {
        if (!SlackUserTokenSecretState.exists()) {
            showSettings("No token")

            return null
        }

        val token = SlackUserTokenSecretState.get() ?: throw IllegalArgumentException("No token provided.")

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