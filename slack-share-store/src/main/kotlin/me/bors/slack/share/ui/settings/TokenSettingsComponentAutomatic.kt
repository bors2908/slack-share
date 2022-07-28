package me.bors.slack.share.ui.settings

import com.intellij.ui.components.panels.HorizontalLayout
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class TokenSettingsComponentAutomatic(
    manualAction: ActionListener,
    automaticAction: ActionListener,
    removeAction: ActionListener
) : TokenSettingsComponent(manualAction, removeAction) {
    override lateinit var panel: JPanel

    private val automaticSetButton = JButton("Add automatically")

    override val preferredFocusedComponent: JComponent
        get() = automaticSetButton

    init {
        buttonJPanel.layout = HorizontalLayout(5)
        buttonJPanel.add(automaticSetButton, HorizontalLayout.LEFT)
        buttonJPanel.add(manualSetButton, HorizontalLayout.LEFT)
        buttonJPanel.add(removeTokenButton, HorizontalLayout.LEFT)
        automaticSetButton.addActionListener(automaticAction)
    }

    override fun extraButtonActions(opName: String) {
        automaticSetButton.text = "$opName automatically"
    }
}
