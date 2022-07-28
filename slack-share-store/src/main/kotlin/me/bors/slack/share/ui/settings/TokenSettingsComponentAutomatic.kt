package me.bors.slack.share.ui.settings

import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JPanel

class TokenSettingsComponentAutomatic(
    manualAction: ActionListener,
    automaticAction: ActionListener,
    removeAction: ActionListener
) : TokenSettingsComponent(manualAction, removeAction) {
    override lateinit var panel: JPanel

    private val automaticSetButton = JButton("Add automatically")

    override fun getExtraButton(): JButton {
        return automaticSetButton
    }

    override fun extraButtonActions(opName: String) {
        automaticSetButton.text = "$opName automatically"
    }

    init {
        automaticSetButton.addActionListener(automaticAction)
    }
}
