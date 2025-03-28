// %1126721330823:hoplugins.transfers.ui%
package module.transfer.transfertype;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.player.Player;
import core.util.CurrencyUtils;
import module.training.ui.comp.DividerListener;
import module.transfer.PlayerRetriever;
import module.transfer.PlayerTransfer;
import module.transfer.TransferType;
import module.transfer.TransfersPanel;
import module.transfer.history.PlayerDetailPanel;
import module.transfer.ui.layout.TableLayout;
import module.transfer.ui.layout.TableLayoutConstants;
import module.transfer.ui.layout.TableLayoutConstraints;
import module.transfer.ui.sorter.DefaultTableSorter;
import java.awt.BorderLayout;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;


/**
 * Pane to show transfer type recp information.
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class TransferTypePane extends JSplitPane implements ListSelectionListener,
                                                            TableModelListener {

    private static final NumberFormat FORMAT = NumberFormat.getIntegerInstance();

    private final JPanel sidePanel = new ImagePanel();
    private final JTable transferTable;
    private final TransfersPanel transfersPanel;
    private List<TransferredPlayer> transferred = new ArrayList<>();
    private final PlayerDetailPanel playerDetailPanel = new PlayerDetailPanel();


    /**
     * Creates a TransferTypePane.
     */
    public TransferTypePane(TransfersPanel transfersPanel) {
        super(JSplitPane.VERTICAL_SPLIT);

        this.transfersPanel = transfersPanel;

        FORMAT.setGroupingUsed(true);
        FORMAT.setMaximumFractionDigits(0);

        // Create side panel
        sidePanel.setOpaque(false);

        final JScrollPane sidePane = new JScrollPane(sidePanel);
        sidePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidePane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Create the top panel and add it to the split pane
        final JPanel topPanel = new ImagePanel();
        topPanel.setLayout(new BorderLayout());

        final TableModel model = new TransferTypeTableModel(new ArrayList<>());
        final TransferTypeSorter sorter = new TransferTypeSorter(model);
        transferTable = new JTable(sorter);

        transferTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transferTable.getSelectionModel().addListSelectionListener(this);

        sorter.setTableHeader(transferTable.getTableHeader());

        final JScrollPane transferPane = new JScrollPane(transferTable);
        transferPane.setOpaque(false);

        topPanel.add(transferPane, BorderLayout.CENTER);
        topPanel.add(sidePane, BorderLayout.WEST);

        setDividerLocation(UserParameter.instance().transferTypePane_splitPane); //$NON-NLS-1$
        addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
                new DividerListener(DividerListener.transferTypePane_splitPane)); //$NON-NLS-1$

        setLeftComponent(topPanel);
        setRightComponent(playerDetailPanel);

        refresh(new Vector<>());
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Refreshes the information on the panel.
     *
     * @param transfers List of transfers to show.
     */
    public final void refresh(List<PlayerTransfer> transfers) {
        final Map<String, TransferredPlayer> players = new LinkedHashMap<>();
        final List<PlayerTransfer> wasted = new ArrayList<>();

        for (final PlayerTransfer pt : transfers) {
            final int id = pt.getPlayerId();

            if (id == 0) {
                wasted.add(pt);
            } else {
                TransferredPlayer tt = players.get(pt.getPlayerId() + "");

                if (tt == null) {
                    Player player = PlayerRetriever.getPlayer(pt.getPlayerId());

                    if (player != null) {
                        tt = new TransferredPlayer(pt.getTransferId(), player);
                    } else {
                        tt = new TransferredPlayer(pt);
                    }

                    players.put("" + tt.getPlayerId(), tt);
                }

                tt.addTransfer(pt);
            }
        }

        transferred = new ArrayList<>(players.values());

        final TransferRecap recap = new TransferRecap();

        for (final PlayerTransfer element : wasted) {
            recap.addWastedOperation(element);
        }

        for (final TransferredPlayer element : transferred) {
            recap.addTradingOperation(element);
        }

        this.sidePanel.removeAll();

        var fontSize = UserParameter.instance().fontSize;
        final double[][] sizes = {
                {fontSize, 15*fontSize, fontSize, 8*fontSize, TableLayoutConstants.FILL, fontSize},
                {fontSize, TableLayoutConstants.PREFERRED, fontSize}
        };
        final TableLayout tLayout = new TableLayout(sizes);
        this.sidePanel.setLayout(tLayout);

        final TableLayoutConstraints c = new TableLayoutConstraints();
        c.vAlign = TableLayoutConstants.CENTER;

        c.row1 = 1;
        c.row2 = c.row1;

        c.col1 = 1;
        c.col2 = c.col1;
        c.hAlign = TableLayoutConstants.LEFT;

        final JLabel type = new JLabel(TranslationFacility.tr("Type"));
        type.setFont(new Font(type.getFont().getName(), Font.BOLD, type.getFont().getSize()));

        this.sidePanel.add(type, c);

        c.col1 = 3;
        c.col2 = c.col1;
        c.hAlign = TableLayoutConstants.LEFT;

        final JLabel income = new JLabel(TranslationFacility.tr("Income"));
        income.setFont(new Font(income.getFont().getName(), Font.BOLD, income.getFont().getSize()));
        this.sidePanel.add(income, c);

        int row = 2;
        int totalIncome = 0;

        for (int i = -1; i < TransferType.NUMBER; i++) {
            final TransferTypeRecap ttc = recap.getRecap(i);

            if (ttc.getNumber() > 0) {
                tLayout.insertRow(++row, TableLayoutConstants.PREFERRED);
                c.row1 = row;
                c.row2 = c.row1;

                c.col1 = 1;
                c.col2 = c.col1;
                c.hAlign = TableLayoutConstants.LEFT;
                this.sidePanel.add(new JLabel(TransferType.getTransferDesc(i) + " ("
                        + ttc.getNumber() + ")"), c);

                c.col1 = 2;
                c.col2 = c.col1;
                c.hAlign = TableLayoutConstants.CENTER;
                this.sidePanel.add(new JLabel(CurrencyUtils.CURRENCYSYMBOL),
                        c);

                c.col1 = 3;
                c.col2 = c.col1;
                c.hAlign = TableLayoutConstants.RIGHT;
                this.sidePanel.add(new JLabel(FORMAT.format(ttc.getNetIncome())), c);

                totalIncome += ttc.getNetIncome();
            }
        }

        row++;
        tLayout.insertRow(row, 10);

        row++;
        tLayout.insertRow(row, TableLayoutConstants.PREFERRED);
        c.row1 = row;
        c.row2 = c.row1;

        c.col1 = 1;
        c.col2 = c.col1;
        c.hAlign = TableLayoutConstants.CENTER;

        final JLabel total = new JLabel(TranslationFacility.tr("Total"));
        total.setFont(new Font(total.getFont().getName(), Font.BOLD, total.getFont().getSize()));
        this.sidePanel.add(total, c);

        c.col1 = 2;
        c.col2 = c.col1;
        c.hAlign = TableLayoutConstants.CENTER;
        this.sidePanel.add(new JLabel(CurrencyUtils.CURRENCYSYMBOL), c);

        c.col1 = 3;
        c.col2 = c.col1;
        c.hAlign = TableLayoutConstants.RIGHT;
        this.sidePanel.add(new JLabel(FORMAT.format(totalIncome)), c);

        final DefaultTableSorter sorter = (DefaultTableSorter) transferTable.getModel();
        sorter.setTableModel(new TransferTypeTableModel(transferred));

        final JComboBox comboBox = new JComboBox();
        for (int i = -1; i < TransferType.NUMBER; i++) {
            comboBox.addItem(TransferType.getTransferDesc(i));
        }
        final TableColumn column = transferTable.getColumnModel().getColumn(2);
        column.setCellEditor(new DefaultCellEditor(comboBox));
        column.setPreferredWidth(10*fontSize);

        sorter.getTableModel().addTableModelListener(this);
        updateUI();
    }

    /**
     * Executed when table has changed.
     *
     * @param e TableModelEvent
     */
    public final void tableChanged(TableModelEvent e) {
        refresh(DBManager.instance().getTransfers(0, true, true));
    }

    /**
     * {@inheritDoc}
     */
    public final void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            final DefaultTableSorter sorter = (DefaultTableSorter) transferTable.getModel();

            if (transferTable.getSelectedRow() >= 0) {
                final int index = sorter.modelIndex(transferTable.getSelectedRow());
                final TransferredPlayer transfer = this.transferred.get(index);
                this.playerDetailPanel.setPlayer(transfer.getPlayerId(), transfer.getPlayerName());
                this.transfersPanel.selectTransfer(transfer.getTransferId());
            } else {
                this.playerDetailPanel.clearPanel();
            }
        }
    }

    private TransferredPlayer getSelectedTransfer() {
        var viewIndex = transferTable.getSelectedRow();
        if (viewIndex > -1 && viewIndex < transferred.size()) {
            return transferred.get(transferTable.convertRowIndexToModel(viewIndex));
        }
        return null;
    }

    public void selectTransfer(int transferId) {
        var selectedTransfer = getSelectedTransfer();
        if (selectedTransfer != null && selectedTransfer.getTransferId() != transferId) {
            var modelIndex = 0;
            for (var t : transferred) {
                if (t.getTransferId() == transferId) {
                    var newViewIndex = transferTable.convertRowIndexToView(modelIndex);
                    transferTable.setRowSelectionInterval(newViewIndex, newViewIndex);
                }
                modelIndex++;
            }
        }
    }
}
