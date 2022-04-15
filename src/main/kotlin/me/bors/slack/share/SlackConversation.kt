package me.bors.slack.share

class SlackConversation(
    val id: String,
    val name: String,
    val priority: Double,
) {
    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SlackConversation

        if (id != other.id) return false
        if (name != other.name) return false
        if (priority != other.priority) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + priority.hashCode()
        return result
    }
}
