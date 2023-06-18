package me.bors.slack.share.ui.dialog.error

import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class ErrorDialogWrapper(private val error: String) : DialogWrapper(true) {
    init {
        title = "Error"

        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())

        val label = JLabel(error)

        label.preferredSize = Dimension(100, 30)

        dialogPanel.add(label, BorderLayout.CENTER)

        return dialogPanel
    }
}
