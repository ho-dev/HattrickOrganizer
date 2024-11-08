package module.youth;

import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;
import javax.swing.*;
import javax.swing.table.TableColumnModel;

public class YouthTrainingView extends JScrollPane implements Refreshable {

    private final FixedColumnsTable table;
    private YouthTrainingViewTableModel tableModel;

    public YouthTrainingView() {
        tableModel = UserColumnController.instance().getYouthTrainingViewColumnModel();
        table = new FixedColumnsTable(tableModel);
        this.setViewportView(table.getContainerComponent());
        for (var c : tableModel.getColumns()) {
            if ( c instanceof YouthTrainingColumn youthTrainingColumn) {
                if (youthTrainingColumn.isEditable()) {
                    var tablecol = table.getColumn(c.getIndex());
                    if (tablecol != null) {
                        var cb = new JComboBox<>(new YouthTrainingTableEntry.ComboBoxModel());
                        var editor = new DefaultCellEditor(cb);
                        editor.addCellEditorListener(table);
                        tablecol.setCellEditor(editor);
                    }
                }
            }
        }
        tableModel.initData();
//        initModel();
//        RefreshManager.instance().registerRefreshable(this);
//        table.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
    }

//    private void initModel() {
//        setOpaque(false);
//        if (tableModel == null) {
//            tableModel = UserColumnController.instance().getYouthTrainingViewColumnModel();
//            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//            table.setRowSelectionAllowed(true);
//
//            table.setModel(tableModel);
//            TableColumnModel tableColumnModel = table.getColumnModel();
//            for (int i = 0; i < tableModel.getColumnCount(); i++) {
//                tableColumnModel.getColumn(i).setIdentifier(i);
//            }
//
//            for (var c : tableModel.getColumns()) {
//                if (c.canBeDisabled()) {
//                    var tablecol = table.getColumn(c.getIndex());
//                    if (tablecol != null) {
//                        var cb = new JComboBox<>(new YouthTrainingTableEntry.ComboBoxModel());
//                        var editor = new DefaultCellEditor(cb);
//                        editor.addCellEditorListener(table);
//                        tablecol.setCellEditor(editor);
//                    }
//                }
//            }
//
//            tableModel.initTable(table);
//        }
//        tableModel.initData();
//    }

    @Override
    public void refresh() {
        this.tableModel.initData();
        repaint();
    }

    @Override
    public void reInit() {
        refresh();
    }

    public void storeUserSettings() {
        this.tableModel.storeUserSettings();
    }
}
