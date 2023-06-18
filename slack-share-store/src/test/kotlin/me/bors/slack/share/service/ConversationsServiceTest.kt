package me.bors.slack.share.service

import me.bors.slack.share.SlackShareTestBase

class ConversationsServiceTest : SlackShareTestBase() {
    fun testConversationsLoading() {
        val workspace = workspaceService.getAvailableWorkspaces().first()

        conversationsService.forceRefresh()

        conversationsService.refresh()

        assertTrue(conversationsService.getConversations(workspace).isNotEmpty())
    }
}
