package me.bors.slack.share.ui.settings

import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.ui.components.panels.HorizontalLayout.LEFT
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class TokenSettingsComponentAutomatic(
    manualAction: ActionListener,
    automaticAction: ActionListener,
    removeAction: ActionListener,
    reloadCachesAction: ActionListener
) : TokenSettingsComponent(manualAction, removeAction) {
    override lateinit var panel: JPanel

    private val automaticSetButton = JButton("Add automatically")
    private val reloadCachesButton = JButton("Reload caches")

    override val preferredFocusedComponent: JComponent
        get() = automaticSetButton

    init {
        buttonJPanel.layout = HorizontalLayout(5)
        buttonJPanel.add(automaticSetButton, LEFT)
        buttonJPanel.add(manualSetButton, LEFT)
        buttonJPanel.add(removeTokenButton, LEFT)
        buttonJPanel.add(reloadCachesButton, LEFT)

        automaticSetButton.addActionListener(automaticAction)
        reloadCachesButton.addActionListener(reloadCachesAction)
    }

    override fun extraButtonActions(opName: String) {
        automaticSetButton.text = "$opName automatically"
    }
}
