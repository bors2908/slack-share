package me.bors.slack.share.auth

import com.intellij.ide.BrowserUtil
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import me.bors.slack.share.ui.dialog.CreateSlackAppDialogWrapper
import me.bors.slack.share.ui.dialog.ManualAuthDialogWrapper
import okhttp3.HttpUrl
import java.net.URI

interface Authenticator {
    fun authManually(): String? {
        val wrapper = ManualAuthDialogWrapper {
            BrowserUtil.browse(createAppUri)

            CreateSlackAppDialogWrapper(createAppUri).showAndGet()
        }

        return if (wrapper.showAndGet()) {
            wrapper.getTokenText()
        } else null
    }

    companion object {
        val SCOPE_LIST: List<String> = listOf(
            "channels:read",
            "groups:read",
            "im:read",
            "mpim:read",
            "users:read",
            "chat:write",
            "files:write"
        )

        private val createAppUri = getCreateAppUri(SCOPE_LIST, "Share from JetBrains")

        fun getCreateAppUri(scopes: List<String>, name: String): URI = HttpUrl.Builder()
            .scheme("https")
            .host("api.slack.com")
            .addPathSegment("apps")
            .addQueryParameter("new_app", "1")
            .addQueryParameter("manifest_json", getJsonManifest(scopes, name))
            .build()
            .toUrl()
            .toURI()

        private fun getJsonManifest(scopes: List<String>, name: String) = JsonObject(
            mapOf(
                "display_information" to JsonObject(
                    mapOf(
                        "name" to JsonPrimitive(name)
                    )
                ),
                "oauth_config" to JsonObject(
                    mapOf(
                        "scopes" to JsonObject(
                            mapOf(
                                "user" to JsonArray(scopes.map { JsonPrimitive(it) })
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
