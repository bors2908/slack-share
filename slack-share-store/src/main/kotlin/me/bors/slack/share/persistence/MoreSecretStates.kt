package me.bors.slack.share.persistence

private const val SLACK_SHARE_CLIENT_ID_KEY = "slackShareClientId"
private const val SLACK_SHARE_SECRET_KEY = "slackShareSecret"

object ShareClientId : BasicSecretState(SLACK_SHARE_CLIENT_ID_KEY)

object SlackShareBasicSecret : BasicSecretState(SLACK_SHARE_SECRET_KEY)
