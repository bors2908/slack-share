package me.bors.slack.share

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse
import com.slack.api.model.Message
import me.bors.slack.share.service.ConversationsService
import me.bors.slack.share.service.WorkspaceService
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class SlackShareTestBase : BasePlatformTestCase() {
    protected lateinit var conversationsService: ConversationsService

    protected lateinit var workspaceService: WorkspaceService

    protected lateinit var testClient: TestClient

    private val properties: Properties = loadProperties()

    private val tokens: List<String> = properties.getProperty("test.tokens").split(",")

    override fun setUp() {
        super.setUp()

        conversationsService = service()
        workspaceService = service()
        testClient = TestClient()

        tokens.forEach { workspaceService.addToken(it) }
    }

    fun createTestFileAndCheck(file: File, fileContent: String, fileAction: (File) -> Unit) {
        try {
            file.createNewFile()

            file.writeText(fileContent)

            fileAction.invoke(file)
        } finally {
            file.delete()
        }
    }

    protected fun ConversationsHistoryResponse?.getLastMessage(): Message =
        this?.messages?.first() ?: throw AssertionError("No last message")

    private fun loadProperties(): Properties {
        val configuration = Properties()

        val classResource: URL = SlackShareTestBase::class.java.classLoader.getResource(".")
            ?: throw AssertionError("Null class resource.")

        //TODO Rework path accessing
        val file = File(classResource.path + "/../../../../../secrets/test-tokens.properties")

        if (!file.canRead()) throw AssertionError("Can't read test token file.")

        val inputStream = Files.newInputStream(file.toPath())

        configuration.load(inputStream)

        inputStream.close()

        return configuration
    }
}
