package core.gui.comp.entry

import core.gui.comp.renderer.HODefaultTableCellRenderer
import java.awt.Color
import javax.swing.JCheckBox
import javax.swing.JComponent

open class CheckBoxTableEntry(isEnabled: Boolean, var value: Boolean, fgStandard: Color?, bgStandard: Color?) :
    IHOTableCellEntry {
    private val fgStandard: Color?
    private val bgStandard: Color?
    private val checkBox: JCheckBox = JCheckBox()

    /**
     * Create table cell entry for booleans by check box
     * @param isEnabled boolean
     * @param value Boolean - Checked
     * @param fgStandard Color
     * @param bgStandard Color
     */
    init {
        this.checkBox.setSelected(value)
        this.checkBox.setEnabled(isEnabled)
        this.fgStandard = fgStandard
        this.bgStandard = bgStandard
        createComponent()
    }

    /**
     * Set value is called from HOTableModel
     * @param b boolean
     */
    open fun changeValue(b: Boolean) {
        this.value = b
        this.updateComponent()
    }

    /**
     * Return the checkbox component
     * @param selected boolean
     * @return JComponent
     */
    override fun getComponent(selected: Boolean): JComponent {
        if (selected) {
            this.checkBox.setBackground(HODefaultTableCellRenderer.SELECTION_BG)
            this.checkBox.setForeground(HODefaultTableCellRenderer.SELECTION_FG)
        } else {
            this.checkBox.setBackground(bgStandard)
            this.checkBox.setForeground(fgStandard)
        }
        return this.checkBox
    }

    /**
     * Reset the checkbox
     */
    override fun clear() {
        this.value = false
        this.checkBox.setSelected(false)
    }

    /**
     * Compare with other cell entry.
     * Descending order is true, false, null.
     * @param obj the object to be compared.
     * @return int [-1,0,1]
     */
    override fun compareTo(obj: IHOTableCellEntry): Int {
        if (obj is CheckBoxTableEntry) {
            return this.value.compareTo(obj.value)
        }
        // Not a checkbox
        return -1
    }

    /**
     * Same as compareTo
     * @param obj IHOTableCellEntry The other entry
     * @return int
     */
    override fun compareToThird(obj: IHOTableCellEntry): Int {
        return this.compareTo(obj)
    }

    /**
     * Update the component
     */
    override fun createComponent() {
        updateComponent()
    }

    /**
     * Update the component
     */
    override fun updateComponent() {
        this.checkBox.setSelected(value)
        this.checkBox.setBackground(bgStandard)
        this.checkBox.setForeground(fgStandard)
    }
}
