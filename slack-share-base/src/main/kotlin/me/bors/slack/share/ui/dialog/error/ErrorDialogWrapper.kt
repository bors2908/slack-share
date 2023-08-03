package me.bors.slack.share.ui.dialog.error

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import java.awt.Dimension
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class ErrorDialogWrapper(private val error: String) : DialogWrapper(true) {
    private val reportButton = JButton("Report Problem")

    private val reportAction = ReportAction()

    init {
        title = "Error"

        reportButton.action = reportAction

        init()
    }

    private inner class ReportAction : DialogWrapperAction("Report Problem") {
        private val serialVersionUID: Long = -4340328936056929591L

        init {
            putValue(Action.NAME, "Report Problem")
        }

        override fun doAction(e: ActionEvent) {
            BrowserUtil.browse(REPORT_URL)
        }
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(GridBagLayout())

        val text = JBLabel("<html>$error</html>")

        text.maximumSize = Dimension(300, 500)

        dialogPanel.add(text)

        return dialogPanel
    }

    override fun createActions(): Array<out Action> {
        super.createDefaultActions()

        return arrayOf(reportAction, okAction, cancelAction)
    }

    companion object {
        const val REPORT_URL: String = "https://github.com/bors2908/slack-share/issues"
    }
}
