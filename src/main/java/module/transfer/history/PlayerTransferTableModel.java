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
        int id = 0;
        columns = new ArrayList<>(List.of(
                new TransferTableColumn(id++, "Datum") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(HODateTime.toEpochSecond(transfer.getDate()), transfer.getDate().toLocaleDateTime(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn(id++,"Season") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(transfer.getSeason(), "" + transfer.getSeason(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn(id++,"Week") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(transfer.getWeek(), "" + transfer.getWeek(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn(id++,"Buyer") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(transfer.getBuyerName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn(id++,"Type") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(PlayerTransfer.BUY, String.valueOf(PlayerTransfer.BUY), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn(id++,"Seller") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(transfer.getSellerName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn(id++,"Price") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(CurrencyUtils.convertCurrency(transfer.getPrice()), ColorLabelEntry.BG_STANDARD, true, 0);
                    }
                },
                new TransferTableColumn(id++,"ls.player.tsi") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(transfer.getTsi(), String.valueOf(transfer.getTsi()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn(id++,"ls.player.age") {
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
                new TransferTableColumn(id++,"ls.transfer.motherclubfee") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(CurrencyUtils.convertCurrency(transfer.getMotherClubFee()), ColorLabelEntry.BG_STANDARD, true, 0);
                    }
                },
                new TransferTableColumn(id++,"ls.transfer.previousclubfee") {
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

    @Override
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
}