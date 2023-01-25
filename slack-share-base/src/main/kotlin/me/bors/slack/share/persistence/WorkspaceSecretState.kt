package me.bors.slack.share.persistence

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName

class WorkspaceSecretState(id: Int) : SecretState() {
    override val credentialAttributes: CredentialAttributes = if (id < MAX_ACCOUNTS) {
        CredentialAttributes(generateServiceName("SlackWorkspaces", id.toString()))
    } else {
        throw IllegalStateException("Credential ID should be lower than $MAX_ACCOUNTS")
    }

    companion object {
        val MAX_ACCOUNTS = 10
    }
}
