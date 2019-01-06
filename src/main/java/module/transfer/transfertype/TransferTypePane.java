// %1126721330823:hoplugins.transfers.ui%
package module.transfer.transfertype;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.Spieler;
import module.training.ui.comp.DividerListener;
import module.transfer.PlayerRetriever;
import module.transfer.PlayerTransfer;
import module.transfer.TransferTypes;
import module.transfer.history.PlayerDetailPanel;
import module.transfer.ui.layout.TableLayout;
import module.transfer.ui.layout.TableLayoutConstants;
import module.transfer.ui.layout.TableLayoutConstraints;
import module.transfer.ui.sorter.DefaultTableSorter;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
    //~ Static fields/initializers -----------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = 4235843964542482924L;

	private static final NumberFormat FORMAT = NumberFormat.getIntegerInstance();

    //~ Instance fields ----------------------------------------------------------------------------

    private JPanel sidePanel = new ImagePanel();
    private JTable transferTable;
    private List<TransferredPlayer> transferred = new ArrayList<TransferredPlayer>();
    private PlayerDetailPanel playerDetailPanel = new PlayerDetailPanel();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a TransferTypePane.
     */
    public TransferTypePane() {
        super(JSplitPane.VERTICAL_SPLIT);

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

        final TableModel model = new TransferTypeTableModel(new ArrayList<TransferredPlayer>());
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

        refresh(new Vector<PlayerTransfer>());
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Refreshes the information on the panel.
     *
     * @param transfers List of transfers to show.
     */
    public final void refresh(List<PlayerTransfer> transfers) {
        final Map<String,TransferredPlayer> players = new LinkedHashMap<String,TransferredPlayer>();
        final List<PlayerTransfer> wasted = new ArrayList<PlayerTransfer>();

        for (final Iterator<PlayerTransfer> iter = transfers.iterator(); iter.hasNext();) {
            final PlayerTransfer pt = iter.next();
            final int id = pt.getPlayerId();

            if (id == 0) {
                wasted.add(pt);
            } else {
                TransferredPlayer tt = players.get(pt.getPlayerId() + "");

                if (tt == null) {
                    Spieler player = PlayerRetriever.getPlayer(pt.getPlayerId());

                    if (player != null) {
                        tt = new TransferredPlayer(player);
                    } else {
                        tt = new TransferredPlayer(pt);
                    }

                    players.put("" + tt.getPlayerId(), tt);
                }

                if (tt != null) {
                    tt.addTransfer(pt);
                }
            }
        }

        transferred = new ArrayList<TransferredPlayer>(players.values());

        final TransferRecap recap = new TransferRecap();

        for (final Iterator<PlayerTransfer> iter = wasted.iterator(); iter.hasNext();) {
            final PlayerTransfer element = iter.next();
            recap.addWastedOperation(element);
        }

        for (final Iterator<TransferredPlayer> iter = transferred.iterator(); iter.hasNext();) {
            final TransferredPlayer element = iter.next();
            recap.addTradingOperation(element);
        }

        this.sidePanel.removeAll();

        final double[][] sizes = {
                               {10, 185, 10, 90, TableLayoutConstants.FILL, 10},
                               {10, TableLayoutConstants.PREFERRED, 10}
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

        final JLabel type = new JLabel(HOVerwaltung.instance().getLanguageString("Type"));
        type.setFont(new Font(type.getFont().getName(), Font.BOLD, type.getFont().getSize()));

        this.sidePanel.add(type, c);

        c.col1 = 3;
        c.col2 = c.col1;
        c.hAlign = TableLayoutConstants.LEFT;

        final JLabel income = new JLabel(HOVerwaltung.instance().getLanguageString("Income"));
        income.setFont(new Font(income.getFont().getName(), Font.BOLD, income.getFont().getSize()));
        this.sidePanel.add(income, c);

        int row = 2;
        int totalIncome = 0;

        for (int i = -1; i < TransferTypes.NUMBER; i++) {
            final TransferTypeRecap ttc = recap.getRecap(i);

            if ((ttc != null) && (ttc.getNumber() > 0)) {
                tLayout.insertRow(++row, TableLayoutConstants.PREFERRED);
                c.row1 = row;
                c.row2 = c.row1;

                c.col1 = 1;
                c.col2 = c.col1;
                c.hAlign = TableLayoutConstants.LEFT;
                this.sidePanel.add(new JLabel(TransferTypes.getTransferDesc(i) + " ("
                                              + Integer.toString(ttc.getNumber()) + ")"), c);

                c.col1 = 2;
                c.col2 = c.col1;
                c.hAlign = TableLayoutConstants.CENTER;
                this.sidePanel.add(new JLabel(HOVerwaltung.instance().getModel().getXtraDaten().getCurrencyName()),
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

        final JLabel total = new JLabel(HOVerwaltung.instance().getLanguageString("Total"));
        total.setFont(new Font(total.getFont().getName(), Font.BOLD, total.getFont().getSize()));
        this.sidePanel.add(total, c);

        c.col1 = 2;
        c.col2 = c.col1;
        c.hAlign = TableLayoutConstants.CENTER;
        this.sidePanel.add(new JLabel(HOVerwaltung.instance().getModel().getXtraDaten().getCurrencyName()), c);

        c.col1 = 3;
        c.col2 = c.col1;
        c.hAlign = TableLayoutConstants.RIGHT;
        this.sidePanel.add(new JLabel(FORMAT.format(totalIncome)), c);

        final DefaultTableSorter sorter = (DefaultTableSorter) transferTable.getModel();
        sorter.setTableModel(new TransferTypeTableModel(transferred));

        final JComboBox comboBox = new JComboBox();
        for (int i = -1; i < TransferTypes.NUMBER; i++) {
        	comboBox.addItem(TransferTypes.getTransferDesc(i));
        }
        final TableColumn column = transferTable.getColumnModel().getColumn(2);
        column.setCellEditor(new DefaultCellEditor(comboBox));
        column.setPreferredWidth(120);

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

    /** {@inheritDoc} */
    public final void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            final DefaultTableSorter sorter = (DefaultTableSorter) transferTable.getModel();

            if (transferTable.getSelectedRow() >= 0) {
                final int index = sorter.modelIndex(transferTable.getSelectedRow());
                final TransferredPlayer transfer = (TransferredPlayer) this.transferred.get(index);
                this.playerDetailPanel.setPlayer(transfer.getPlayerId(), transfer.getPlayerName());
            } else {
                this.playerDetailPanel.clearPanel();
            }
        }
    }
}
