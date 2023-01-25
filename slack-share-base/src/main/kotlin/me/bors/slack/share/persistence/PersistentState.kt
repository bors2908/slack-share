package me.bors.slack.share.persistence

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.util.xmlb.XmlSerializerUtil

abstract class PersistentState<T : Any> : PersistentStateComponent<PersistentState<T>> {
    private var value: T? = null

    fun getValue(): T? = value

    fun setValue(value: T?) {
        this.value = value
    }

    override fun getState(): PersistentState<T> {
        return this
    }

    override fun loadState(state: PersistentState<T>) {
        XmlSerializerUtil.copyBean(state, this)
    }
}

