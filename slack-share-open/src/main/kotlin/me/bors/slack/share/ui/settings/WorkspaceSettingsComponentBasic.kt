package me.bors.slack.share.ui.settings

import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.components.panels.VerticalLayout.TOP
import me.bors.slack.share.entity.Workspace
import java.awt.event.ActionListener
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class WorkspaceSettingsComponentBasic(
    manualAction: ActionListener,
    removeAction: ActionListener,
    moveUpAction: ActionListener,
    moveDownAction: ActionListener,
    workspaces: List<Workspace>
) : WorkspaceSettingsComponent(manualAction, removeAction, moveUpAction, moveDownAction, workspaces) {
    override val preferredFocusedComponent: JComponent
        get() = manualSetButton

    override lateinit var panel: JPanel

    init {
        buttonJPanel.layout = VerticalLayout(5)
        buttonJPanel.add(JLabel("Slack Workspaces:"), TOP)
        buttonJPanel.add(manualSetButton, TOP)
        buttonJPanel.add(removeButton, TOP)
        buttonJPanel.add(moveUpButton, TOP)
        buttonJPanel.add(moveDownButton, TOP)
    }

    override fun extraButtonActions(opName: String) {
        // No-op.
    }
}
