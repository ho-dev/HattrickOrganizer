package module.youth;

import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;
import javax.swing.*;
import javax.swing.table.TableColumnModel;

public class YouthTrainingView extends FixedColumnsTable implements Refreshable {

    private final YouthTrainingViewTableModel tableModel;

    public YouthTrainingView() {
        super(UserColumnController.instance().getYouthTrainingViewColumnModel());
        tableModel = (YouthTrainingViewTableModel) this.getModel();
        for (var c : tableModel.getColumns()) {
            if (c instanceof YouthTrainingColumn youthTrainingColumn) {
                if (youthTrainingColumn.isEditable()) {
                    var tableColumn = getColumn(c.getId());
                    if (tableColumn != null) {
                        var cb = new JComboBox<>(new YouthTrainingTableEntry.ComboBoxModel());
                        var editor = new DefaultCellEditor(cb);
                        editor.addCellEditorListener(this);
                        tableColumn.setCellEditor(editor);
                    }
                }
            }
        }
        tableModel.initData();
        RefreshManager.instance().registerRefreshable(this);
    }

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
