// %1126721330463:hoplugins.transfers.ui.model%
package module.transfer.history;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.CurrencyUtils;
import core.util.HODateTime;
import module.transfer.PlayerTransfer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * TableModel representing the transfers for your own team.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
public class TransferTableModel extends HOTableModel {

    private List<PlayerTransfer> values = new ArrayList<>();

    /**
     * Creates a TransferTableModel.
     *
     */
    public TransferTableModel() {
        super(UserColumnController.ColumnModelId.TEAMTRANSFER, "TeamTransfers");
        int id = 0;
        columns = new ArrayList<>(List.of(
                new TransferTableColumn(id++,"Datum") {
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
                new TransferTableColumn(id++,"Spieler") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        String text;
                        if ((transfer.getPlayerName() != null) && (!transfer.getPlayerName().isEmpty())) {
                            text = transfer.getPlayerName();
                        } else {
                            text = "< " + HOVerwaltung.instance().getLanguageString("FiredPlayer") + " >";
                        }

                        return new ColorLabelEntry(text, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
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
                        return new ColorLabelEntry(sortValue, text, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new TransferTableColumn(id++,"Type") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return new ColorLabelEntry(transfer.getType(), String.valueOf(transfer.getType()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TransferTableColumn(id++,"FromTo") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        String text;
                        if (transfer.getType() == PlayerTransfer.BUY) {
                            text = transfer.getSellerName();
                        } else {
                            text = transfer.getBuyerName();
                        }
                        return new ColorLabelEntry(text, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
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
                        return new ColorLabelEntry(transfer.getTsi(), String.valueOf(transfer.getTsi()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new TransferTableColumn(id++,"ls.player.short_leadership") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return getPlayerInfoTableEntry(transfer);
                    }

                    @Override
                    public IHOTableEntry getTableEntry(Player playerInfo) {
                        return createPlayerInfoLabelEntry(playerInfo.getLeadership());
                    }
                },
                new TransferTableColumn(id++,"ls.player.short_experience") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return getPlayerInfoTableEntry(transfer);
                    }

                    @Override
                    public IHOTableEntry getTableEntry(Player playerInfo) {
                        return createPlayerInfoLabelEntry(playerInfo.getExperience());
                    }
                },
                new TransferTableColumn(id++,"ls.player.short_form") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return getPlayerInfoTableEntry(transfer);
                    }

                    @Override
                    public IHOTableEntry getTableEntry(Player playerInfo) {
                        return createPlayerInfoLabelEntry(playerInfo.getForm());
                    }
                },
                new TransferTableColumn(id++,"ls.player.skill_short.stamina") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return getPlayerInfoTableEntry(transfer);
                    }

                    @Override
                    public IHOTableEntry getTableEntry(Player playerInfo) {
                        return createPlayerInfoLabelEntry(playerInfo.getStamina());
                    }
                },
                new TransferTableColumn(id++,"ls.player.skill_short.keeper") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return getPlayerInfoTableEntry(transfer);
                    }

                    @Override
                    public IHOTableEntry getTableEntry(Player playerInfo) {
                        return createPlayerInfoLabelEntry(playerInfo.getGKskill());
                    }
                },
                new TransferTableColumn(id++,"ls.player.skill_short.defending") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return getPlayerInfoTableEntry(transfer);
                    }

                    @Override
                    public IHOTableEntry getTableEntry(Player playerInfo) {
                        return createPlayerInfoLabelEntry(playerInfo.getDEFskill());
                    }
                },
                new TransferTableColumn(id++,"ls.player.skill_short.playmaking") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return getPlayerInfoTableEntry(transfer);
                    }

                    @Override
                    public IHOTableEntry getTableEntry(Player playerInfo) {
                        return createPlayerInfoLabelEntry(playerInfo.getPMskill());
                    }
                },
                new TransferTableColumn(id++,"ls.player.skill_short.passing") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return getPlayerInfoTableEntry(transfer);
                    }

                    @Override
                    public IHOTableEntry getTableEntry(Player playerInfo) {
                        return createPlayerInfoLabelEntry(playerInfo.getPSskill());
                    }
                },
                new TransferTableColumn(id++,"ls.player.skill_short.winger") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return getPlayerInfoTableEntry(transfer);
                    }

                    @Override
                    public IHOTableEntry getTableEntry(Player playerInfo) {
                        return createPlayerInfoLabelEntry(playerInfo.getWIskill());
                    }
                },
                new TransferTableColumn(id++,"ls.player.skill_short.scoring") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return getPlayerInfoTableEntry(transfer);
                    }

                    @Override
                    public IHOTableEntry getTableEntry(Player playerInfo) {
                        return createPlayerInfoLabelEntry(playerInfo.getSCskill());
                    }
                },
                new TransferTableColumn(id++,"ls.player.skill_short.setpieces") {
                    @Override
                    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
                        return getPlayerInfoTableEntry(transfer);
                    }

                    @Override
                    public IHOTableEntry getTableEntry(Player playerInfo) {
                        return  createPlayerInfoLabelEntry(playerInfo.getSPskill());
                    }
                }
        )).toArray(new TransferTableColumn[0]);
    }

    private ColorLabelEntry createPlayerInfoLabelEntry(int value) {
        String text;
        if ( value >= 0 ) text =  String.valueOf(value);
        else text = "--";
        return new ColorLabelEntry(value, text, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
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
