package me.bors.slack.share.ui.dialog.error

import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class SlackOfflineErrorDialogWrapper : DialogWrapper(true) {
    init {
        title = "Slack Offline"

        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())

        val label = JLabel("Slack URLs are unreachable. Check internet connection.")

        label.preferredSize = Dimension(100, 30)

        dialogPanel.add(label, BorderLayout.CENTER)

        return dialogPanel
    }
}
