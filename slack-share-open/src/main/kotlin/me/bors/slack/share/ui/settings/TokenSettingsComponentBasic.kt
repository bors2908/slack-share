package me.bors.slack.share.ui.settings

import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JPanel

class TokenSettingsComponentBasic(
    manualAction: ActionListener,
    removeAction: ActionListener
) : TokenSettingsComponent(manualAction, removeAction) {
    override lateinit var panel: JPanel

    override fun getExtraButton(): JButton? {
        return null
    }

    override fun extraButtonActions(opName: String) {
        // No-op.
    }
}