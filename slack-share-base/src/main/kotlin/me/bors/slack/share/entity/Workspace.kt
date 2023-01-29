package me.bors.slack.share.entity

import me.bors.slack.share.persistence.WorkspaceSecretState

class Workspace(
    val id: Int,
    val slackId: String,
    var name: String,
    var valid: Boolean = true
) {
    val state: WorkspaceSecretState = WorkspaceSecretState(id)

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Workspace

        if (id != other.id) return false
        if (slackId != other.slackId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + slackId.hashCode()
        return result
    }
}
