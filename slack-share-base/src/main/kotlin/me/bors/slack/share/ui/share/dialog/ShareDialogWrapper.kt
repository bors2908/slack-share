package me.bors.slack.share.ui.share.dialog

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.UIUtil
import me.bors.slack.share.entity.Conversation
import me.bors.slack.share.entity.FileExclusion
import java.awt.BorderLayout
import java.awt.Dimension
import java.lang.Boolean.TRUE
import javax.swing.BorderFactory
import javax.swing.DefaultComboBoxModel
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.ScrollPaneConstants
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

class ShareDialogWrapper(
    private val conversations: List<Conversation>,
    private val text: String = "",
    private val filenames: List<String> = emptyList(),
    private val fileExclusions: List<FileExclusion> = emptyList()
) : DialogWrapper(true) {
    private lateinit var comboBox: ComboBox<Conversation>
    private lateinit var editorPane: JEditorPane
    private var quoteCheckBox: JCheckBox? = null

    init {
        title = "Share to Slack"

        init()
    }

    fun getEditedText(): String {
        return editorPane.text
    }

    fun getSelectedItem(): Conversation {
        return comboBox.selectedItem as Conversation
    }

    fun isQuotedCode(): Boolean {
        return quoteCheckBox?.isSelected ?: false
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())

        comboBox = ComboBox(DefaultComboBoxModel(conversations.toTypedArray()))
        comboBox.toolTipText = "Select message destination"

        editorPane = JEditorPane("text", text)
        editorPane.minimumSize = Dimension(400, 100)
        editorPane.background = UIUtil.getTextFieldBackground()

        editorPane.toolTipText = "Message editor"

        val scrollPane = JBScrollPane(
            editorPane,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        )

        scrollPane.border = BorderFactory.createEmptyBorder()

        dialogPanel.add(scrollPane, BorderLayout.CENTER)
        dialogPanel.add(comboBox, BorderLayout.BEFORE_FIRST_LINE)

        val s = System.lineSeparator()

        val attachments = if (filenames.isNotEmpty()) {
            filenames.joinToString(
                s,
                "\uD83D\uDCCE Attachments: $s",
                ""
            )
        } else ""

        val conditionalSeparator = if (attachments.isNotEmpty()) "$s$s" else ""

        val exclusionText = if (fileExclusions.isNotEmpty()) {
            "$conditionalSeparator\u274C Exclusions (Files that cannot be attached): $s " +
                    fileExclusions.joinToString(s)
        } else ""

        if (attachments.isNotEmpty() || exclusionText.isNotEmpty()) {
            val document = DefaultStyledDocument()

            val attributes = SimpleAttributeSet()
            attributes.addAttribute(StyleConstants.CharacterConstants.Italic, TRUE)

            document.insertString(document.length, attachments + exclusionText, attributes)

            val attachmentsPane = JTextPane(document)

            attachmentsPane.isEditable = false
            attachmentsPane.toolTipText = "Attached files"

            dialogPanel.add(attachmentsPane, BorderLayout.AFTER_LAST_LINE)
        } else {
            quoteCheckBox = JCheckBox("Code quote", true)

            dialogPanel.add(quoteCheckBox!!, BorderLayout.AFTER_LAST_LINE)
        }

        dialogPanel.minimumSize = Dimension(400, 200)
        dialogPanel.maximumSize = Dimension(1500, 600)

        return dialogPanel
    }
}
