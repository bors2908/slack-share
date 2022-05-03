package me.bors.slack.share

import org.junit.jupiter.api.Test

class TestAuth {
    @Test
    fun testAuth() {
        val auth = SlackAuthenticator()
        auth.auth()
    }
}