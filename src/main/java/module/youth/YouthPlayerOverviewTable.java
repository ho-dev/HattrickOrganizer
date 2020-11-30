package module.youth;

import core.gui.RefreshManager;
import core.gui.comp.table.TableSorter;
import core.gui.model.UserColumnController;
import core.model.UserParameter;

import javax.swing.*;

public class YouthPlayerOverviewTable extends JTable implements core.gui.Refreshable {

    private YouthPlayerOverviewTableModel tableModel;
    private TableSorter tableSorter;

    public YouthPlayerOverviewTable() {
        super();
        initModel();
        RefreshManager.instance().registerRefreshable(this);
    }

    @Override
    public void reInit() {
        initModel();
        repaint();
    }

    private void initModel() {
        setOpaque(false);
        if (tableModel == null) {
            tableModel = UserColumnController.instance().getYouthPlayerOverviewColumnModel();
            tableModel.initData();
            tableSorter = new TableSorter(tableModel,
                    tableModel.getPositionInArray(99),
                    getOrderByColumn());
            setModel(tableSorter);
        }

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowSelectionAllowed(true);
        tableSorter.initsort();
    }

    private int getOrderByColumn() {
        return switch (UserParameter.instance().standardsortierung) {
            case UserParameter.SORT_NAME -> tableModel.getPositionInArray(0);
            default -> tableModel.getPositionInArray(0);
        };
    }

    @Override
    public void refresh() {
        ((YouthPlayerOverviewTableModel) this.getSorter().getModel()).initData();
        repaint();
    }

    private TableSorter getSorter() {
        return this.tableSorter;
    }
}
