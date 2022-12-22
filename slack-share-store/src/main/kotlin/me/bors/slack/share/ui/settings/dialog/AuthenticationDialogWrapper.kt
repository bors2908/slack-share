package me.bors.slack.share.ui.settings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.FormBuilder
import me.bors.slack.share.ui.component.UriTextSlider
import java.awt.Dimension
import java.net.URI
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea

class AuthenticationDialogWrapper : DialogWrapper(true) {
    private val slider: UriTextSlider = UriTextSlider()

    init {
        title = "Slack Auth"

        init()
    }

    fun setUri(uri: URI) {
        slider.setUri(uri)
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
            .addComponent(slider)
            .addComponent(eLabel)
            .addComponent(label)
            .addComponentFillVertically(panel, 0)
            .panel

        resultingPanel.preferredSize = Dimension(700, 100)

        return resultingPanel
    }
}



