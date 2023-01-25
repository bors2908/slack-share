package me.bors.slack.share.entity

import me.bors.slack.share.persistence.WorkspaceSecretState

class Workspace(
    var id: Int,
    var slackId: String,
    var name: String,
    var valid: Boolean = true
) {
    val state: WorkspaceSecretState = WorkspaceSecretState(id)

    override fun toString(): String {
        return name
    }
}
