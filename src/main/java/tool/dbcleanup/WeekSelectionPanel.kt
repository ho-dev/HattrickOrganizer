package tool.dbcleanup

import core.model.HOVerwaltung
import java.awt.FlowLayout
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.BorderFactory
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

internal class WeekSelectionPanel(weeks: Int, showRemoveAll: Boolean) : JPanel() {
    private val labelRemoveOlderThan = JLabel(HOVerwaltung.instance().getLanguageString("dbcleanup.removeOlderThan"))
    private val labelWeeks = JLabel(HOVerwaltung.instance().getLanguageString("dbcleanup.weeks"))

    private val noneCheckBox = JCheckBox(HOVerwaltung.instance().getLanguageString("dbcleanup.none"))
    private val allCheckBox = JCheckBox(HOVerwaltung.instance().getLanguageString("dbcleanup.allTime"))
    private val weeksTextField = JTextField(3)

    constructor(weeks: Int) : this(weeks, true)

    init {
        // Small downward shift to align the labels with the main label
        border = BorderFactory.createEmptyBorder(4, 0, 0, 0)
        layout = FlowLayout(FlowLayout.LEADING, 0, 0)
        noneCheckBox.isSelected = false
        allCheckBox.isSelected = false
        weeksTextField.text = "0"
        if (weeks <= DBCleanupTool.REMOVE_NONE) {
            noneCheckBox.isSelected = true
        } else if (weeks == DBCleanupTool.REMOVE_ALL) {
            allCheckBox.isSelected = true
        } else {
            weeksTextField.text = weeks.toString()
        }
        if (!showRemoveAll) allCheckBox.isVisible = false
        initComponents()
    }

    private fun initComponents() {
        noneCheckBox.addActionListener {
            if (noneCheckBox.isSelected) {
                allCheckBox.isSelected = false
                weeksTextField.text = "0"
            }
        }
        allCheckBox.addActionListener {
            if (allCheckBox.isSelected) {
                noneCheckBox.isSelected = false
                weeksTextField.text = "0"
            }
        }
        weeksTextField.addFocusListener(object : FocusListener {
            override fun focusGained(arg0: FocusEvent) {
                noneCheckBox.isSelected = false
                allCheckBox.isSelected = false
            }

            override fun focusLost(arg0: FocusEvent) {
                // do nothing
            }
        })

        add(noneCheckBox)
        add(JLabel("     "))
        add(allCheckBox)
        add(JLabel("     "))
        add(labelRemoveOlderThan)
        add(weeksTextField)
        add(labelWeeks)
    }

    fun getWeeks(): Int {
        if (noneCheckBox.isSelected) {
            return DBCleanupTool.REMOVE_NONE
        } else if (allCheckBox.isSelected) {
            return DBCleanupTool.REMOVE_ALL
        } else {
            var weeks = DBCleanupTool.REMOVE_NONE
            try {
                weeks = weeksTextField.text.toInt()
            } catch (e: Exception) {
                // be silent
            }
            return if (weeks > 0) weeks
            else DBCleanupTool.REMOVE_NONE
        }
    }
}
