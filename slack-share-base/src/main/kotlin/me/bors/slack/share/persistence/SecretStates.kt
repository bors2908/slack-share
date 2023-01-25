package me.bors.slack.share.persistence

private const val SLACK_USER_TOKEN_KEY = "slackUserToken"

@Deprecated("To be removed after a couple of updates.")
object SlackUserTokenBasicSecretState : BasicSecretState(SLACK_USER_TOKEN_KEY)
