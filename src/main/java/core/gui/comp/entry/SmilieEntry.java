// %3364174802:de.hattrickorganizer.gui.model%
package core.gui.comp.entry;

import core.gui.theme.GroupTeamFactory;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingConstants;


/**
 * Zeigt die Warnings und Verletzungen an
 */
public class SmilieEntry extends DoubleLabelEntries {
    //~ Instance fields ----------------------------------------------------------------------------

    private ColorLabelEntry manuell = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                                                          ColorLabelEntry.BG_STANDARD,
                                                          SwingConstants.RIGHT);
    private ColorLabelEntry team = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
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
        if (obj instanceof SmilieEntry) {
            final SmilieEntry entry = (SmilieEntry) obj;

            if ((entry.getPlayer() != null) && (getPlayer() != null)) {
                int ergebnis = 0;

                //Beide null -> Der ManuelleSmilie entscheidet
                if (((entry.getPlayer().getTeamGroup() == null)
                        || entry.getPlayer().getTeamGroup().equals(""))
                        && ((getPlayer().getTeamGroup() == null)
                        || getPlayer().getTeamGroup().equals(""))) {
                } else if ((entry.getPlayer().getTeamGroup() == null)
                        || entry.getPlayer().getTeamGroup().equals("")) {
                    ergebnis = 1;
                } else if ((getPlayer().getTeamGroup() == null)
                        || getPlayer().getTeamGroup().equals("")) {
                    ergebnis = -1;
                } else {
                    ergebnis = entry.getPlayer().getTeamGroup().compareTo(getPlayer()
                            .getTeamGroup());
                }

                // if equal check lineup
                if (ergebnis == 0) {
                    var team = HOVerwaltung.instance().getModel().getCurrentLineupTeam();
                    if (team == null) return 0;
                    final MatchRoleID entrySort = team.getLineup().getPositionByPlayerId(entry.getPlayer().getPlayerID());
                    final MatchRoleID sort = team.getLineup().getPositionByPlayerId(getPlayer().getPlayerID());
                    if ((sort == null) && (entrySort == null)) {
                        ergebnis = 0;
                    } else if (sort == null) {
                        ergebnis = -1;
                    } else if (entrySort == null) {
                        ergebnis = 1;
                    } else ergebnis = Integer.compare(entrySort.getSortId(), sort.getSortId());
                }
                return ergebnis;
            }
        }
        return 0;
    }

    @Override
	public final void updateComponent() {
        if (player != null) {
            if ((player.getTeamGroup() != null) && !player.getTeamGroup().equals("")) {
                team.setIcon(GroupTeamFactory.instance().getActiveGroupIcon(player.getTeamGroup()));
            } else {
                team.clear();
            }

            if ((player.getInfoSmiley() != null) && !player.getInfoSmiley().equals("")) {
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
