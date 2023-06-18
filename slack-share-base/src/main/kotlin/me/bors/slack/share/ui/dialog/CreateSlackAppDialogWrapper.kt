package me.bors.slack.share.ui.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.FormBuilder
import me.bors.slack.share.ui.component.UriTextSlider
import java.awt.Dimension
import java.net.URI
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextArea

class CreateSlackAppDialogWrapper(private val uri: URI) : DialogWrapper(true) {
    init {
        title = "Create Slack App"

        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()

        val area = JTextArea(
            "You were redirected to your default system browser to create Slack App with required permissions." +
                "${System.lineSeparator()}If browser page was not opened automatically, copy URL below and " +
                "paste it to your preferred browser:"
        )

        area.font = panel.font
        area.background = panel.background
        area.preferredSize = Dimension(700, 35)

        val slider = UriTextSlider()

        slider.setUri(uri)

        val resultingPanel = FormBuilder.createFormBuilder()
            .addComponent(area)
            .addComponent(slider)
            .addComponentFillVertically(panel, 0)
            .panel

        resultingPanel.preferredSize = Dimension(700, 100)

        return resultingPanel
    }
}
