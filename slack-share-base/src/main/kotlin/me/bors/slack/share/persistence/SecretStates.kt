package me.bors.slack.share.persistence

private const val SLACK_USER_TOKEN_KEY = "slackUserToken"

object SlackUserTokenSecretState : SecretState(SLACK_USER_TOKEN_KEY)