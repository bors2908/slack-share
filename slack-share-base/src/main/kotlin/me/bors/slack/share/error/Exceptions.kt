package me.bors.slack.share.error

open class SlackClientException(message: String) : RuntimeException(message) {
    companion object {
        private const val serialVersionUID: Long = 0L
    }
}
