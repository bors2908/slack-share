package me.bors.slack.share.persistence

private const val SLACK_SHARE_CLIENT_ID_KEY = "slackShareClientId"
private const val SLACK_SHARE_SECRET_KEY = "slackShareSecret"

object SlackShareClientId : SecretState(SLACK_SHARE_CLIENT_ID_KEY)

object SlackShareSecret : SecretState(SLACK_SHARE_SECRET_KEY)
