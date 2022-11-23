// %1126721330463:hoplugins.transfers.ui.model%
package module.transfer.history;

import core.model.HOVerwaltung;
import module.transfer.PlayerTransfer;
import java.io.Serial;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * TableModel representing the transfers for your own team.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
class TransferTableModel extends AbstractTableModel {

	@Serial
    private static final long serialVersionUID = -7041463041956883945L;
    private final List<PlayerTransfer> values;
    private final String[] colNames;

    /**
     * Creates a TransferTableModel.
     *
     * @param values List of values to show in table.
     */
    TransferTableModel(List<PlayerTransfer> values) {
        super();

        this.colNames = new String[]{
                HOVerwaltung.instance().getLanguageString("Datum"),
                HOVerwaltung.instance().getLanguageString("Season"),
                HOVerwaltung.instance().getLanguageString("Week"),
                HOVerwaltung.instance().getLanguageString("Spieler"),
                HOVerwaltung.instance().getLanguageString("ls.player.age"),
                "",
                HOVerwaltung.instance().getLanguageString("FromTo"),
                HOVerwaltung.instance().getLanguageString("Price"),
                HOVerwaltung.instance().getLanguageString("ls.player.tsi"),
                HOVerwaltung.instance().getLanguageString("ls.player.short_leadership"),
                HOVerwaltung.instance().getLanguageString("ls.player.short_experience"),
                HOVerwaltung.instance().getLanguageString("ls.player.short_form"),
                HOVerwaltung.instance().getLanguageString("ls.player.skill_short.stamina"),
                HOVerwaltung.instance().getLanguageString("ls.player.skill_short.keeper"),
                HOVerwaltung.instance().getLanguageString("ls.player.skill_short.defending"),
                HOVerwaltung.instance().getLanguageString("ls.player.skill_short.playmaking"),
                HOVerwaltung.instance().getLanguageString("ls.player.skill_short.passing"),
                HOVerwaltung.instance().getLanguageString("ls.player.skill_short.winger"),
                HOVerwaltung.instance().getLanguageString("ls.player.skill_short.scoring"),
                HOVerwaltung.instance().getLanguageString("ls.player.skill_short.setpieces")
        };

        this.values = values;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /** {@inheritDoc} */
    public final int getColumnCount() {
        return colNames.length;
    }

    /** {@inheritDoc} */
    @Override
	public final String getColumnName(int column) {
        return colNames[column];
    }

    /** {@inheritDoc} */
    public final int getRowCount() {
        return values.size();
    }

    /** {@inheritDoc} */
    public final Object getValueAt(int rowIndex, int columnIndex) {
        final PlayerTransfer transfer = values.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return transfer.getDate().toLocaleDateTime();

            case 1:
                return transfer.getSeason();

            case 2:
                return transfer.getWeek();

            case 3:

                if ((transfer.getPlayerName() != null) && (transfer.getPlayerName().length() > 0)) {
                    return transfer.getPlayerName();
                } else {
                    return "< " + HOVerwaltung.instance().getLanguageString("FiredPlayer") + " >";
                }

            case 4:

                if ((transfer.getPlayerName() != null) && (transfer.getPlayerName().length() > 0)) {
                    var player = transfer.getPlayerInfo();
                    if ( player != null ) return player.getAgeWithDaysAsString(transfer.getDate());
                    return "";
                }

            case 5:
                return transfer.getType();

            case 6:

                if (transfer.getType() == PlayerTransfer.BUY) {
                    return transfer.getSellerName();
                } else {
                    return transfer.getBuyerName();
                }

            case 7:
                return transfer.getPrice();

            case 8:
                return transfer.getTsi();

            case 9:

                if (transfer.getPlayerInfo() != null) {
                    return transfer.getPlayerInfo().getLeadership();
                } else {
                    return -1;
                }

            case 10:

                if (transfer.getPlayerInfo() != null) {
                    return transfer.getPlayerInfo().getExperience();
                } else {
                    return -1;
                }

            case 11:

                if (transfer.getPlayerInfo() != null) {
                    return transfer.getPlayerInfo().getForm();
                } else {
                    return -1;
                }

            case 12:

                if (transfer.getPlayerInfo() != null) {
                    return transfer.getPlayerInfo().getStamina();
                } else {
                    return -1;
                }

            case 13:

                if (transfer.getPlayerInfo() != null) {
                    return transfer.getPlayerInfo().getGKskill();
                } else {
                    return -1;
                }

            case 14:

                if (transfer.getPlayerInfo() != null) {
                    return transfer.getPlayerInfo().getDEFskill();
                } else {
                    return -1;
                }

            case 15:

                if (transfer.getPlayerInfo() != null) {
                    return transfer.getPlayerInfo().getPMskill();
                } else {
                    return -1;
                }

            case 16:

                if (transfer.getPlayerInfo() != null) {
                    return transfer.getPlayerInfo().getPSskill();
                } else {
                    return -1;
                }

            case 17:

                if (transfer.getPlayerInfo() != null) {
                    return transfer.getPlayerInfo().getWIskill();
                } else {
                    return -1;
                }

            case 18:

                if (transfer.getPlayerInfo() != null) {
                    return transfer.getPlayerInfo().getSCskill();
                } else {
                    return -1;
                }

            case 19:

                if (transfer.getPlayerInfo() != null) {
                    return transfer.getPlayerInfo().getSPskill();
                } else {
                    return -1;
                }

            default:
                return ""; //$NON-NLS-1$
        }
    }
}
