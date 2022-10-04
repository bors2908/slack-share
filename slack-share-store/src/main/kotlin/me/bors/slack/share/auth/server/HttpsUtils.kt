package me.bors.slack.share.auth.server

import me.bors.slack.share.Configuration
import org.apache.http.HttpException
import java.io.IOException
import java.net.DatagramSocket
import java.net.ServerSocket

fun getFreePort(): Int {
    val range = Configuration.startPort..Configuration.endPort

    for (i in range) {
        if (available(i)) return i
    }

    throw HttpException("No ports available in range $range")
}

private fun available(port: Int): Boolean {
    var ss: ServerSocket? = null
    var ds: DatagramSocket? = null
    try {
        ss = ServerSocket(port)
        ss.reuseAddress = true
        ds = DatagramSocket(port)
        ds.reuseAddress = true
        return true
    } catch (_: IOException) {
    } finally {
        try {
            ds?.close()
            ss?.close()
        } catch (_: IOException) {
            // No-op.
        }
    }

    return false
}
