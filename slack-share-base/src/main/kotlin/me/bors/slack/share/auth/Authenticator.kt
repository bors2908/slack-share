package me.bors.slack.share.auth

import me.bors.slack.share.persistence.SlackUserTokenSecretState
import me.bors.slack.share.ui.settings.dialog.AddTokenManualDialogWrapper

interface Authenticator {
    fun authManually() {
        val wrapper = AddTokenManualDialogWrapper()

        if (wrapper.showAndGet()) {
            SlackUserTokenSecretState.set(wrapper.field.text)
        }
    }

    fun remove() {
        SlackUserTokenSecretState.remove()
    }

    fun isTokenPresent(): Boolean {
        return SlackUserTokenSecretState.exists()
    }

    fun getToken(): String? {
        return SlackUserTokenSecretState.get()
    }
}
