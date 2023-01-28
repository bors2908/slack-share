package me.bors.slack.share.ui.settings

import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.components.panels.VerticalLayout.TOP
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class TokenSettingsComponentAutomatic(
    manualAction: ActionListener,
    automaticAction: ActionListener,
    removeAction: ActionListener,
    reloadCachesAction: ActionListener,
    moveUpAction: ActionListener,
    moveDownAction: ActionListener
) : TokenSettingsComponent(manualAction, removeAction, moveUpAction, moveDownAction) {
    override lateinit var panel: JPanel

    private val automaticSetButton = JButton("Add automatically")
    private val reloadCachesButton = JButton("Reload caches")

    override val preferredFocusedComponent: JComponent
        get() = automaticSetButton

    init {
        buttonJPanel.layout = VerticalLayout(5)
        buttonJPanel.add(JLabel("Slack Workspaces:"), TOP)
        buttonJPanel.add(automaticSetButton, TOP)
        buttonJPanel.add(manualSetButton, TOP)
        buttonJPanel.add(removeButton, TOP)
        buttonJPanel.add(moveUpButton, TOP)
        buttonJPanel.add(moveDownButton, TOP)
        buttonJPanel.add(reloadCachesButton, TOP)

        automaticSetButton.addActionListener(automaticAction)
        reloadCachesButton.addActionListener(reloadCachesAction)
    }

    override fun extraButtonActions(opName: String) {
        automaticSetButton.text = "$opName automatically"
    }
}
