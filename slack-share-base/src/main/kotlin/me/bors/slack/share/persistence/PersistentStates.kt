package me.bors.slack.share.persistence

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "me.bors.slack.share.persistence.WorkspacePersistenceState", storages = [Storage("SlackSharePlugin.xml")])
object WorkspacePersistenceState : PersistentState<List<Int>>()
