package me.bors.slack.share.entity

enum class MessageStyle(private val style: String) {
    NONE("None"),

    QUOTED("Quoted"),

    CODE_SNIPPET("Code Snippet");

    override fun toString(): String {
        return style
    }
}
