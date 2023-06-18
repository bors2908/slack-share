package me.bors.slack.share.persistence

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName

open class BasicSecretState(key: String) : SecretState() {
    override val credentialAttributes: CredentialAttributes = CredentialAttributes(generateServiceName("SlackSecrets", key))
}
