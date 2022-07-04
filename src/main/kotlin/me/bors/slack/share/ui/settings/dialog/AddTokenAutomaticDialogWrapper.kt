package me.bors.slack.share.ui.settings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.FormBuilder
import me.bors.slack.share.SlackAuthenticator
import me.bors.slack.share.persistence.SlackUserTokenSecretState
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class AddTokenAutomaticDialogWrapper : DialogWrapper(true) {
    init {
        title = "Slack Auth"

        init()
    }

    override fun createCenterPanel(): JComponent {
        val label = JLabel(
            "Warning! This functionality is still beta. It is using OAuth 2.0 sequence to receive tokens " +
                    "with redirects to local HTTPS server."
        )

        return FormBuilder.createFormBuilder()
            .addComponent(label)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}

class AddTokenAutomaticDialogController : DialogWrapperController {
    override fun show() {
        val wrapper = AddTokenAutomaticDialogWrapper()

        if (wrapper.showAndGet()) {
            val authenticator = SlackAuthenticator()

            SlackUserTokenSecretState.set(authenticator.auth())
        }
    }
}
