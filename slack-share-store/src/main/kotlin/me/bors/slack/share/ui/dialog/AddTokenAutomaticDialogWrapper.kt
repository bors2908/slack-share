package me.bors.slack.share.ui.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextArea

class AddTokenAutomaticDialogWrapper : DialogWrapper(true) {

    init {
        title = "Slack Auth"

        init()
    }

    override fun createCenterPanel(): JComponent {
        val text = JTextArea(
            "Warning! This functionality is still beta. " +
                "${System.lineSeparator()}It is using OAuth 2.0 token sequence. " +
                "${System.lineSeparator()}You will be redirected to Slack website to confirm app integration." +
                "${System.lineSeparator()}Due to Slack API limitations, OAuth redirect URL should be HTTPS, " +
                "${System.lineSeparator()}but it is impossible to properly sign localhost certificate, so you would " +
                "${System.lineSeparator()}have to accept the fake certificate in your browser. " +
                "${System.lineSeparator()}It is safe, since HTTPS request never leaves your device." +
                "${System.lineSeparator()}You can close the browser afterwards."
        )

        val panel = JPanel()

        text.background = panel.background
        text.font = panel.font

        return FormBuilder.createFormBuilder()
            .addComponent(text)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
