package me.bors.slack.share.persistence

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe

abstract class SecretState {
    abstract val credentialAttributes: CredentialAttributes

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
        PasswordSafe.instance.set(credentialAttributes, null)
    }
}
