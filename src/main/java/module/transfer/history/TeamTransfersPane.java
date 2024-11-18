package module.transfer.history;

import core.gui.comp.panel.ImagePanel;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;
import module.transfer.PlayerTransfer;
import module.transfer.TransfersPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Pane to show transfers for your own team.
 *
 */
class TeamTransfersPane extends JPanel implements ListSelectionListener {

    private final TransfersPanel transferPanel;
    private List<PlayerTransfer> transfers = new ArrayList<>();
	private final FixedColumnsTable transferTable;
    private final PlayerDetailPanel playerDetailPanel;

    /**
     * Creates a new TeamTransfersPane object.
     */
    TeamTransfersPane(TransfersPanel transfersPanel) {
        super(new BorderLayout());

        this.transferPanel = transfersPanel;

        this.playerDetailPanel = new PlayerDetailPanel();
        final JPanel mainPanel = new ImagePanel();
        mainPanel.setLayout(new BorderLayout());
        setOpaque(false);
        add(mainPanel, BorderLayout.CENTER);

        var model = UserColumnController.instance().getTransferTableModel();
        transferTable = new FixedColumnsTable(model);
        transferTable.getSelectionModel().addListSelectionListener(this);
        mainPanel.add(transferTable.getContainerComponent(), BorderLayout.CENTER);
    }

    public TransferTableModel getTransferTableModel(){
        return (TransferTableModel) transferTable.getModel();
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
                this.transferPanel.selectTransfer(transfer.getTransferId());
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

    public PlayerDetailPanel getPlayerDetailPanel() {
        return this.playerDetailPanel;
    }

    private PlayerTransfer getSelectedTransfer() {
        var viewIndex = transferTable.getSelectedRow();
        if (viewIndex > -1 && viewIndex < transfers.size()) {
            return transfers.get(transferTable.convertRowIndexToModel(viewIndex));
        }
        return null;
    }

    public void selectTransfer(int transferId) {
        var selectedTransfer = getSelectedTransfer();
        if (selectedTransfer != null && selectedTransfer.getTransferId() != transferId) {
            var modelIndex = 0;
            for (var t : transfers) {
                if (t.getTransferId() == transferId) {
                    var newViewIndex = transferTable.convertRowIndexToView(modelIndex);
                    transferTable.setRowSelectionInterval(newViewIndex, newViewIndex);
                }
                modelIndex++;
            }
        }
    }

}
