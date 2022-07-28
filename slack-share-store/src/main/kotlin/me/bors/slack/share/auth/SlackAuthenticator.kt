package me.bors.slack.share.auth

import com.slack.api.Slack
import com.slack.api.methods.request.oauth.OAuthV2AccessRequest
import me.bors.slack.share.configuration
import me.bors.slack.share.persistence.SlackShareClientId
import me.bors.slack.share.persistence.SlackShareSecret
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import java.awt.Desktop
import java.io.IOException
import java.net.DatagramSocket
import java.net.ServerSocket
import java.net.URI
import java.util.concurrent.TimeUnit

private val scopeList = listOf(
    "channels:read",
    "chat:write",
    "files:write",
    "groups:read",
    "im:read",
    "mpim:read",
    "users:read"
)

private val slack = Slack.getInstance()

//TODO Make a service
@Suppress("MaxLineLength", "UnusedPrivateMember")
class SlackAuthenticator {
    fun auth(): String {
        val clientId = SlackShareClientId.get()
        val clientSecret = SlackShareSecret.get()

        val schema = "https://"

        val ip = "127.0.0.1"

        val port = getFreePort()

        val path = "/slack-share/auth"

        val redirectUrl = "$schema$ip:$port$path"

        val server = startHttpsServer(ip, port)

        val scopes = scopeList.joinToString(" ")

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

        //TODO Add proper message afterwards
        server.enqueue(
            MockResponse()
                .setStatus("HTTP/1.1 200")
                .addHeader("content-type: text/html; charset=utf-8")
                .setBody(
                    "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<body>\n" +
                            "<h1>Please, Clode the Browser</h1>\n" +
                            "<p>Your Slack App was successfully authenticated.</p>\n" +
                            "</body>\n" +
                            "</html>"
                )
        )

        val incomingRequest =
            server.takeRequest(300, TimeUnit.SECONDS) ?: throw AuthenticationException("Auth timeout.")

        val requestUrl = incomingRequest.requestUrl ?: throw AuthenticationException("Request URL was null.")

        val code = requestUrl.queryParameter("code")

        server.shutdown()

        server.close()

        val oauthRequest = OAuthV2AccessRequest.builder()
            .redirectUri(redirectUrl)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .code(code)
            .build()

        val response = slack.methods().oauthV2Access(oauthRequest).authedUser

        scopeList.forEach {
            if (!response.scope.contains(it))
                throw AuthenticationException("Scopes does not match. [expected=$scopeList], [provided=${response.scope}]")
        }

        return response.accessToken ?: throw AuthenticationException("Null access token.")
    }

    private fun openInBrowser(uri: URI) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(uri)
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
        val range = configuration.startPort!!..configuration.endPort!!

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

class AuthenticationException(msg: String) : RuntimeException(msg)
