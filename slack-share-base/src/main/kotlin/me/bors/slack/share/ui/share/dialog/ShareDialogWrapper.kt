package me.bors.slack.share.ui.share.dialog

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.UIUtil
import me.bors.slack.share.entity.Conversation
import me.bors.slack.share.entity.FileExclusion
import me.bors.slack.share.entity.MessageStyle
import java.awt.Component.LEFT_ALIGNMENT
import java.awt.ComponentOrientation
import java.awt.Dimension
import java.awt.FlowLayout
import java.lang.Boolean.TRUE
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.ScrollPaneConstants
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

private const val UNKNOWN = "Unknown"

@Suppress("TooManyFunctions")
class ShareDialogWrapper(
    private val conversations: List<Conversation>,
    private val text: String = "",
    private val filenames: List<String> = emptyList(),
    private val fileExclusions: List<FileExclusion> = emptyList(),
    private val snippetFileExtension: String = ""
) : DialogWrapper(true) {
    private lateinit var conversationComboBox: ComboBox<Conversation>
    private lateinit var editorPane: JEditorPane
    private lateinit var messageFormatComboBox: ComboBox<MessageStyle>
    private lateinit var extensionTextField: JBTextField

    init {
        title = "Share to Slack"

        init()
    }

    fun getEditedText(): String {
        return editorPane.text
    }

    fun getEditedSnippetFileExtension(): String {
        return extensionTextField.text.replace(".", "").replace(UNKNOWN, "")
    }

    fun getSelectedItem(): Conversation {
        return conversationComboBox.selectedItem as Conversation
    }

    fun getMessageFormatType(): MessageStyle {
        return messageFormatComboBox.selectedItem as MessageStyle
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel()

        dialogPanel.layout = BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS)

        editorPane = createEditorPane()

        val scrollPane = createScrollPane(editorPane)

        dialogPanel.add(createConversationsPanel())
        dialogPanel.add(createVerticalFiller())
        dialogPanel.add(scrollPane)
        dialogPanel.add(createVerticalFiller())

        val attachments = getAttachments()

        val exclusionText = getExclusionText(attachments)

        if (attachments.isNotEmpty() || exclusionText.isNotEmpty()) {
            dialogPanel.add(createAttachmentsPane(attachments, exclusionText))
        } else {
            dialogPanel.add(createMessageFormatPanel())
        }

        dialogPanel.minimumSize = Dimension(400, 200)
        dialogPanel.preferredSize = Dimension(600, 400)
        dialogPanel.maximumSize = Dimension(800, 1200)

        return dialogPanel
    }

    private fun createScrollPane(editorPane: JEditorPane): JBScrollPane {
        val scrollPane = JBScrollPane(
            editorPane,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        )

        scrollPane.border = BorderFactory.createEmptyBorder()
        scrollPane.minimumSize = Dimension(200, 200)
        scrollPane.preferredSize = Dimension(400, 300)
        scrollPane.maximumSize = Dimension(1000, 1000)
        scrollPane.alignmentX = LEFT_ALIGNMENT

        return scrollPane
    }

    private fun createEditorPane(): JEditorPane {
        val editorPane = JEditorPane("text", text)

        editorPane.caretPosition = 0
        editorPane.minimumSize = Dimension(200, 200)
        editorPane.preferredSize = Dimension(400, 300)
        editorPane.maximumSize = Dimension(1000, 1000)
        editorPane.background = UIUtil.getTextFieldBackground()
        editorPane.toolTipText = "Message editor"

        return editorPane
    }

    private fun getExclusionText(attachments: String): String {
        val s = System.lineSeparator()

        val conditionalSeparator = if (attachments.isNotEmpty()) "$s$s" else ""

        val exclusionText = if (fileExclusions.isNotEmpty()) {
            "$conditionalSeparator\u274C Exclusions (Files that cannot be attached): $s " +
                fileExclusions.joinToString(s)
        } else ""
        return exclusionText
    }

    private fun getAttachments(): String {
        val s = System.lineSeparator()

        val attachments = if (filenames.isNotEmpty()) {
            filenames.joinToString(
                s,
                "\uD83D\uDCCE Attachments: $s",
                ""
            )
        } else ""
        return attachments
    }

    private fun createConversationsPanel(): JPanel {
        conversationComboBox = ComboBox(DefaultComboBoxModel(conversations.toTypedArray()))
        conversationComboBox.maximumSize = Dimension(1000, 30)
        conversationComboBox.preferredSize = Dimension(300, 30)
        conversationComboBox.minimumSize = Dimension(100, 30)
        conversationComboBox.toolTipText = "Message destination"

        val conversationsPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        conversationsPanel.alignmentX = LEFT_ALIGNMENT
        conversationsPanel.componentOrientation = ComponentOrientation.LEFT_TO_RIGHT
        conversationsPanel.preferredSize = Dimension(500, 50)
        conversationsPanel.maximumSize = Dimension(1000, 50)
        conversationsPanel.minimumSize = Dimension(400, 50)

        val selectLabel = JLabel("Select Conversation:")

        conversationsPanel.add(selectLabel)
        conversationsPanel.add(conversationComboBox)
        return conversationsPanel
    }

    private fun createVerticalFiller(): Box.Filler {
        val filler = Box.Filler(Dimension(0, 0), Dimension(600, 20), Dimension(1000, 40))

        filler.alignmentX = LEFT_ALIGNMENT

        return filler
    }

    private fun createHorizontalFiller(): Box.Filler {
        return Box.Filler(Dimension(10, 10), Dimension(50, 10), Dimension(400, 10))
    }

    private fun createAttachmentsPane(attachments: String, exclusionText: String): JTextPane {
        val document = DefaultStyledDocument()

        val attributes = SimpleAttributeSet()
        attributes.addAttribute(StyleConstants.CharacterConstants.Italic, TRUE)

        document.insertString(document.length, attachments + exclusionText, attributes)

        val attachmentsPane = JTextPane(document)

        attachmentsPane.isEditable = false
        attachmentsPane.toolTipText = "Attached files"
        attachmentsPane.alignmentX = LEFT_ALIGNMENT
        return attachmentsPane
    }

    private fun createMessageFormatPanel(): JPanel {
        extensionTextField = JBTextField("Show Hide Example")
        extensionTextField.text = if (snippetFileExtension.isEmpty()) UNKNOWN else ".$snippetFileExtension"
        extensionTextField.preferredSize = Dimension(extensionTextField.text.length * 15, 30)
        extensionTextField.isVisible = false

        val extensionLabel = JLabel("Highlighting format:")
        extensionLabel.isVisible = false

        messageFormatComboBox = ComboBox(DefaultComboBoxModel(MessageStyle.values()))
        messageFormatComboBox.toolTipText = "Message style"
        messageFormatComboBox.maximumSize = Dimension(1000, 30)
        messageFormatComboBox.preferredSize = Dimension(150, 30)
        messageFormatComboBox.addActionListener {
            run {
                val visible = messageFormatComboBox.selectedItem == MessageStyle.CODE_SNIPPET
                extensionTextField.isVisible = visible
                extensionLabel.isVisible = visible
            }
        }

        val styleLabel = JLabel("Message style:")

        val messageFormatPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        messageFormatPanel.alignmentX = LEFT_ALIGNMENT
        messageFormatPanel.componentOrientation = ComponentOrientation.LEFT_TO_RIGHT
        messageFormatPanel.preferredSize = Dimension(400, 50)
        messageFormatPanel.maximumSize = Dimension(1000, 50)

        messageFormatPanel.add(styleLabel)
        messageFormatPanel.add(messageFormatComboBox)
        messageFormatPanel.add(createHorizontalFiller())
        messageFormatPanel.add(extensionLabel)
        messageFormatPanel.add(extensionTextField)

        return messageFormatPanel
    }
}
