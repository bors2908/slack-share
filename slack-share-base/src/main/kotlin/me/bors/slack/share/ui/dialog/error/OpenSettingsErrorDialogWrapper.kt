package me.bors.slack.share.ui.dialog.error

import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class OpenSettingsErrorDialogWrapper(private val error: String, title: String) : DialogWrapper(true) {
    init {
        this.title = title

        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())

        val label = JLabel("Error: $error. Press OK to open settings.")

        label.preferredSize = Dimension(100, 30)

        dialogPanel.add(label, BorderLayout.CENTER)

        return dialogPanel
    }
}
