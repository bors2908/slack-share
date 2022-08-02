package me.bors.slack.share

import me.bors.slack.share.auth.AutomaticAuthenticator
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TestAuth {
    @Disabled
    @Test
    fun testAuth() {
        AutomaticAuthenticator.authAutomatically()
    }
}