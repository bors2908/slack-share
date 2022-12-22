package me.bors.slack.share.error

open class SlackClientException(message: String) : RuntimeException(message)

class SlackTokenValidationException(message: String) : SlackClientException(message)
