package me.bors.slack.share

class Configuration(
    val startPort: Int?,
    val endPort: Int?
)

val configuration = Configuration(
    6969,
    6979
)