package core.gui.model;


import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoppelLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.util.Helper;

import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;


/**
 * Column shows a position skill of a player
 *
 * @author Thorsten Dietz
 * @since 1.36
 */
class PlayerPositionColumn extends PlayerColumn {

    /**
     * position id
     **/
    private byte position;

    /**
     * constructor
     *
     * @param id
     * @param name
     * @param tooltip
     * @param position
     */
    protected PlayerPositionColumn(int id, String name, String tooltip, byte position) {
        super(id, name, tooltip);
        this.position = position;
    }

    /**
     * returns TableEntry
     * will not be overwrite
     */
    @Override
    public IHOTableEntry getTableEntry(Player player, Player comparePlayer) {
        return new DoppelLabelEntry(getEntryValue(player), getCompareValue(player, comparePlayer));
    }

    /**
     * returns player value
     * overwritten by created columns
     *
     * @param player
     * @return
     */
    public ColorLabelEntry getEntryValue(Player player) {
        ColorLabelEntry temp = new ColorLabelEntry(player.calcPosValue(position, true),
                getBackgroundColor(), false,
                core.model.UserParameter.instance().nbDecimals);

        byte[] alternativePosition = player.getAlternativePositions();
        for (byte altPos : alternativePosition) {
            if (altPos == position) {
                temp.setBold(true);
                break;
            }
        }

        return temp;
    }

    /**
     * return a value if comparePlayer is not null
     *
     * @param player
     * @param comparePlayer
     * @return ColorLabelEntry
     */
    public ColorLabelEntry getCompareValue(Player player, Player comparePlayer) {
        if (comparePlayer == null) {
            return new ColorLabelEntry("",
                    ColorLabelEntry.FG_STANDARD,
                    getBackgroundColor(),
                    SwingConstants.RIGHT);
        }

        return new ColorLabelEntry(player.calcPosValue(position, true)
                - comparePlayer.calcPosValue(position, true),
                getBackgroundColor(), false, false,
                core.model.UserParameter.instance().nbDecimals);

    }

    /**
     * overwrite the method from UserColumn
     */
    @Override
    public void setSize(TableColumn column) {
        final int breite = (int) (55d * (1d + ((core.model.UserParameter.instance().nbDecimals - 1) / 4.5d)));
        column.setMinWidth(25);
        column.setPreferredWidth((preferredWidth == 0) ? Helper.calcCellWidth(breite) : preferredWidth);
    }

    private Color getBackgroundColor() {
        switch (position) {
            case IMatchRoleID.KEEPER:
            case IMatchRoleID.CENTRAL_DEFENDER:
            case IMatchRoleID.BACK:
            case IMatchRoleID.MIDFIELDER:
            case IMatchRoleID.WINGER:
            case IMatchRoleID.FORWARD:
                return ColorLabelEntry.BG_PLAYERSPOSITIONVALUES;
            default:
                return ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES;
        }
    }
}
