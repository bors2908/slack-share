package me.bors.slack.share.extension.listener

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import me.bors.slack.share.service.InitializationService

class PostStartListener : StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        service<InitializationService>()
    }
}
