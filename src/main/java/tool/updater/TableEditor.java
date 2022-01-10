// %1304476644:de.hattrickorganizer.tools.updater%
/*
 * Created on 27.12.2004
 *
 */
package tool.updater;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.util.HOLogger;

import java.awt.Component;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;



/**
 * TableEditor for UpdateDialogs
 *
 * @author tdietz
 *
 * @since 1.35
 */
public final class TableEditor extends AbstractCellEditor implements TableCellEditor {
    //~ Instance fields ----------------------------------------------------------------------------

    protected HashMap<Integer,TableCellEditor> editors;

    protected TableCellEditor defaultEditor;

    protected TableCellEditor editor;
    private JTextField textField;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TableEditor object.
     */
	public TableEditor() {
		editors = new HashMap<>();

		textField = new JTextField();
		textField.setBorder(null);
		textField.setFocusCycleRoot(true);

		defaultEditor = new DefaultCellEditor(textField);
	}

    //~ Methods ------------------------------------------------------------------------------------

    public boolean isCellEditable() {
        return true;
    }

    public Object getCellEditorValue() {
        return editor.getCellEditorValue();
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
        editor = editors.get(row);

        if (editor == null) {
            editor = defaultEditor;
        }

        if (value != null) {
            try {
            	if(value instanceof JComponent){
            		((JComponent)value).setBackground(isSelected?HODefaultTableCellRenderer.SELECTION_BG:ColorLabelEntry.BG_STANDARD);
            		((JComponent)value).setForeground(isSelected?HODefaultTableCellRenderer.SELECTION_FG:ColorLabelEntry.FG_STANDARD);
            	}
            		
                if (value instanceof JTextField) {
                    ((JTextField) value).setFocusCycleRoot(true);
                    return (JTextField) value;
                }

                if (value instanceof JComboBox) {
                    return (JComboBox) value;
                }
                
                if (value instanceof JButton) {
                    return (JButton) value;
                }

                if (value instanceof JCheckBox) {
                    return (JCheckBox) value;
                }
            } catch (IllegalArgumentException e) {
                HOLogger.instance().log(getClass(),e);
            }
        }

        return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    public void add(int row, TableCellEditor teditor) {
        editors.put(row, teditor);
    }

    @Override
	public void addCellEditorListener(CellEditorListener l) {
        editor.addCellEditorListener(l);
    }

    @Override
	public void cancelCellEditing() {
        super.cancelCellEditing();
    }

    @Override
	public void removeCellEditorListener(CellEditorListener l) {
        editor.removeCellEditorListener(l);
    }

    @Override
	public boolean shouldSelectCell(EventObject anEvent) {
        return super.shouldSelectCell(anEvent);
    }

    @Override
	public boolean stopCellEditing() {
        return super.stopCellEditing();
    }
}