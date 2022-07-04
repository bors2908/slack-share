package me.bors.slack.share.ui.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.util.ui.FormBuilder
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class TokenSettingsComponent(manualAction: ActionListener, automaticAction: ActionListener, removeAction: ActionListener) {
    var panel: JPanel
    private val tokenStatusLabel = JBLabel("Token status: ")

    private val buttonJPanel = JPanel()

    private val manualSetButton = JButton("Add manually")
    private val automaticSetButton = JButton("Add automatically")
    private val removeTokenButton = JButton("Remove token")

    val preferredFocusedComponent: JComponent
        get() = automaticSetButton

    fun setStatus(exists: Boolean) {
        tokenStatusLabel.text = "Token status: " + if (exists) "\u2705 Token is present" else "\u26A0 No token"

        val opName = if (exists) "Renew" else "Add"

        manualSetButton.text = "$opName manually"
        automaticSetButton.text = "$opName automatically"
    }

    init {
        buttonJPanel.layout = HorizontalLayout(5)
        buttonJPanel.add(manualSetButton, HorizontalLayout.LEFT)
        buttonJPanel.add(automaticSetButton, HorizontalLayout.LEFT)
        buttonJPanel.add(removeTokenButton, HorizontalLayout.LEFT)

        manualSetButton.addActionListener(manualAction)
        automaticSetButton.addActionListener(automaticAction)
        removeTokenButton.addActionListener(removeAction)

        panel = FormBuilder.createFormBuilder()
            .addComponent(tokenStatusLabel)
            .addComponent(buttonJPanel)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
