package me.bors.slack.share.error

//TODO Move Exceptions
open class SlackClientException(message: String) : RuntimeException(message)

class SlackTokenValidationException(message: String) : SlackClientException(message)
