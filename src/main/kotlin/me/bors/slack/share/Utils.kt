package me.bors.slack.share

import java.util.Properties

object Utils {
    fun getProperties(path: String): Properties {
        val props = Properties()

        props.load(javaClass.classLoader.getResourceAsStream(path))

        return props
    }
}
