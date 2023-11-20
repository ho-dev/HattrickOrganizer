package module.transfer.history;

import core.gui.comp.panel.ImagePanel;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
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

	private final JTable transferTable;
    private List<PlayerTransfer> transfers = new ArrayList<>();
    private PlayerDetailPanel playerDetailPanel;
    private final ColorCellRenderer greenColumn = new ColorCellRenderer(ColorCellRenderer.GREEN);
    private final ColorCellRenderer yellowColumn = new ColorCellRenderer(ColorCellRenderer.YELLOW);


    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TeamTransfersPane object.
     */
    TeamTransfersPane() {
        super(new BorderLayout());

        final JPanel mainPanel = new ImagePanel();
        mainPanel.setLayout(new BorderLayout());
        setOpaque(false);
        add(mainPanel, BorderLayout.CENTER);

        var model = getTransferTableModel();
        final var sorter = new DefaultTableSorter(model);
        transferTable = new JTable(sorter);
        transferTable.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
        transferTable.setOpaque(false);

        sorter.setTableHeader(transferTable.getTableHeader());

        final JScrollPane pane = new JScrollPane(transferTable);
        pane.setOpaque(false);
        mainPanel.add(pane, BorderLayout.CENTER);

        model.restoreUserSettings(transferTable);
        refresh(new Vector<>());
    }

    public static TransferTableModel getTransferTableModel(){
        return UserColumnController.instance().getTransferTableModel();
    }

    /**
     * Creates a new TeamTransfersPane object.
     */
    public TeamTransfersPane(PlayerDetailPanel playerDetailPanel) {
        this();
        this.playerDetailPanel = playerDetailPanel;

        transferTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        transferTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transferTable.getSelectionModel().addListSelectionListener(this);
        transferTable.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Refresh the information on the panel.
     *
     * @param transfers List of transfers to show.
     */
    public final void refresh(List<PlayerTransfer> transfers) {
        this.transfers = transfers;
        transferTable.getColumnModel().getColumn(5).setCellRenderer(new IconCellRenderer());
        transferTable.getColumnModel().getColumn(5).setMaxWidth(36);
        transferTable.getColumnModel().getColumn(9).setCellRenderer(greenColumn);
        transferTable.getColumnModel().getColumn(10).setCellRenderer(greenColumn);
        transferTable.getColumnModel().getColumn(11).setCellRenderer(greenColumn);
        transferTable.getColumnModel().getColumn(12).setCellRenderer(yellowColumn);
        transferTable.getColumnModel().getColumn(13).setCellRenderer(yellowColumn);
        transferTable.getColumnModel().getColumn(14).setCellRenderer(yellowColumn);
        transferTable.getColumnModel().getColumn(15).setCellRenderer(yellowColumn);
        transferTable.getColumnModel().getColumn(16).setCellRenderer(yellowColumn);
        transferTable.getColumnModel().getColumn(17).setCellRenderer(yellowColumn);
        transferTable.getColumnModel().getColumn(18).setCellRenderer(yellowColumn);
        transferTable.getColumnModel().getColumn(19).setCellRenderer(yellowColumn);
        var model = getTransferTableModel();
        model.setValues(transfers);
    }

    /** {@inheritDoc} */
    public final void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            final DefaultTableSorter sorter = (DefaultTableSorter) transferTable.getModel();

            if (transferTable.getSelectedRow() >= 0) {
                final int index = sorter.modelIndex(transferTable.getSelectedRow());
                final PlayerTransfer transfer = this.transfers.get(index);
                this.playerDetailPanel.setPlayer(transfer);
            } else {
                this.playerDetailPanel.clearPanel();
            }
        }
    }

    public void storeUserSettings(){
        var model = getTransferTableModel();
        model.storeUserSettings(this.transferTable);
        playerDetailPanel.storeUserSettings();
    }

}
