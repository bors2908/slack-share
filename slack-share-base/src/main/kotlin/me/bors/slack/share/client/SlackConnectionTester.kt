package me.bors.slack.share.client

import com.slack.api.methods.request.api.ApiTestRequest
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

object SlackConnectionTester : SlackClientBase() {
    private val ONE_SECOND: Long = Duration.of(1, ChronoUnit.SECONDS).toMillis()

    @OptIn(DelicateCoroutinesApi::class)
    @Suppress("SwallowedException")
    fun isSlackAccessible(): Boolean {
        var accessible = false

        runBlocking {
            withTimeoutOrNull(ONE_SECOND) {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        accessible = slack.methods()
                            .apiTest(ApiTestRequest.builder().build())
                            .isOk
                    } catch (_: UnknownHostException) {
                        // No-op.
                    } catch (_: SocketTimeoutException) {
                        // No-op.
                    }
                }.join()
            }
        }

        return accessible
    }
}
