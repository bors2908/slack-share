package me.bors.slack.share.ui.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.HorizontalLayout
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
    private val tokenStatusLabel = JBLabel("Token status: ")

    private val manualSetButton = JButton("Add manually")
    private val removeTokenButton = JButton("Remove token")

    protected val buttonJPanel = JPanel()

    val preferredFocusedComponent: JComponent
        get() = manualSetButton

    init {
        setPanel(manualAction, removeAction)
    }

    abstract fun getExtraButton(): JButton?
    abstract fun extraButtonActions(opName: String)

    fun setStatus(exists: Boolean) {
        tokenStatusLabel.text = "Token status: " + if (exists) "\u2705 Token is present" else "\u26A0 No token"

        val opName = if (exists) "Renew" else "Add"

        manualSetButton.text = "$opName manually"
        extraButtonActions(opName)
    }

    private fun setPanel(manualAction: ActionListener, removeAction: ActionListener) {
        buttonJPanel.layout = HorizontalLayout(5)
        val extraButton = getExtraButton()
        if (extraButton != null) buttonJPanel.add(extraButton, HorizontalLayout.LEFT)
        buttonJPanel.add(manualSetButton, HorizontalLayout.LEFT)
        buttonJPanel.add(removeTokenButton, HorizontalLayout.LEFT)

        manualSetButton.addActionListener(manualAction)
        removeTokenButton.addActionListener(removeAction)

        panel = FormBuilder.createFormBuilder()
            .addComponent(tokenStatusLabel)
            .addComponent(buttonJPanel)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
