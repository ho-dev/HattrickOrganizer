package module.transfer.scout;

import core.db.DBManager;
import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;

public class TransferScoutingTable extends FixedColumnsTable implements Refreshable {

    private TransferScoutingTableModel m_clTableModel;

    TransferScoutingTable() {
        super(UserColumnController.instance().getTransferScoutingTableModel(), 2);
        setOpaque(false);
        m_clTableModel = (TransferScoutingTableModel)this.getModel();
        m_clTableModel.setValues(DBManager.instance().getScoutList());
        RefreshManager.instance().registerRefreshable(this);
    }
    public final TransferScoutingTableModel getTransferTableModel() {
        return m_clTableModel;
    }

    public final void reInit() {
        refresh();
    }

    public final void refresh() {
        m_clTableModel.initData();
        repaint();
    }
}
