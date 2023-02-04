package me.bors.slack.share.persistence

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@Service
@State(name = "SlackSharePersistentState", storages = [Storage("SlackShare.xml")])
class PersistentState : PersistentStateComponent<SlackPersistentState> {
    companion object {
        val instance: PersistentState
            get() = service()
    }

    var myState: SlackPersistentState = SlackPersistentState()

    override fun getState(): SlackPersistentState {
        return myState
    }

    override fun loadState(stateLoadedFromPersistence: SlackPersistentState) {
        myState = stateLoadedFromPersistence
    }
}
