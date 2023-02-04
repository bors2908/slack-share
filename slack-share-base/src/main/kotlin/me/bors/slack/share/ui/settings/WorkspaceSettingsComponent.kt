package me.bors.slack.share.ui.settings

import com.intellij.ui.CollectionListModel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBPanel
import me.bors.slack.share.entity.Workspace
import java.awt.BorderLayout
import java.awt.Component
import java.awt.ComponentOrientation
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionListener
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextArea

abstract class WorkspaceSettingsComponent(
    manualAction: ActionListener,
    removeAction: ActionListener,
    upAction: ActionListener,
    downAction: ActionListener,
    workspaces: List<Workspace>
) {
    abstract var panel: JPanel
    abstract val preferredFocusedComponent: JComponent

    protected val manualSetButton: JButton = JButton("Add manually")
    protected val removeButton: JButton = JButton("Remove")
    protected val moveUpButton: JButton = JButton("Move up")
    protected val moveDownButton: JButton = JButton("Move down")

    protected val workspacesList: JBList<Workspace> = JBList<Workspace>()

    protected val buttonJPanel: JPanel = JPanel()

    init {
        setPanel(manualAction, removeAction, upAction, downAction)

        setWorkspaces(workspaces)
    }

    abstract fun extraButtonActions(opName: String)

    fun setWorkspaces(workspaces: List<Workspace>) {
        workspacesList.model = CollectionListModel(workspaces)
    }

    fun getSelectedWorkspace(): Workspace? {
        return workspacesList.selectedValue
    }

    private fun setPanel(
        manualAction: ActionListener,
        removeAction: ActionListener,
        upAction: ActionListener,
        downAction: ActionListener
    ) {
        manualSetButton.addActionListener(manualAction)
        removeButton.addActionListener(removeAction)
        moveUpButton.addActionListener(upAction)
        moveDownButton.addActionListener(downAction)

        workspacesList.background = JTextArea().background

        buttonJPanel.preferredSize = Dimension(150, 300)

        panel = JBPanel<Nothing>(BorderLayout())

        val innerPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        innerPanel.alignmentX = Component.LEFT_ALIGNMENT
        innerPanel.componentOrientation = ComponentOrientation.LEFT_TO_RIGHT

        innerPanel.add(buttonJPanel)
        innerPanel.add(createFiller())

        panel.add(innerPanel, BorderLayout.WEST)
        panel.add(workspacesList, BorderLayout.CENTER)
    }

    private fun createFiller(): Box.Filler {
        return Box.Filler(Dimension(5, 100), Dimension(5, 300), Dimension(5, 1000))
    }
}
