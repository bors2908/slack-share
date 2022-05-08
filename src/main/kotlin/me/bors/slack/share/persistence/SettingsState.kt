package me.bors.slack.share.persistence

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "me.bors.slack.share.ui.settings.TokenSettingsState", storages = [Storage("SlackSharePlugin.xml")])
object SettingsState : PersistentStateComponent<SettingsState> {
    var addTokenManually = true

    override fun getState(): SettingsState {
        return this
    }

    override fun loadState(state: SettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
