package me.bors.slack.share

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import me.bors.slack.share.service.ConversationsService
import me.bors.slack.share.service.WorkspaceService
import java.util.*


abstract class SlackTestBase : BasePlatformTestCase() {
    protected lateinit var conversationsService: ConversationsService

    protected lateinit var workspaceService: WorkspaceService

    private val properties: Properties = loadProperties()

    private val tokens: List<String> = properties.getProperty("test.tokens").split(",")

    override fun setUp() {
        super.setUp()

        conversationsService = service()
        workspaceService = service()

        tokens.forEach { workspaceService.addToken(it) }
    }

    private fun loadProperties(): Properties {
        val configuration = Properties()

        val inputStream = SlackTestBase::class.java
            .classLoader
            .getResourceAsStream("secrets/test-tokens.properties")

        configuration.load(inputStream)

        inputStream?.close()

        return configuration
    }
}
