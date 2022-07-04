package me.bors.slack.share.persistence

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe

open class SecretState(key: String) {
    private val credentialAttributes = CredentialAttributes(generateServiceName("SlackSecrets", key))

    fun set(value: String?) {
        val credentials = Credentials("", value)

        PasswordSafe.instance.set(credentialAttributes, credentials)
    }

    fun get(): String? {
        val credentials = PasswordSafe.instance.get(credentialAttributes)

        return credentials?.getPasswordAsString()
    }

    fun exists(): Boolean {
        return get() != null
    }

    fun remove() {
        set(null)
    }
}
