package me.bors.slack.share.ui.settings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class AddTokenManualDialogWrapper(val createApp: ActionListener) : DialogWrapper(true) {
    lateinit var field: JBTextField

    init {
        title = "Insert Token"

        init()
    }

    override fun createCenterPanel(): JComponent {
        val label = JLabel("Open Slack App's settings on \"OAuth & Permissions\" tab, copy User OAuth Token and paste here.")

        val button = JButton("Create App")

        button.addActionListener(createApp)

        val panel = JPanel(BorderLayout())

        panel.add(JLabel("Create a new slack App if you haven't already. Install app to Slack workspace afterwards."), BorderLayout.WEST)
        panel.add(button, BorderLayout.EAST)

        field = JBTextField()
        field.isEnabled = true

        return FormBuilder.createFormBuilder()
            .addComponent(panel)
            .addComponent(label)
            .addLabeledComponent("Insert token: ", field)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}

