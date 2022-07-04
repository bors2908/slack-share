package me.bors.slack.share.ui.share.actions

import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.ProjectManager
import me.bors.slack.share.SlackClient
import me.bors.slack.share.SlackTokenValidationException
import me.bors.slack.share.persistence.SlackUserTokenSecretState
import me.bors.slack.share.ui.settings.TokenSettingsConfigurable
import me.bors.slack.share.ui.share.dialog.TokenErrorDialogWrapper

interface SlackClientAction {
    /***
     * @return SlackClient or null if unsuccessful.
     */
    fun validateTokenAndGetSlackClient(): SlackClient? {
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
                .editConfigurable(ProjectManager.getInstance().defaultProject, TokenSettingsConfigurable())
        }
    }
}
