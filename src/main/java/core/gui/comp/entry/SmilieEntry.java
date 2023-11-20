// %3364174802:de.hattrickorganizer.gui.model%
package core.gui.comp.entry;

import core.gui.theme.GroupTeamFactory;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingConstants;


/**
 * Zeigt die Warnings und Verletzungen an
 */
public class SmilieEntry extends DoubleLabelEntries {
    //~ Instance fields ----------------------------------------------------------------------------

    private final ColorLabelEntry manuell = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                                                          ColorLabelEntry.BG_STANDARD,
                                                          SwingConstants.RIGHT);
    private final ColorLabelEntry team = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                                                       ColorLabelEntry.BG_STANDARD,
                                                       SwingConstants.LEFT);
    private Player player;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SmilieEntry object.
     */
    public SmilieEntry() {
        super();
        this.setLabels(team, manuell);
    }

   public final void setPlayer(Player player) {
        this.player = player;
        updateComponent();
    }

    public final Player getPlayer() {
        return player;
    }

    @Override
	public final int compareTo(@NotNull IHOTableEntry obj) {
        if (obj instanceof SmilieEntry entry) {

            if ((entry.getPlayer() != null) && (getPlayer() != null)) {
                int result = 0;
                var thisGroup = this.getPlayer().getTeamGroup();
                var entryGroup = entry.getPlayer().getTeamGroup();
                if (!StringUtils.isEmpty(thisGroup)){
                    if ( !StringUtils.isEmpty(entryGroup )){
                        result = thisGroup.compareTo(entryGroup);
                    }
                    else {
                        return -1;
                    }
                }
                else if ( !StringUtils.isEmpty(entryGroup )){
                    return 1;
                }

                // if equal check lineup
                if (result == 0) {
                    var team = HOVerwaltung.instance().getModel().getCurrentLineupTeam();
                    final MatchRoleID entrySort = team.getLineup().getPositionByPlayerId(entry.getPlayer().getPlayerID());
                    final MatchRoleID sort = team.getLineup().getPositionByPlayerId(getPlayer().getPlayerID());
                    if (sort != null) {
                        if (entrySort != null) {
                            result = Integer.compare(entrySort.getSortId(), sort.getSortId()); // inverse direction (Keeper is top)
                        } else {
                            return 1;
                        }
                    } else if (entrySort != null) {
                        return -1;
                    }
                }
                return result;
            }
        }
        return 0;
    }

    @Override
	public final void updateComponent() {
        if (player != null) {
            if ((player.getTeamGroup() != null) && !player.getTeamGroup().isEmpty()) {
                team.setIcon(GroupTeamFactory.instance().getActiveGroupIcon(player.getTeamGroup()));
            } else {
                team.clear();
            }

            if ((player.getInfoSmiley() != null) && !player.getInfoSmiley().isEmpty()) {
                manuell.setIcon(ImageUtilities.getSmileyIcon(player.getInfoSmiley()));
            } else {
                manuell.clear();
            }
        } else {
            team.clear();
            manuell.clear();
        }
    }
}
