package me.bors.slack.share.persistence

import com.intellij.openapi.components.*

@Service
@State(name = "SlackSharePersistentState", storages = [Storage("SlackShare.xml")])
class PersistentState : PersistentStateComponent<SlackState> {
    companion object {
        val instance: PersistentState
            get() = service()
    }

    var myState = SlackState()

    override fun getState(): SlackState {
        return myState
    }

    override fun loadState(stateLoadedFromPersistence: SlackState) {
        myState = stateLoadedFromPersistence
    }
}


