package me.bors.slack.share

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import java.lang.Boolean.TRUE
import javax.swing.DefaultComboBoxModel
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

class ShareDialogWrapper(
    private val conversations: List<SlackConversation>,
    private val text: String = "",
    private val filenames: List<String> = emptyList(),
) : DialogWrapper(true) {
    private lateinit var comboBox: ComboBox<SlackConversation>
    private lateinit var editorPane: JEditorPane
    private var quoteCheckBox: JCheckBox? = null

    init {
        title = "Share to Slack"

        init()
    }

    fun getEditedText(): String {
        return editorPane.text
    }

    fun getSelectedItem(): SlackConversation {
        return comboBox.selectedItem as SlackConversation
    }

    fun isQuotedCode(): Boolean {
        return quoteCheckBox?.isSelected ?: false
    }

    //TODO Make editor pane border visible
    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())

        comboBox = ComboBox(DefaultComboBoxModel(conversations.toTypedArray()))
        comboBox.preferredSize = Dimension(300, 30)

        comboBox.selectedItem

        editorPane = JEditorPane("text", text)
        editorPane.minimumSize = Dimension(300, 50)
        editorPane.preferredSize = Dimension(300, 100)

        editorPane.text

        dialogPanel.add(editorPane, BorderLayout.CENTER)
        dialogPanel.add(comboBox, BorderLayout.BEFORE_FIRST_LINE)

        if (filenames.isNotEmpty()) {
            val attachments = filenames.joinToString(
                System.lineSeparator(),
                "\uD83D\uDCCE Attachments: ${System.lineSeparator()}",
                ""
            )

            val document = DefaultStyledDocument()

            val attributes = SimpleAttributeSet()
            attributes.addAttribute(StyleConstants.CharacterConstants.Italic, TRUE)

            document.insertString(document.length, attachments, attributes)

            val attachmentsPane = JTextPane(document)

            attachmentsPane.isEditable = false

            dialogPanel.add(attachmentsPane, BorderLayout.AFTER_LAST_LINE)
        } else if (quoteCheckBox != null) {
            quoteCheckBox = JCheckBox("Code quote", true)

            dialogPanel.add(quoteCheckBox!!, BorderLayout.AFTER_LAST_LINE)
        }


        dialogPanel.minimumSize = Dimension(300, 150)
        dialogPanel.maximumSize = Dimension(1500, 500)

        return dialogPanel
    }
}