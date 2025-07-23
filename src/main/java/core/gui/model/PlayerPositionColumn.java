package core.gui.model;


import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoubleLabelEntries;
import core.gui.comp.entry.IHOTableEntry;
import core.model.HOVerwaltung;
import core.model.UserParameter;
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
public class PlayerPositionColumn extends PlayerColumn {

    /**
     * position id
     **/
    private final byte position;

    /**
     * constructor
     *
     * @param id        User column identifier
     * @param name      Column header string
     * @param tooltip   Column tool tip
     * @param position  Lineup position code
     */
    protected PlayerPositionColumn(int id, String name, String tooltip, byte position) {
        super(id, name, tooltip);
        this.position = position;
        this.preferredWidth = UserParameter.instance().fontSize * 5;
    }

    /**
     * returns TableEntry
     * will not be overridden
     */
    @Override
    public IHOTableEntry getTableEntry(Player player, Player comparePlayer) {
        return new DoubleLabelEntries(getEntryValue(player), getCompareValue(player, comparePlayer));
    }

    /**
     * Returns player value
     * overwritten by created columns
     *
     * @param player Player
     * @return ColorLabelEntry
     */
    public ColorLabelEntry getEntryValue(Player player) {
        var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
        var r = ratingPredictionModel.getPlayerMatchAverageRating(player, position);
        ColorLabelEntry temp = new ColorLabelEntry(r, getBackgroundColor(), core.model.UserParameter.instance().nbDecimals);
        var alternativePosition = player.getAlternativeBestPositions();
        for (byte altPos : alternativePosition) {
            if (altPos == position) {
                temp.setBold(true);
                break;
            }
        }

        return temp;
    }

    /**
     * Return a value if comparePlayer is not null
     *
     * @param player         Player
     * @param comparePlayer  Player to compare with
     * @return ColorLabelEntry
     */
    public ColorLabelEntry getCompareValue(Player player, Player comparePlayer) {
        if (comparePlayer == null) {
            return new ColorLabelEntry("",
                    ColorLabelEntry.FG_STANDARD,
                    getBackgroundColor(),
                    SwingConstants.RIGHT);
        }
        var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
        var playerRating = ratingPredictionModel.getPlayerMatchAverageRating(player, position);
        var comparePlayerRating = ratingPredictionModel.getPlayerMatchAverageRating(comparePlayer, position);
        return new ColorLabelEntry((float) (playerRating - comparePlayerRating),
                getBackgroundColor(), false, core.model.UserParameter.instance().nbDecimals);

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
        return switch (position) {
            case IMatchRoleID.KEEPER, IMatchRoleID.CENTRAL_DEFENDER, IMatchRoleID.BACK, IMatchRoleID.MIDFIELDER, IMatchRoleID.WINGER, IMatchRoleID.FORWARD ->
                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES;
            default -> ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES;
        };
    }
}
