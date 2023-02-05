package me.bors.slack.share.auth

import com.intellij.openapi.components.service
import me.bors.slack.share.SlackShareTestBase

class AuthenticatorTest : SlackShareTestBase() {
    private var authenticator = service<Authenticator>() as AutomaticAuthenticator

    fun testAutomaticAuth() {
        //TODO Complex UI and browser automation (Selenium?) test.
        //authenticator.authAutomatically()
    }

    fun testManualAuth() {
        //TODO UI test to check inserted values.
        //authenticator.authManually()
    }
}