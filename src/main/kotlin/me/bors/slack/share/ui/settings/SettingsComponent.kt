package me.bors.slack.share.ui.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class SettingsComponent {
    val panel: JPanel
    private val slackShareUserTokenField = JBTextField()
    private val addTokenManuallyCheckBox = JBCheckBox("Add token manually")

    val preferredFocusedComponent: JComponent
        get() = slackShareUserTokenField

    var slackShareUserToken: String
        get() = slackShareUserTokenField.text
        set(newText) {
            slackShareUserTokenField.text = newText
        }

    var addTokenManually: Boolean
        get() = addTokenManuallyCheckBox.isSelected
        set(newStatus) {
            addTokenManuallyCheckBox.isSelected = newStatus
        }

    init {
        panel = FormBuilder.createFormBuilder()
            .addComponent(addTokenManuallyCheckBox, 1)
            .addLabeledComponent(JBLabel("Token: "), slackShareUserTokenField, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
