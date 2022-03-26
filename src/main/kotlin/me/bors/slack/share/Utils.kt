package me.bors.slack.share

import java.io.FileNotFoundException
import java.util.Properties

object Utils {
    fun getProperties(fileName: String): Properties {
        val properties = Properties()

        val inputStream = javaClass.classLoader.getResourceAsStream(fileName)

        if (inputStream != null) {
            properties.load(inputStream)
        } else {
            throw FileNotFoundException("Could not find property file $fileName")
        }

        return properties
    }
}