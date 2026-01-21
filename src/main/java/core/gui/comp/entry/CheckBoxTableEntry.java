package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class CheckBoxTableEntry implements IHOTableCellEntry {

    private final Color fgStandard;
    private final Color bgStandard;
    private final JCheckBox checkBox;
    private Boolean value;

    /**
     * Create table cell entry for booleans by check box
     * @param isEnabled boolean
     * @param value Boolean - Checked
     * @param fgStandard Color
     * @param bgStandard Color
     */
    public CheckBoxTableEntry(boolean isEnabled, Boolean value, Color fgStandard, Color bgStandard) {
        this.value = value;
        this.checkBox = new JCheckBox();
        this.checkBox.setSelected(value != null && value);
        this.checkBox.setEnabled(isEnabled);
        this.fgStandard = fgStandard;
        this.bgStandard = bgStandard;
        createComponent();
    }

    /**
     * Set value is called from HOTableModel
     * @param b boolean
     */
    public void setValue(boolean b) {
        this.value = b;
        this.checkBox.setSelected(b);
        this.updateComponent();
    }

    /**
     * Get value is called from HOTableModel
     * @return Boolean
     */
    public Boolean getValue() {
        return this.value;
    }

    /**
     * Return the checkbox component
     * @param selected
     * @return JComponent
     */
    @Override
    public JComponent getComponent(boolean selected) {
        if (selected) {
            this.checkBox.setBackground(HODefaultTableCellRenderer.SELECTION_BG);

        } else {
            this.checkBox.setBackground(bgStandard);
        }
        this.checkBox.setForeground(selected ? HODefaultTableCellRenderer.SELECTION_FG : fgStandard);
        return this.checkBox;
    }

    /**
     * Reset the checkbox
     */
    @Override
    public void clear() {
        this.checkBox.setSelected(false);
    }

    /**
     * Compare with other cell entry
     * @param obj the object to be compared.
     * @return int [-1,0,1]
     */
    @Override
    public int compareTo(@NotNull IHOTableCellEntry obj) {
        if (obj instanceof CheckBoxTableEntry entry) {
            if (this.getValue() == entry.getValue()){
                return 0;
            }
            else if (this.getValue()){
                return 1;
            }
        }
        // Not a checkbox
        return -1;
    }

    /**
     * Same as compareTo
     * @param obj
     * @return
     */
    @Override
    public int compareToThird(IHOTableCellEntry obj) {
        return this.compareTo(obj);
    }

    /**
     * Update the component
     */
    @Override
    public void createComponent() {
        updateComponent();
    }

    /**
     * Update the component
     */
    @Override
    public void updateComponent() {
        this.checkBox.setSelected(this.value);
        this.checkBox.setBackground(bgStandard);
        this.checkBox.setForeground(fgStandard);
    }
}
