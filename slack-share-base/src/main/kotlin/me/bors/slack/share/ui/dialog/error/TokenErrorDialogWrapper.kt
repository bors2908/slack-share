package me.bors.slack.share.ui.dialog.error

import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class TokenErrorDialogWrapper(private val error: String) : DialogWrapper(true) {
    init {
        title = "Invalid Token"

        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())

        val label = JLabel("User token error: $error")

        label.preferredSize = Dimension(100, 30)

        dialogPanel.add(label, BorderLayout.CENTER)

        return dialogPanel
    }
}
