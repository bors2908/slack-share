package me.bors.slack.share.error

class SlackShareBundledFileException : RuntimeException("Data file, bundled with plugin, required for authentication was not found.") {
    companion object {
        private const val serialVersionUID: Long = 0L
    }
}

class AuthenticationException(msg: String) : RuntimeException(msg) {
    companion object {
        private const val serialVersionUID: Long = 0L
    }
}

