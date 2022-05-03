package me.bors.slack.share

import java.io.FileInputStream
import java.nio.file.FileSystems
import java.util.Properties
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.SystemUtils

// TODO Add proper auth or at least store token safely
object Utils {
    fun getToken(): String {
        var path = ""

        if (SystemUtils.IS_OS_WINDOWS) path = "${System.getenv("LOCALAPPDATA")}\\"
        else if (SystemUtils.IS_OS_MAC_OSX) path = "~/Library/Application Support/"
        else if (SystemUtils.IS_OS_LINUX) path = "~/.local/share/"

        val separator = FileSystems.getDefault().separator

        val filepath = "${path}${separator}slack-share${separator}slack-share"

        val fis = FileInputStream(filepath)

        return IOUtils.toString(fis, "UTF-8")
    }

    fun getProperties(path: String): Properties {
        val props = Properties()

        props.load(javaClass.classLoader.getResourceAsStream(path))

        return props
    }
}
