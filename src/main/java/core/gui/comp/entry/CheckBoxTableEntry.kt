package core.gui.comp.entry

import core.gui.comp.renderer.HODefaultTableCellRenderer
import java.awt.Color
import javax.swing.JCheckBox
import javax.swing.JComponent

open class CheckBoxTableEntry(isEnabled: Boolean, var value: Boolean?, fgStandard: Color?, bgStandard: Color?) :
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
        this.checkBox.setSelected(value != null && value == true)
        this.checkBox.setEnabled(isEnabled)
        this.fgStandard = fgStandard
        this.bgStandard = bgStandard
        createComponent()
    }

    /**
     * Set value is called from HOTableModel
     * @param b boolean
     */
    open fun changeValue(b: Boolean?) {
        this.value = b
        this.checkBox.setSelected(b != null && b)
        this.updateComponent()
    }

    /**
     * Get value is called from HOTableModel
     * @return Boolean
     */
    fun getValue(): Boolean {
        return this.value!!
    }

    /**
     * Return the checkbox component
     * @param selected boolean
     * @return JComponent
     */
    override fun getComponent(selected: Boolean): JComponent {
        if (selected) {
            this.checkBox.setBackground(HODefaultTableCellRenderer.SELECTION_BG)
        } else {
            this.checkBox.setBackground(bgStandard)
        }
        this.checkBox.setForeground(if (selected) HODefaultTableCellRenderer.SELECTION_FG else fgStandard)
        return this.checkBox
    }

    /**
     * Reset the checkbox
     */
    override fun clear() {
        this.checkBox.setSelected(false)
    }

    /**
     * Compare with other cell entry
     * @param obj the object to be compared.
     * @return int [-1,0,1]
     */
    override fun compareTo(obj: IHOTableCellEntry): Int {
        if (obj is CheckBoxTableEntry) {
            if (this.getValue() == obj.getValue()) {
                return 0
            } else if (this.getValue()) {
                return 1
            }
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
        this.checkBox.setSelected(this.value!!)
        this.checkBox.setBackground(bgStandard)
        this.checkBox.setForeground(fgStandard)
    }
}
