package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class CheckBoxTableEntry extends JCheckBox implements IHOTableCellEntry {
    private final Color fgStandard;
    private final Color bgStandard;

    private Boolean value;

    public CheckBoxTableEntry(boolean isEnabled, Boolean value, Color fgStandard, Color bgStandard) {
        this.value = value;
        this.fgStandard = fgStandard;
        this.bgStandard = bgStandard;
        createComponent();
        setEnabled(isEnabled);
    }

    protected void setValue(boolean b) {
        this.value = b;
        this.updateComponent();
    }

    public Boolean getValue() {return this.value; }
    

    @Override
    public JComponent getComponent(boolean selected) {
        if (selected) {
            this.setBackground(HODefaultTableCellRenderer.SELECTION_BG);

        } else {
            this.setBackground(bgStandard);
        }
        this.setForeground(selected ? HODefaultTableCellRenderer.SELECTION_FG : fgStandard);
        return this;
    }

    @Override
    public void clear() {
        this.setSelected(false);
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
        this.setSelected(this.value);
        this.setOpaque(true);
        this.setForeground(fgStandard);
    }

    @Override
    public void updateComponent() {
        this.setSelected(this.value);
        this.setBackground(bgStandard);
        this.setForeground(fgStandard);
    }

    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if ( b){
            this.setFocusable(true);
            this.addItemListener(event -> setValue(event.getStateChange()== ItemEvent.SELECTED));
//            var editor = new DefaultCellEditor(this.checkBox);
//            editor.setClickCountToStart(1);
        }
    }
}
