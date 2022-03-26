package me.bors.slack.share

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JPanel

class ShareDialogWrapper(
    private val content: String
) : DialogWrapper(true) {
    private val slackClient = SlackClient()

    init {
        title = "Share to Slack"

        init()
    }

    override fun createCenterPanel(): JComponent? {
        val dialogPanel = JPanel(BorderLayout())

        val channels = slackClient.receiveChannels()

        val comboBox: ComboBox<Pair<String, String>> = ComboBox(DefaultComboBoxModel(channels.toTypedArray()))

        comboBox.preferredSize = Dimension(300, 30)

        val editorPane = JEditorPane("text", content)

        editorPane.preferredSize = Dimension(300, 100)

        dialogPanel.add(comboBox, BorderLayout.CENTER)
        dialogPanel.add(editorPane, BorderLayout.AFTER_LAST_LINE)

        dialogPanel.minimumSize = Dimension(300, 150)
        dialogPanel.maximumSize = Dimension(1000, 500)

        return dialogPanel
    }
}