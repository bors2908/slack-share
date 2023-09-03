package me.bors.slack.share.service

import me.bors.slack.share.SlackShareTestBase
import me.bors.slack.share.entity.Workspace

class WorkspaceServiceTest : SlackShareTestBase() {
    private var previousWorkspaces: List<Workspace> = emptyList()

    override fun setUp() {
        super.setUp()

        previousWorkspaces = workspaceService.getAvailableWorkspaces()
    }

    override fun tearDown() {
        workspaceService.removeAllTokens()

        previousWorkspaces.forEach { workspaceService.addToken(it.state.get()!!) }

        workspaceService.persist()

        super.tearDown()
    }

    fun testMoveUpDown() {
        val workspaces = workspaceService.getAvailableWorkspaces()

        val moving = workspaces.first()

        val indexBefore = workspaceService.getAvailableWorkspaces().indexOf(moving)

        workspaceService.moveDown(moving)

        val indexAfter = workspaceService.getAvailableWorkspaces().indexOf(moving)

        workspaceService.moveUp(moving)

        val finalIndex = workspaceService.getAvailableWorkspaces().indexOf(moving)

        assertEquals(indexBefore + 1, indexAfter)
        assertEquals(indexBefore, finalIndex)
    }

    fun testMovingAbuse() {
        val workspaces = workspaceService.getAvailableWorkspaces()

        val moving = workspaces.first()

        (0..20).forEach { _ -> workspaceService.moveDown(moving) }

        (0..20).forEach { _ -> workspaceService.moveUp(moving) }
    }

    fun testRemove() {
        val workspaces = workspaceService.getAvailableWorkspaces()

        val toDelete = workspaces.first()

        workspaceService.delete(toDelete)

        assertFalse(workspaceService.getAvailableWorkspaces().contains(toDelete))
    }

    fun testAdd() {
        val workspaces = workspaceService.getAvailableWorkspaces()

        val toAdd = workspaces.first()

        workspaceService.delete(toAdd)

        workspaceService.addToken(toAdd.state.get()!!)

        assertTrue(workspaceService.getAvailableWorkspaces().contains(toAdd))
    }
}
