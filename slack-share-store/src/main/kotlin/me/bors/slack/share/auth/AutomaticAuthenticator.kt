package me.bors.slack.share.auth

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.TimeoutUtil.sleep
import com.slack.api.Slack
import com.slack.api.methods.request.oauth.OAuthV2AccessRequest
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpsExchange
import io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR
import io.netty.handler.codec.http.HttpResponseStatus.OK
import me.bors.slack.share.auth.Authenticator.Companion.SCOPE_LIST
import me.bors.slack.share.auth.server.DummySslHttpsServer
import me.bors.slack.share.auth.server.getFreePort
import me.bors.slack.share.error.AuthenticationException
import me.bors.slack.share.persistence.ShareClientId
import me.bors.slack.share.persistence.SlackShareBasicSecret
import me.bors.slack.share.ui.dialog.AddTokenAutomaticDialogWrapper
import me.bors.slack.share.ui.dialog.AuthenticationDialogWrapper
import okhttp3.HttpUrl
import java.io.OutputStream
import java.net.URI
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Exchanger
import java.util.concurrent.Executors
import java.util.concurrent.Future

private val slack = Slack.getInstance()

private val logger: Logger = Logger.getInstance(AutomaticAuthenticator::class.java)

private const val schema = "https://"
private const val ip = "127.0.0.1"
private const val innerPath = "/slack-share/auth"

@Suppress("MaxLineLength", "UnusedPrivateMember")
object AutomaticAuthenticator : Authenticator, AutoCloseable {
    private lateinit var server: DummySslHttpsServer

    private lateinit var state: String

    private var authJob: Future<String?>? = null

    private var finishResponseLatch: CountDownLatch? = null

    private val codeExchanger = Exchanger<String>()
    private val resultExchanger = Exchanger<AuthResult>()

    fun authAutomatically(): String? {
        val wrapper = AddTokenAutomaticDialogWrapper()

        if (!wrapper.showAndGet()) {
            return null
        }

        return requestTokenFromSlack()
    }

    private fun cancelAuth() {
        if (authJob != null) {
            logger.info("Cancelling auth.")

            authJob!!.cancel(true)

            resultExchanger.exchange(
                AuthResult(
                    false,
                    "Authentication process was manually cancelled."
                )
            )
        }
    }

    @Suppress("LongMethod")
    private fun requestTokenFromSlack(): String? {
        val clientId = ShareClientId.get()!!
        val clientSecret = SlackShareBasicSecret.get()

        val port = getFreePort()

        val redirectUrl = "${schema}${ip}:$port${innerPath}"

        val scopes = SCOPE_LIST.joinToString(" ")

        state = UUID.randomUUID().toString()

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

        val pool = Executors.newFixedThreadPool(1)

        val authenticationDialogWrapper = AuthenticationDialogWrapper()

        authenticationDialogWrapper.setUri(uri)

        authJob = pool.submit<String?> {
            try {
                logger.info("Starting dummy HTTPS server on $redirectUrl")

                startServer(port)

                logger.info("Opening browser on $uri.")

                BrowserUtil.browse(uri)

                val code = codeExchanger.exchange(null)

                logger.info("Code received from HTTPS server.")

                val oauthRequest = OAuthV2AccessRequest.builder()
                    .redirectUri(redirectUrl)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .code(code)
                    .build()

                val response = slack.methods().oauthV2Access(oauthRequest)
                    .authedUser

                SCOPE_LIST.forEach()
                {
                    if (!response.scope.contains(it))
                        throw AuthenticationException(
                            "Scopes does not match. [expected=$SCOPE_LIST], [provided=${response.scope}]"
                        )
                }

                logger.info("Auth Success.")

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
            logger.info("Handling HTTPS request ${exchange.requestURI}.")

            finishResponseLatch = CountDownLatch(1)

            try {
                exchange as HttpsExchange

                val queryParams = exchange.requestURI.queryParams()

                val receivedState = queryParams["state"]

                checkState(receivedState)

                val code = queryParams["code"]

                if (code != null) codeExchanger.exchange(code)

            } catch (e: Exception) {
                cancelAuth()

                logger.error("Authentication process cancelled. Https server has encountered an error: $e.")

                finishResponseLatch!!.countDown()
            }

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
        }
    }
}

private fun URI.queryParams(): Map<String, String> {
    if (this.query.isNullOrBlank()) return emptyMap()

    return this.query
        .split("&")
        .mapNotNull {
            val entry = it.split(delimiters = arrayOf("="), limit = 2)

            if (entry.size != 2) {
                return@mapNotNull null
            }

            entry[0] to entry[1]
        }
        .toMap()
}

data class AuthResult(
    val success: Boolean,
    val extraMessage: String? = null,
    val error: Throwable? = null
)
