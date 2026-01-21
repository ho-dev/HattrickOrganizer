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

    public static class Editor implements CellEditorListener {

        private JCheckBox checkBox;
        private DefaultCellEditor  cellEditor;

        public Editor() {
            this.checkBox = new JCheckBox();
            cellEditor = new DefaultCellEditor(this.checkBox);
            cellEditor.setClickCountToStart(1);
            cellEditor.addCellEditorListener(this);
        }

        public DefaultCellEditor getCellEditor() {return cellEditor; }
        public JCheckBox getCheckBox() { return checkBox; }

        @Override
        public void editingStopped(ChangeEvent e) {

        }

        @Override
        public void editingCanceled(ChangeEvent e) {

        }

    }

    private static Editor editor =  new Editor();
    public static Editor getEditor() { return editor; }

    private final Color fgStandard;
    private final Color bgStandard;

    private Boolean value;
    private boolean isEnabled;

    private JCheckBox getCheckbox(){return this.getEditor().getCheckBox();}
    public CheckBoxTableEntry(boolean isEnabled, Boolean value, Color fgStandard, Color bgStandard) {
        this.value = value;
        this.isEnabled=isEnabled;
        this.fgStandard = fgStandard;
        this.bgStandard = bgStandard;
        createComponent();
    }

    protected void setValue(boolean b) {
        this.value = b;
        this.updateComponent();
    }

    public Boolean getValue() {
        return this.value;
    }
    

    @Override
    public JComponent getComponent(boolean selected) {
        var checkbox = getCheckbox();
        if (selected) {
            checkbox.setBackground(HODefaultTableCellRenderer.SELECTION_BG);

        } else {
            checkbox.setBackground(bgStandard);
        }
        checkbox.setForeground(selected ? HODefaultTableCellRenderer.SELECTION_FG : fgStandard);
        return checkbox;
    }

    @Override
    public void clear() {
        getCheckbox().setSelected(false);
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
        var checkbox =  getCheckbox();
        checkbox.setEnabled(this.isEnabled);
        checkbox.setSelected(this.value);
        checkbox.setBackground(bgStandard);
        checkbox.setForeground(fgStandard);
    }

//    public void setEnabled(boolean b) {
//        var checkbox = getCheckbox();
//        checkbox.setEnabled(b);
//        if ( b){
//            checkbox.setFocusable(true);
////            checkbox.addItemListener(event ->
////                    setValue(event.getStateChange()== ItemEvent.SELECTED));
////            var editor = new DefaultCellEditor(this.checkBox);
////            editor.setClickCountToStart(1);
//        }
//    }
}
