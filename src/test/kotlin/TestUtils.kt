package me.bors.slack.share

import java.io.FileInputStream
import java.util.Properties

val properties = loadProperies()

fun loadProperies(): Properties {
    val props = Properties()

    props.load(FileInputStream("secret.properties"))

    return props
}
