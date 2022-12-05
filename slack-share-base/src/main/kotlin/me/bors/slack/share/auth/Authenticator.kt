package me.bors.slack.share.auth

import com.intellij.ide.BrowserUtil
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import me.bors.slack.share.persistence.SlackUserTokenSecretState
import me.bors.slack.share.ui.settings.dialog.AddTokenManualDialogWrapper
import me.bors.slack.share.ui.settings.dialog.CreateSlackAppDialogWrapper
import okhttp3.HttpUrl

interface Authenticator {
    fun authManually() {
        val wrapper = AddTokenManualDialogWrapper {
            BrowserUtil.browse(createAppUri)

            CreateSlackAppDialogWrapper(createAppUri).showAndGet()
        }

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

    companion object {
        val SCOPE_LIST = listOf(
            "channels:read",
            "chat:write",
            "files:write",
            "groups:read",
            "im:read",
            "mpim:read",
            "users:read"
        )

        private val createAppUri = HttpUrl.Builder()
            .scheme("https")
            .host("api.slack.com")
            .addPathSegment("apps")
            .addQueryParameter("new_app", "1")
            .addQueryParameter("manifest_json", getJsonManifest())
            .build()
            .toUrl()
            .toURI()

        private fun getJsonManifest() = JsonObject(
            mapOf(
                "display_information" to JsonObject(
                    mapOf(
                        "name" to JsonPrimitive("Share from JetBrains")
                    )
                ),
                "oauth_config" to JsonObject(
                    mapOf(
                        "scopes" to JsonObject(
                            mapOf(
                                "user" to JsonArray(SCOPE_LIST.map { JsonPrimitive(it) })
                            )
                        )
                    )
                ),
                "settings" to JsonObject(
                    mapOf(
                        "org_deploy_enabled" to JsonPrimitive(false),
                        "socket_mode_enabled" to JsonPrimitive(false),
                        "token_rotation_enabled" to JsonPrimitive(false)
                    )
                )
            )
        ).toString()
    }
}
