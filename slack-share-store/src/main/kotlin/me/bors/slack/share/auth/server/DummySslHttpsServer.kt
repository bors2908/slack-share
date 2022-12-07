package me.bors.slack.share.auth.server

import com.intellij.openapi.diagnostic.Logger
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpsConfigurator
import com.sun.net.httpserver.HttpsParameters
import com.sun.net.httpserver.HttpsServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLEngine
import javax.net.ssl.SSLParameters

private val logger: Logger = Logger.getInstance(DummySslHttpsServer::class.java)

class DummySslHttpsServer(url: String, path: String, port: Int, requestHandler: HttpHandler) : AutoCloseable {
    private val httpsServer = HttpsServer.create(InetSocketAddress(port), 0)

    init {
        val localhostCertificate = HeldCertificate.Builder()
            .addSubjectAlternativeName(url)
            .duration(10 * 365, TimeUnit.DAYS)
            .build()

        val serverCertificates = HandshakeCertificates.Builder()
            .heldCertificate(localhostCertificate)
            .build()

        val sslContext = serverCertificates.sslContext()

        httpsServer.httpsConfigurator = object : HttpsConfigurator(sslContext) {
            override fun configure(params: HttpsParameters) {
                try {
                    val sslEngine: SSLEngine = sslContext.createSSLEngine()
                    params.needClientAuth = false
                    params.cipherSuites = sslEngine.enabledCipherSuites
                    params.protocols = sslEngine.enabledProtocols

                    val sslParameters: SSLParameters = sslContext.supportedSSLParameters
                    params.setSSLParameters(sslParameters)
                    logger.info("The HTTPS server is created.")
                } catch (ex: Exception) {
                    logger.error("Failed to create the HTTPS port", ex)
                }
            }
        }

        httpsServer.createContext(path, requestHandler)
        httpsServer.executor = null
    }

    fun start() {
        httpsServer.start()

        logger.info("The HTTPS server was started.")
    }

    override fun close() {
        httpsServer.stop(0)
    }
}
