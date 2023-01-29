package me.bors.slack.share

class ConversationTest : SlackTestBase() {
    fun test() {
        val times: MutableList<Long> = ArrayList(5)

        times.add(System.currentTimeMillis())

        conversationsService.forceRefresh()

        times.add(System.currentTimeMillis())

        conversationsService.refresh()

        times.add(System.currentTimeMillis())

        val refresh = times[1] - times[0]
        val refresh2 = times[2] - times[1]

        println(refresh)
        println(refresh2)
        println(workspaceService.getAvailableWorkspaces().flatMap { conversationsService.getConversations(it) }.size)
    }
}