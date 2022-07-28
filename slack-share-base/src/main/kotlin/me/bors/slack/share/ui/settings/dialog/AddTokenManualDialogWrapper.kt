package me.bors.slack.share.ui.settings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import me.bors.slack.share.persistence.SlackUserTokenSecretState
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class AddTokenManualDialogWrapper : DialogWrapper(true) {
    lateinit var field: JBTextField

    init {
        title = "Insert Token"

        init()
    }

    override fun createCenterPanel(): JComponent {
        val label = JLabel(
            "Open Slack App's settings on \"OAuth & Permissions\" tab, copy User OAuth Token and paste here."
        )

        field = JBTextField()
        field.isEnabled = true

        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Insert token: ", field)
            .addComponent(label)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}

class AddTokenManualDialogController : DialogWrapperController {
    override fun show() {
        val wrapper = AddTokenManualDialogWrapper()

        if (wrapper.showAndGet()) {
            SlackUserTokenSecretState.set(wrapper.field.text)
        }
    }
}
