package me.bors.slack.share.client

import com.intellij.openapi.diagnostic.Logger
import com.slack.api.Slack
import com.slack.api.methods.SlackApiTextResponse
import me.bors.slack.share.error.SlackClientException

open class SlackClientBase {
    protected val slack: Slack = Slack.getInstance()

    // Unfortunately Slack Java API paginated request has no extracted interface with cursor and limit fields.
    protected inline fun <reified T> processPaginatedRequest(
        processRequest: (String, Int) -> Pair<String, List<T>>,
    ): MutableList<T> {
        val limit = PAGE_SIZE

        val accumulator = mutableListOf<T>()

        var cursor = ""

        do {
            val pair = processRequest.invoke(cursor, limit)

            cursor = pair.first

            accumulator.addAll(pair.second)
        } while (cursor.isNotEmpty())

        return accumulator
    }

    protected inline fun <reified T : SlackApiTextResponse> T.processErrors(): T {
        if (this.warning != null) {
            logger.warn("Warning received from Slack: ${this.warning}")
        }

        if (this.error != null) {
            val needed = if (this.needed != null) "Needed: ${this.needed}" else ""
            val provided = if (this.provided != null) "Provided: ${this.provided}" else ""


            throw SlackClientException(
                "Error occurred, during Slack request execution: " +
                    "${this.error} ${System.lineSeparator()} $needed ${System.lineSeparator()} $provided"
            )
        }

        return this
    }

    companion object {
        const val PAGE_SIZE: Int = 200

        val logger: Logger = Logger.getInstance(SlackClientBase::class.java)
    }
}
