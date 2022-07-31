package me.bors.slack.share.auth

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.TimeoutUtil.sleep
import com.intellij.util.queryParameters
import com.slack.api.Slack
import com.slack.api.methods.request.oauth.OAuthV2AccessRequest
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpsExchange
import io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR
import io.netty.handler.codec.http.HttpResponseStatus.OK
import me.bors.slack.share.auth.dialog.AuthenticationDialogWrapper
import me.bors.slack.share.auth.server.DummySslHttpsServer
import me.bors.slack.share.auth.server.getFreePort
import me.bors.slack.share.persistence.SlackShareClientId
import me.bors.slack.share.persistence.SlackShareSecret
import me.bors.slack.share.persistence.SlackUserTokenSecretState
import me.bors.slack.share.ui.settings.dialog.AddTokenAutomaticDialogWrapper
import okhttp3.HttpUrl
import java.io.OutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Exchanger
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.random.Random

private val slack = Slack.getInstance()

private val logger: Logger = Logger.getInstance(AutomaticAuthenticator::class.java)

private const val schema = "https://"
private const val ip = "127.0.0.1"
private const val innerPath = "/slack-share/auth"
private val scopeList = listOf(
    "channels:read",
    "chat:write",
    "files:write",
    "groups:read",
    "im:read",
    "mpim:read",
    "users:read"
)

@Suppress("MaxLineLength", "UnusedPrivateMember")
object AutomaticAuthenticator : Authenticator, AutoCloseable {
    private lateinit var server: DummySslHttpsServer

    private lateinit var redirectUrl: String

    private lateinit var state: String

    private var authJob: Future<String?>? = null

    private var finishResponseLatch: CountDownLatch? = null

    private val codeExchanger = Exchanger<String>()
    private val resultExchanger = Exchanger<AuthResult>()

    private val random = Random(31)

    fun authAutomatically() {
        val wrapper = AddTokenAutomaticDialogWrapper()

        if (!wrapper.showAndGet()) {
            return
        }

        val token = requestTokenFromSlack()

        if (token != null) SlackUserTokenSecretState.set(token)
    }

    private fun cancelAuth() {
        if (authJob != null) {
            authJob!!.cancel(true)

            resultExchanger.exchange(
                AuthResult(
                    false,
                    "Authentication process was manually cancelled."
                )
            )
        }
    }

    private fun requestTokenFromSlack(): String? {
        val clientId = SlackShareClientId.get()!!
        val clientSecret = SlackShareSecret.get()

        val authenticationDialogWrapper = AuthenticationDialogWrapper()

        val pool = Executors.newFixedThreadPool(1)

        authJob = pool.submit<String?> {
            try {
                val port = getFreePort()

                redirectUrl = "${schema}${ip}:$port${innerPath}"

                state = (random.nextInt() * 31).toString()

                startServer(port)

                val scopes = scopeList.joinToString(" ")

                val uri = HttpUrl.Builder()
                    .scheme("https")
                    .host("slack.com")
                    .addPathSegment("oauth")
                    .addPathSegment("v2")
                    .addPathSegment("authorize")
                    .addQueryParameter("user_scope", scopes)
                    .addQueryParameter("client_id", clientId)
                    .addQueryParameter("state", state)
                    .addQueryParameter("redirect_uri", redirectUrl)
                    .build()
                    .toUrl()
                    .toURI()

                authenticationDialogWrapper.setUri(uri)

                BrowserUtil.browse(uri)

                val code = codeExchanger.exchange(null)

                val oauthRequest = OAuthV2AccessRequest.builder()
                    .redirectUri(redirectUrl)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .code(code)
                    .build()

                val response = slack.methods().oauthV2Access(oauthRequest)
                    .authedUser

                scopeList.forEach()
                {
                    if (!response.scope.contains(it))
                        throw AuthenticationException("Scopes does not match. [expected=$scopeList], [provided=${response.scope}]")
                }

                resultExchanger.exchange(
                    AuthResult(true)
                )

                return@submit response.accessToken
            } catch (e: Exception) {
                resultExchanger.exchange(
                    AuthResult(success = false, error = e)
                )

                return@submit null
            } finally {
                sleep(1000)

                close()

                authenticationDialogWrapper.isOKActionEnabled = true
            }
        }

        if (!authenticationDialogWrapper.showAndGet()) {
            cancelAuth()

            return null
        }

        return (authJob as Future<String?>).get()
    }

    private fun startServer(port: Int) {
        server = DummySslHttpsServer(ip, innerPath, port, Handler())

        server.start()
    }


    override fun close() {
        if (finishResponseLatch != null) {
            finishResponseLatch!!.await()
        }

        server.close()
    }

    class Handler : HttpHandler {
        private fun getMessage(result: AuthResult): String {
            val header = if (result.success) {
                "Successful authentication."
            } else {
                "Error occurred during authentication."
            }

            return "<h1>${header}</h1>\n" +
                    "<p>${result.extraMessage ?: ""}</p>\n" +
                    "<p>${result.error?.message ?: ""}</p>\n" +
                    "<p>You can close your browser now.</p>\n"
        }

        private fun checkState(receivedState: String?) {
            if (receivedState == null || receivedState != state)
                throw AuthenticationException("Wrong auth state. May be a sign of a MiTM attack.")
        }

        override fun handle(exchange: HttpExchange) {
            finishResponseLatch = CountDownLatch(1)

            try {
                exchange as HttpsExchange

                val receivedState = exchange.requestURI.queryParameters["state"]

                checkState(receivedState)

                val code = exchange.requestURI.queryParameters["code"]

                if (code != null) codeExchanger.exchange(code)

                val result = resultExchanger.exchange(null)

                val message = getMessage(result)

                val response = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<body>\n" +
                        message +
                        "</body>\n" +
                        "</html>"

                exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
                exchange.responseHeaders.add("content-type", "text/html; charset=utf-8")

                exchange.sendResponseHeaders(
                    if (result.success) OK.code() else INTERNAL_SERVER_ERROR.code(),
                    response.toByteArray().size.toLong()
                )

                val outputStream: OutputStream = exchange.responseBody

                outputStream.write(response.toByteArray())

                outputStream.close()

                finishResponseLatch!!.countDown()
            } catch (e: Exception) {
                cancelAuth()

                logger.error("Authentication process cancelled. Https server has encountered an error: $e.")

                finishResponseLatch!!.countDown()
            }
        }
    }
}

data class AuthResult(
    val success: Boolean,
    val extraMessage: String? = null,
    val error: Throwable? = null
)

class AuthenticationException(msg: String) : RuntimeException(msg)
