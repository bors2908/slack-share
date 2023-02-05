package me.bors.slack.share

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import me.bors.slack.share.service.ConversationsService
import me.bors.slack.share.service.WorkspaceService
import java.io.File
import java.util.Properties

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class SlackShareTestBase : BasePlatformTestCase() {
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

    fun createTestFileAndCheck(file: File, fileAction: (File) -> Unit) {
        try {
            file.createNewFile()

            file.writeText("Sample Text")

            fileAction.invoke(file)
        } finally {
            file.delete()
        }
    }

    private fun loadProperties(): Properties {
        val configuration = Properties()

        val inputStream = SlackShareTestBase::class.java
            .classLoader
            .getResourceAsStream("secrets/test-tokens.properties")

        configuration.load(inputStream)

        inputStream?.close()

        return configuration
    }
}
