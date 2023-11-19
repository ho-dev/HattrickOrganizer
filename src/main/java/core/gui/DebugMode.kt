package core.gui

import core.db.frontend.SQLDialog
import core.net.MyConnector
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import javax.swing.JCheckBoxMenuItem
import javax.swing.JMenu
import javax.swing.JMenuItem

object DebugMode {
    val developerMenu: JMenu
        get() {
            val menu = JMenu("Debug")
            menu.add(sQLDialogMenuItem)
            menu.add(lookAndFeelDialogMenuItem)
            menu.add(saveXMLMenuItem)
            return menu
        }
    private val lookAndFeelDialogMenuItem: JMenuItem
        get() {
            val newItem = JMenuItem("Look and Feel")
            newItem.addActionListener { e: ActionEvent? -> LookAndFeelDialog().isVisible = true }
            return newItem
        }
    private val sQLDialogMenuItem: JMenuItem
        get() {
            val newItem = JMenuItem("SQL Editor")
            newItem.addActionListener { e: ActionEvent? -> SQLDialog().isVisible = true }
            return newItem
        }
    private val saveXMLMenuItem: JMenuItem
        get() {
            val newItem: JMenuItem = JCheckBoxMenuItem("Save downloaded XML")
            newItem.addItemListener { e: ItemEvent -> MyConnector.setDebugSave(e.stateChange == ItemEvent.SELECTED) }
            return newItem
        }
}