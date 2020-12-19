package module.youth;

import core.gui.RefreshManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.model.UserColumnController;

import javax.swing.*;

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
        }
        tableModel.initData();
        //tableSorter = new TableSorter(tableModel, tableModel.getPositionInArray(99), getOrderByColumn());
        setModel(tableModel);
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
