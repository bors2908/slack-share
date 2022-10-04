package me.bors.slack.share.auth.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollBar
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.util.ui.FormBuilder
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.net.URI
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea

class AuthenticationDialogWrapper : DialogWrapper(true) {
    private var sliderTextField: JBTextField? = null

    init {
        title = "Slack Auth"

        init()
    }

    fun setUri(uri: URI) {
        if (sliderTextField != null) {
            sliderTextField!!.text = uri.toString()
        }
    }

    override fun createCenterPanel(): JComponent {
        setCancelButtonText("Cancel Auth")
        isOKActionEnabled = false

        val panel = JPanel()
        val area = JTextArea(
            "You were redirected to your default system browser to continue authentication process." +
                    "${System.lineSeparator()}If browser page was not opened automatically, copy URL below and " +
                    "paste it to your preferred browser:"
        )

        area.font = panel.font
        area.background = panel.background
        area.preferredSize = Dimension(700, 35)

        val eLabel = JLabel("If you've encountered problems with client_id or secret on Slack side, try to use Reload Caches option.")
        val label = JLabel("Click OK if authentication process is finished or click Cancel Auth if you want to stop it.")

        val resultingPanel = FormBuilder.createFormBuilder()
            .addComponent(area)
            .addComponent(getTextSlider())
            .addComponent(eLabel)
            .addComponent(label)
            .addComponentFillVertically(panel, 0)
            .panel

        resultingPanel.preferredSize = Dimension(700, 100)

        return resultingPanel
    }

    private fun getTextSlider(): JComponent {
        val textField = JBTextField()

        sliderTextField = textField

        textField.preferredSize = Dimension(650, 30)
        textField.caretPosition = 0
        textField.isEditable = false

        val scrollBar = JBScrollBar(JBScrollBar.HORIZONTAL)

        scrollBar.preferredSize = Dimension(650, 3)

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        val brm = textField.horizontalVisibility
        scrollBar.model = brm
        panel.add(textField)
        panel.add(scrollBar)

        panel.preferredSize = Dimension(650, 35)

        val copyButton = JButton("Copy")
        copyButton.preferredSize = Dimension(75, 35)

        val linkPanel = JPanel()

        linkPanel.layout = HorizontalLayout(5)
        linkPanel.add(panel, HorizontalLayout.LEFT)
        linkPanel.add(copyButton, HorizontalLayout.LEFT)

        copyButton.addActionListener {
            val stringSelection = StringSelection(textField.text)
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(stringSelection, null)
            copyButton.text = "Copied"
        }

        return linkPanel
    }
}



