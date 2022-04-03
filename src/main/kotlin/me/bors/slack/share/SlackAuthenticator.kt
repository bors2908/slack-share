package me.bors.slack.share

import java.net.ServerSocket
import java.util.concurrent.TimeUnit
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate

private val clientId = ""

class SlackAuthenticator {
    fun auth() {
        val port = 59861//getFreePort()

        val server = startServer(port)

        val url = HttpUrl.Builder()
            .scheme("https")
            .host("slack.com")
            .addPathSegment("openid")
            .addPathSegment("connect")
            .addPathSegment("authorize")
            .addQueryParameter("response_type", "code")
            .addQueryParameter("scope", "openid channels:read chat:write files:write groups:read im:read mpim:read users:read")
            .addQueryParameter("client_id", clientId)
            .addQueryParameter("state", "whatever")
            .addQueryParameter("nonce", "whatever")
            .addQueryParameter("redirect_uri", "https://127.0.0.1:$port/")
            .build()

        val request = Request.Builder()
            .get()
            .url(url)
            .build()

        val okClient = OkHttpClient()

        val response = okClient.newCall(request).execute()

        println(response)

        val incomingRequest = server.takeRequest()

        val code = incomingRequest.requestUrl?.queryParameter("code")

        Thread.sleep(10000)

        server.close()

        server.shutdown()


    }

    fun startServer(port: Int): MockWebServer {
        val server = MockWebServer()

        val localhost = "127.0.0.1"
        val localhostCertificate = HeldCertificate.Builder()
            .addSubjectAlternativeName(localhost)
            .duration(10 * 365, TimeUnit.DAYS)
            .build()

        val serverCertificates = HandshakeCertificates.Builder()
            .heldCertificate(localhostCertificate)
            .build()

        server.useHttps(serverCertificates.sslSocketFactory(), false)

        server.start(port)

        return server
    }

        fun getFreePort(): Int {
        ServerSocket(0).use { socket -> return socket.localPort }
    }
}

fun main() {
    val auth = SlackAuthenticator()
    auth.auth()
}