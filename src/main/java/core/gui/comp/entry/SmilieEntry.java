// %3364174802:de.hattrickorganizer.gui.model%
package core.gui.comp.entry;

import core.gui.theme.GroupTeamFactory;
import core.gui.theme.ImageUtilities;
import core.model.player.MatchRoleID;
import core.model.player.Player;
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
	public final int compareTo(IHOTableEntry obj) {
        if (obj instanceof SmilieEntry) {
            final SmilieEntry entry = (SmilieEntry) obj;

            if ((entry.getPlayer() != null) && (getPlayer() != null)) {
                int ergebnis = 0;

                //Beide null -> Der ManuelleSmilie entscheidet
                if (((entry.getPlayer().getTeamInfoSmilie() == null)
                        || entry.getPlayer().getTeamInfoSmilie().equals(""))
                        && ((getPlayer().getTeamInfoSmilie() == null)
                        || getPlayer().getTeamInfoSmilie().equals(""))) {
                    ergebnis = 0;
                } else if ((entry.getPlayer().getTeamInfoSmilie() == null)
                        || entry.getPlayer().getTeamInfoSmilie().equals("")) {
                    ergebnis = 1;
                } else if ((getPlayer().getTeamInfoSmilie() == null)
                        || getPlayer().getTeamInfoSmilie().equals("")) {
                    ergebnis = -1;
                } else {
                    ergebnis = entry.getPlayer().getTeamInfoSmilie().compareTo(getPlayer()
                            .getTeamInfoSmilie());
                }

                //Bei "Gleichstand" die Aufstellung beachten
                if (ergebnis == 0) {
                    final MatchRoleID entrySort = core.model.HOVerwaltung.instance()
                            .getModel()
                            .getCurrentLineup()
                            .getPositionBySpielerId(entry.getPlayer()
                                    .getSpielerID());
                    final MatchRoleID sort = core.model.HOVerwaltung.instance()
                            .getModel()
                            .getCurrentLineup()
                            .getPositionBySpielerId(getPlayer()
                                    .getSpielerID());

                    if ((sort == null) && (entrySort == null)) {
                        ergebnis = 0;
                    } else if (sort == null) {
                        ergebnis = -1;
                    } else if (entrySort == null) {
                        ergebnis = 1;
                    } else if (sort.getSortId() > entrySort.getSortId()) {
                        ergebnis = -1;
                    } else if (sort.getSortId() < entrySort.getSortId()) {
                        ergebnis = 1;
                    } else {
                        ergebnis = 0;
                    }
                }

                return ergebnis;
            }
        }

        return 0;
    }

    @Override
	public final void updateComponent() {
        if (player != null) {
            if ((player.getTeamInfoSmilie() != null) && !player.getTeamInfoSmilie().equals("")) {
                team.setIcon(GroupTeamFactory.instance().getActiveGroupIcon(player.getTeamInfoSmilie()));
            } else {
                team.clear();
            }

            if ((player.getManuellerSmilie() != null) && !player.getManuellerSmilie().equals("")) {
                manuell.setIcon(ImageUtilities.getSmileyIcon(player.getManuellerSmilie()));
            } else {
                manuell.clear();
            }
        } else {
            team.clear();
            manuell.clear();
        }
    }
}
