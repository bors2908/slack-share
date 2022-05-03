package me.bors.slack.share

import com.slack.api.Slack
import com.slack.api.methods.request.oauth.OAuthV2AccessRequest
import java.awt.Desktop
import java.io.IOException
import java.net.DatagramSocket
import java.net.ServerSocket
import java.net.URI
import java.util.concurrent.TimeUnit
import me.bors.slack.share.Utils.getProperties
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate

private val props = getProperties("auth.properties")

private val slack = Slack.getInstance()

@Suppress("MaxLineLength", "UnusedPrivateMember")
class SlackAuthenticator {
    fun auth() {
        val clientId = props.getProperty("client.id")
        // TODO Totally unsafe. Remove
        val clientSecret = props.getProperty("client.secret")

        val schema = "https://"

        val ip = "127.0.0.1"

        val port = getFreePort()

        val path = "/slack-share/auth"

        val redirectUrl = "$schema$ip:$port$path"

        val server = startHttpsServer(ip, port)

        val scopes = listOf(
            "channels:read",
            "chat:write",
            "files:write",
            "groups:read",
            "im:read",
            "mpim:read",
            "users:read"
        ).joinToString(" ")

        val uri = HttpUrl.Builder()
            .scheme("https")
            .host("slack.com")
            .addPathSegment("oauth")
            .addPathSegment("v2")
            .addPathSegment("authorize")
            .addQueryParameter("user_scope", scopes)
            .addQueryParameter("client_id", clientId)
            //.addQueryParameter("state", "whatever")
            //.addQueryParameter("nonce", "whatever")
            .addQueryParameter("redirect_uri", redirectUrl)
            .build()
            .toUrl()
            .toURI()

        openInBrowser(uri)

        val incomingRequest = server.takeRequest(45, TimeUnit.SECONDS)

        val code = incomingRequest?.requestUrl?.queryParameter("code")

        server.shutdown()

        server.close()

        val oauthRequest = OAuthV2AccessRequest.builder()
            .redirectUri(redirectUrl)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .code(code)
            .build()

        val res = slack.methods().oauthV2Access(oauthRequest).authedUser

        println(res)
    }

    private fun openInBrowser(uri: URI) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(uri);
        }
    }

    private fun startHttpsServer(url: String, port: Int): MockWebServer {
        val server = MockWebServer()

        val localhostCertificate = HeldCertificate.Builder()
            .addSubjectAlternativeName(url)
            .duration(10 * 365, TimeUnit.DAYS)
            .build()

        val serverCertificates = HandshakeCertificates.Builder()
            .heldCertificate(localhostCertificate)
            .build()

        server.useHttps(serverCertificates.sslSocketFactory(), false)

        server.start(port)

        return server
    }

    private fun getFreePort(): Int {
        val range = props.getProperty("port.pool.start").toInt()..props.getProperty("port.pool.end").toInt()

        for (i in range) {
            if (available(i)) return i
        }

        throw IllegalStateException("No ports available in range $range")
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
}
