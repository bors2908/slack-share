package me.bors.slack.share.ui.settings

import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

abstract class TokenSettingsComponent(
    manualAction: ActionListener,
    removeAction: ActionListener
) {
    abstract var panel: JPanel
    abstract val preferredFocusedComponent: JComponent
    private val tokenStatusLabel = JBLabel("Token status: ")

    protected val manualSetButton = JButton("Add manually")
    protected val removeTokenButton = JButton("Remove token")

    protected val buttonJPanel = JPanel()

    init {
        setPanel(manualAction, removeAction)
    }

    abstract fun extraButtonActions(opName: String)

    fun setStatus(exists: Boolean) {
        tokenStatusLabel.text = "Token status: " + if (exists) "\u2705 Token is present" else "\u26A0 No token"

        val opName = if (exists) "Renew" else "Add"

        manualSetButton.text = "$opName manually"
        extraButtonActions(opName)
    }

    private fun setPanel(manualAction: ActionListener, removeAction: ActionListener) {
        manualSetButton.addActionListener(manualAction)
        removeTokenButton.addActionListener(removeAction)

        panel = FormBuilder.createFormBuilder()
            .addComponent(tokenStatusLabel)
            .addComponent(buttonJPanel)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
