package me.bors.slack.share.error

class SlackShareBundledFileException : RuntimeException("Data file, bundled with plugin, required for authentication was not found.")

class AuthenticationException(msg: String) : RuntimeException(msg)

