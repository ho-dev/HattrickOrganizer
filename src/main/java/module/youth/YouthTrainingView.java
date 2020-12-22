package module.youth;

import core.gui.RefreshManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.UserColumnController;

import javax.swing.*;
import javax.swing.table.TableColumnModel;

public class YouthTrainingView extends JTable implements core.gui.Refreshable {

    private YouthTrainingViewTableModel tableModel;

    public YouthTrainingView() {
        super();
        initModel();
        RefreshManager.instance().registerRefreshable(this);
        setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
    }

    private void initModel() {
        setOpaque(false);
        if (tableModel == null) {
            tableModel = UserColumnController.instance().getYouthTrainingViewColumnModel();
            setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setRowSelectionAllowed(true);

            setModel(tableModel);
            TableColumnModel tableColumnModel = getColumnModel();
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                tableColumnModel.getColumn(i).setIdentifier(i);
            }

            for ( var c : tableModel.getColumns()){
                if ( c.isEditable()){
                    var tablecol = this.getColumn(c.getIndex());
                    if ( tablecol != null ){
                        tablecol.setCellEditor(new DefaultCellEditor(new JComboBox<>(new YouthTrainingTableEntry.ComboBoxModel())));
                    }
                }
            }
        }
        tableModel.initData();
        //tableSorter = new TableSorter(tableModel, tableModel.getPositionInArray(99), getOrderByColumn());
        //tableSorter.initsort();
    }

    @Override
    public void refresh() {
        this.tableModel.initData();
        repaint();
    }

    @Override
    public void reInit() {
        initModel();
        repaint();
    }
}
