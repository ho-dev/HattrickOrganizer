// %1126721330416:hoplugins.transfers.ui.model%
package module.transfer.history;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.util.CurrencyUtils;
import core.util.HODateTime;
import module.transfer.PlayerTransfer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * TableModel representing transfers for a player.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
public class PlayerTransferTableModel extends HOTableModel {

    private List<PlayerTransfer> values;

    /**
     * Creates a PlayerTransferTableModel.
     */
    public PlayerTransferTableModel() {
        super(UserColumnController.ColumnModelId.PLAYERTRANSFER, "PlayerTransfers");
        columns = new ArrayList<>(List.of(
                new TransferTableColumn("Datum") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(HODateTime.toEpochSecond(transfer.getDate()), transfer.getDate().toLocaleDate(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn("Season") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(transfer.getSeason(), "" + transfer.getSeason(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn("Week") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(transfer.getWeek(), "" + transfer.getWeek(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn("Buyer") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(transfer.getBuyerName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn("Type") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(PlayerTransfer.BUY, String.valueOf(PlayerTransfer.BUY), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn("Seller") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(transfer.getSellerName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn("Price") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(CurrencyUtils.convertCurrency(transfer.getPrice()), ColorLabelEntry.BG_STANDARD, true, 0);
                    }
                },
                new TransferTableColumn("ls.player.tsi") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(transfer.getTsi(), String.valueOf(transfer.getTsi()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn("ls.player.age") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        String text = "";
                        double sortValue = -1;
                        if ((transfer.getPlayerName() != null) && (!transfer.getPlayerName().isEmpty())) {
                            var player = transfer.getPlayerInfo();
                            if (player != null) {
                                var age = player.getAgeAtDate(transfer.getDate());
                                if (age != null) {
                                    text = age.toString();
                                    sortValue = age.toDouble();
                                }
                            }
                        }
                        return new ColorLabelEntry(sortValue, text, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn("ls.transfer.motherclubfee") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(CurrencyUtils.convertCurrency(transfer.getMotherClubFee()), ColorLabelEntry.BG_STANDARD, true, 0);
                    }
                },
                new TransferTableColumn("ls.transfer.previousclubfee") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(CurrencyUtils.convertCurrency(transfer.getPreviousClubFee()), ColorLabelEntry.BG_STANDARD, true, 0);
                    }
                }
        )).toArray(new TransferTableColumn[0]);
    }

    public void setValues(List<PlayerTransfer> values){
        this.values = values;
        initData();
    }

//
//    //~ Methods ------------------------------------------------------------------------------------
//
//    /** {@inheritDoc} */
//    public final int getColumnCount() {
//        return colNames.length;
//    }
//
//    /** {@inheritDoc} */
//    @Override
//	public final String getColumnName(int column) {
//        return colNames[column];
//    }
//
//    @Override
    protected void initData() {
        UserColumn[] displayedColumns = getDisplayedColumns();
        m_clData = new Object[values.size()][columns.length];
        int playernum = 0;
        for (var value : values) {
            int columnnum = 0;
            for (var col : displayedColumns) {
                m_clData[playernum][columnnum] = ((TransferTableColumn) col).getTableEntry(value);
                columnnum++;
            }
            playernum++;
        }
        fireTableDataChanged();
    }
//
//    /** {@inheritDoc} */
//    public final int getRowCount() {
//        return values.size();
//    }
//
//    /** {@inheritDoc} */
//	public boolean isCellEditable(int row, int col) {
//        return col == 11;
//    }
//
//    /** {@inheritDoc} */
//    public final Object getValueAt(int rowIndex, int columnIndex) {
//        final PlayerTransfer transfer = values.get(rowIndex);
//        var player = PlayerRetriever.getPlayer(transfer.getPlayerId());
//        return switch (columnIndex) {
//            case 0 -> transfer.getDate().toLocaleDateTime();
//            case 1 -> transfer.getSeason();
//            case 2 -> transfer.getWeek();
//            case 3 -> transfer.getBuyerName();
//            case 4 -> PlayerTransfer.BUY;
//            case 5 -> transfer.getSellerName();
//            case 6 -> transfer.getPrice();
//            case 7 -> transfer.getTsi();
//            case 8 -> player!=null?player.getAgeWithDaysAsString(transfer.getDate()):"";
//            case 9 -> transfer.getMotherClubFee();
//            case 10 -> transfer.getPreviousClubFee();
//            case 11 -> new JButton(HOVerwaltung.instance().getLanguageString("ls.button.delete"));
//            default -> ""; //$NON-NLS-1$
//        };
//    }
}