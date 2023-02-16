package me.bors.slack.share

import me.bors.slack.share.auth.Authenticator
import java.awt.Desktop

class CreateSlackTestApp {
    fun createTestApp() {
        val extraScopes = listOf(
            "channels:history",
            "groups:history",
            "im:history",
            "mpim:history",
        )

        val createTestAppUrl = Authenticator.getCreateAppUri(
            Authenticator.SCOPE_LIST + extraScopes,
            "Share from JetBrains - Test App"
        )

        println(
            "You will be redirected to Slack to create Slack App, required to perform tests. " +
                "Install app to workspace, copy token and paste it to 'secrets/test-tokens.properties'"
        )

        Desktop.getDesktop().browse(createTestAppUrl)
    }
}

// Run this to create test app to properly run tests.
fun main() {
    CreateSlackTestApp().createTestApp()
}
