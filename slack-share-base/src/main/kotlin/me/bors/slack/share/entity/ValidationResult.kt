package me.bors.slack.share.entity

data class ValidationResult(
    val name: String = "",
    val slackId: String = "",
    val error: String? = null
)
