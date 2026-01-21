package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.PlayersTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ItemEvent;

public class CheckBoxTableEntry implements IHOTableCellEntry {

    private final Color fgStandard;
    private final Color bgStandard;

    private JCheckBox checkBox;
    private Boolean value;

    public CheckBoxTableEntry(boolean isEnabled, Boolean value, Color fgStandard, Color bgStandard) {
        this.value = value;
        this.checkBox = new JCheckBox();
        this.checkBox.setSelected(value != null && value);
        this.checkBox.setEnabled(isEnabled);
        this.fgStandard = fgStandard;
        this.bgStandard = bgStandard;
        createComponent();
    }

    public void setValue(boolean b) {
        this.value = b;
        this.checkBox.setSelected(b);
        this.updateComponent();
    }

    public Boolean getValue() {
        return this.value;
    }

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

    @Override
    public void clear() {
        this.checkBox.setSelected(false);
    }

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
        //Not a checkbox
        return -1;
    }

    @Override
    public int compareToThird(IHOTableCellEntry obj) {
        return this.compareTo(obj);
    }

    @Override
    public void createComponent() {
        updateComponent();
    }

    @Override
    public void updateComponent() {
        this.checkBox.setSelected(this.value);
        this.checkBox.setBackground(bgStandard);
        this.checkBox.setForeground(fgStandard);
    }
}
