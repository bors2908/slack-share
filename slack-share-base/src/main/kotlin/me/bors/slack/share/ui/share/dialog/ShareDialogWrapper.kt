package me.bors.slack.share.ui.share.dialog

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.PopupMenuListenerAdapter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import com.intellij.util.ui.UIUtil
import me.bors.slack.share.entity.FileExclusion
import me.bors.slack.share.entity.MessageStyle
import me.bors.slack.share.entity.SlackConversation
import me.bors.slack.share.entity.Workspace
import java.awt.BorderLayout
import java.awt.Component.LEFT_ALIGNMENT
import java.awt.ComponentOrientation
import java.awt.Dimension
import java.awt.FlowLayout
import java.lang.Boolean.TRUE
import javax.swing.*
import javax.swing.event.PopupMenuEvent
import javax.swing.plaf.basic.BasicComboBoxEditor
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

private const val UNKNOWN = "Unknown"

//TODO Conversation performance loading
@Suppress("TooManyFunctions")
class ShareDialogWrapper(
    private val workspaces: List<Workspace>,
    private val text: String = "",
    private val filenames: List<String> = emptyList(),
    private val fileExclusions: List<FileExclusion> = emptyList(),
    private val snippetFileExtension: String = "",
    private val conversationProcessing: (Workspace) -> List<SlackConversation>
) : DialogWrapper(true) {
    private lateinit var workspacesComboBox: ComboBox<Workspace>
    private lateinit var conversationComboBox: ComboBox<SlackConversation>
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

    fun getSelectedWorkspace(): Workspace {
        return workspacesComboBox.selectedItem as Workspace
    }

    fun getSelectedConversation(): SlackConversation {
        return conversationComboBox.selectedItem as SlackConversation
    }

    fun getMessageFormatType(): MessageStyle {
        return messageFormatComboBox.selectedItem as MessageStyle
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel()

        dialogPanel.layout = BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS)

        editorPane = createEditorPane()

        val scrollPane = createScrollPane(editorPane)

        workspacesComboBox = ComboBox(DefaultComboBoxModel(workspaces.toTypedArray()))
        conversationComboBox = ComboBox()

        dialogPanel.add(createWorkspacesPanel())
        dialogPanel.add(createVerticalFiller(5))
        dialogPanel.add(createConversationsPanel())
        dialogPanel.add(createVerticalFiller(20))
        dialogPanel.add(scrollPane)
        dialogPanel.add(createVerticalFiller(20))

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

    private fun createWorkspacesPanel(): JPanel {
        workspacesComboBox.maximumSize = Dimension(1000, 30)
        workspacesComboBox.preferredSize = Dimension(300, 30)
        workspacesComboBox.minimumSize = Dimension(100, 30)
        workspacesComboBox.toolTipText = "Workplace to select conversation from."
        workspacesComboBox.addPopupMenuListener(object : PopupMenuListenerAdapter() {
            override fun popupMenuWillBecomeInvisible(e: PopupMenuEvent) {
                super.popupMenuWillBecomeInvisible(e)

                run {
                    val workspace = workspacesComboBox.selectedItem as Workspace

                    updateConversations(workspace)
                }
            }
        })

        val workspacesPanel = JPanel(BorderLayout())
        workspacesPanel.alignmentX = LEFT_ALIGNMENT
        workspacesPanel.preferredSize = Dimension(500, 30)
        workspacesPanel.maximumSize = Dimension(1000, 30)
        workspacesPanel.minimumSize = Dimension(400, 30)

        val selectLabel = JLabel("Select Workspace:")

        workspacesPanel.add(selectLabel, BorderLayout.WEST)
        workspacesPanel.add(workspacesComboBox, BorderLayout.CENTER)

        return workspacesPanel
    }

    private fun updateConversations(workspace: Workspace) {
        setComboBox(conversationComboBox, true)

        refreshConversationValues(conversationComboBox, workspace)

        setComboBox(conversationComboBox, false)
    }

    private fun refreshConversationValues(comboBox: ComboBox<SlackConversation>, workspace: Workspace) {
        val conversations = conversationProcessing.invoke(workspace)

        comboBox.model = DefaultComboBoxModel(conversations.toTypedArray())
    }

    private fun setComboBox(comboBox: ComboBox<*>, loading: Boolean) {
        if (loading) {
            val loadingExtension = ExtendableTextComponent.Extension.create(AnimatedIcon.Default(), null, null)

            comboBox.editor = object : BasicComboBoxEditor() {
                override fun createEditorComponent(): JTextField {
                    val ecbEditor = ExtendableTextField()
                    ecbEditor.addExtension(loadingExtension)
                    ecbEditor.border = null
                    return ecbEditor
                }
            }
        } else {
            comboBox.editor = BasicComboBoxEditor()
        }
    }

    private fun createConversationsPanel(): JPanel {
        conversationComboBox.maximumSize = Dimension(1000, 30)
        conversationComboBox.preferredSize = Dimension(300, 30)
        conversationComboBox.minimumSize = Dimension(100, 30)
        conversationComboBox.toolTipText = "Message destination"

        val conversationsPanel = JPanel(BorderLayout())
        conversationsPanel.alignmentX = LEFT_ALIGNMENT
        conversationsPanel.preferredSize = Dimension(500, 30)
        conversationsPanel.maximumSize = Dimension(1000, 30)
        conversationsPanel.minimumSize = Dimension(400, 30)

        val selectLabel = JLabel("Select Conversation:")

        conversationsPanel.add(selectLabel, BorderLayout.WEST)
        conversationsPanel.add(conversationComboBox, BorderLayout.CENTER)

        refreshConversationValues(conversationComboBox, workspaces.first())

        return conversationsPanel
    }

    private fun createVerticalFiller(height: Int): Box.Filler {
        val filler = Box.Filler(Dimension(0, 0), Dimension(600, height), Dimension(1000, height * 2))

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
