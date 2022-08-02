package me.bors.slack.share.ui.settings

import com.intellij.ui.components.panels.HorizontalLayout
import java.awt.event.ActionListener
import javax.swing.JComponent
import javax.swing.JPanel

class TokenSettingsComponentBasic(
    manualAction: ActionListener,
    removeAction: ActionListener
) : TokenSettingsComponent(manualAction, removeAction) {
    override val preferredFocusedComponent: JComponent
        get() = manualSetButton

    override lateinit var panel: JPanel

    init {
        buttonJPanel.layout = HorizontalLayout(5)
        buttonJPanel.add(manualSetButton, HorizontalLayout.LEFT)
        buttonJPanel.add(removeTokenButton, HorizontalLayout.LEFT)
    }

    override fun extraButtonActions(opName: String) {
        // No-op.
    }
}