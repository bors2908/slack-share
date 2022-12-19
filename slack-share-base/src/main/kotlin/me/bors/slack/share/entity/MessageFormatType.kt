package me.bors.slack.share.entity

enum class MessageFormatType(val type: String) {
    DEFAULT("Default"),

    QUOTED("Quoted"),

    HIGHLIGHTED("Highlighted")
}
