package module.transfer.history;

import core.gui.comp.panel.ImagePanel;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import module.transfer.PlayerTransfer;
import module.transfer.ui.sorter.DefaultTableSorter;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Pane to show transfers for your own team.
 *
 */
class TeamTransfersPane extends JPanel implements ListSelectionListener {

	private final FixedColumnsTable transferTable;
    private List<PlayerTransfer> transfers = new ArrayList<>();
    private PlayerDetailPanel playerDetailPanel;

    /**
     * Creates a new TeamTransfersPane object.
     */
    TeamTransfersPane() {
        super(new BorderLayout());

        final JPanel mainPanel = new ImagePanel();
        mainPanel.setLayout(new BorderLayout());
        setOpaque(false);
        add(mainPanel, BorderLayout.CENTER);

        var model = UserColumnController.instance().getTransferTableModel();
        transferTable = new FixedColumnsTable(model);
        model.initTable(transferTable);
        mainPanel.add(transferTable.getContainerComponent(), BorderLayout.CENTER);
//        refresh(new Vector<>());
    }

    public TransferTableModel getTransferTableModel(){
        return (TransferTableModel) transferTable.getModel();
    }

    /**
     * Creates a new TeamTransfersPane object.
     */
    public TeamTransfersPane(PlayerDetailPanel playerDetailPanel) {
        this();
        this.playerDetailPanel = playerDetailPanel;
        transferTable.getSelectionModel().addListSelectionListener(this);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Refresh the information on the panel.
     *
     * @param transfers List of transfers to show.
     */
    public final void refresh(List<PlayerTransfer> transfers) {
        this.transfers = transfers;
        var model = getTransferTableModel();
        model.setValues(transfers);
    }

    /** {@inheritDoc} */
    public final void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (transferTable.getSelectedRow() >= 0) {
                var index = transferTable.getSelectedModelIndex();
                final PlayerTransfer transfer = this.transfers.get(index);
                this.playerDetailPanel.setPlayer(transfer);
            } else {
                this.playerDetailPanel.clearPanel();
            }
        }
    }

    public void storeUserSettings(){
        var model = getTransferTableModel();
        model.storeUserSettings();
        playerDetailPanel.storeUserSettings();
    }

}
