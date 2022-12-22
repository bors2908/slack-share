package me.bors.slack.share.ui.component

import com.intellij.ui.components.JBScrollBar
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.panels.HorizontalLayout
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.net.URI
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel

class UriTextSlider : JPanel() {
    private val urlField = JBTextField()

    init {
        urlField.preferredSize = Dimension(650, 30)
        urlField.caretPosition = 0
        urlField.isEditable = false

        val scrollBar = JBScrollBar(JBScrollBar.HORIZONTAL)

        scrollBar.preferredSize = Dimension(650, 3)

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        val brm = urlField.horizontalVisibility
        scrollBar.model = brm
        panel.add(urlField)
        panel.add(scrollBar)

        panel.preferredSize = Dimension(650, 35)

        val copyButton = JButton("Copy")
        copyButton.preferredSize = Dimension(75, 35)

        this.layout = HorizontalLayout(5)
        this.add(panel, HorizontalLayout.LEFT)
        this.add(copyButton, HorizontalLayout.LEFT)

        copyButton.addActionListener {
            val stringSelection = StringSelection(urlField.text)
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(stringSelection, null)
            copyButton.text = "Copied"
        }
    }

    fun setUri(uri: URI) {
        urlField.text = uri.toString()
        urlField.caretPosition = 0
    }
}
