package module.youth;

import core.gui.RefreshManager;
import core.gui.comp.table.TableSorter;
import core.gui.model.UserColumnController;
import core.gui.model.UserColumnFactory;
import core.model.HOVerwaltung;
import core.model.UserParameter;

import javax.swing.*;

public class YouthPlayerOverviewTable extends JTable implements core.gui.Refreshable {

    private YouthPlayerOverviewColumnModel tableModel;
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
        if (tableModel == null) {
            tableModel = UserColumnController.instance().getYouthPlayerOverviewColumnModel();
            tableModel.setValues(HOVerwaltung.instance().getModel().getCurrentYouthPlayers());
            tableSorter = new TableSorter(tableModel,
                    tableModel.getPositionInArray(UserColumnFactory.ID),
                    getOrderByColumn(),
                    tableModel.getPositionInArray(UserColumnFactory.NAME));
        }
    }

    private int getOrderByColumn() {
        return switch (UserParameter.instance().standardsortierung) {
            case UserParameter.SORT_NAME -> tableModel.getPositionInArray(UserColumnFactory.NAME);
            default -> tableModel.getPositionInArray(UserColumnFactory.BEST_POSITION);
        };
    }

    @Override
    public void refresh() {
        reInitModel();
        repaint();
    }

    private void reInitModel() {
        ((YouthPlayerOverviewColumnModel) this.getSorter().getModel()).reInitData();
    }

    private TableSorter getSorter() {
        return this.tableSorter;
    }
}
