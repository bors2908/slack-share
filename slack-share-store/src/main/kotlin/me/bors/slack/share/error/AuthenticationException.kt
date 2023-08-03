package me.bors.slack.share.error

class AuthenticationException(msg: String) : RuntimeException(msg) {
    companion object {
        private const val serialVersionUID: Long = 5757563299098491416L
    }
}
