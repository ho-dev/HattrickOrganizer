// %3364174802:de.hattrickorganizer.gui.model%
package core.gui.comp.entry;

import core.gui.theme.ThemeManager;

import javax.swing.SwingConstants;



/**
 * Zeigt die Warnings und Verletzungen an
 */
public class SmilieEntry extends DoppelLabelEntry {
    //~ Instance fields ----------------------------------------------------------------------------

    private ColorLabelEntry manuell = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                                                          ColorLabelEntry.BG_STANDARD,
                                                          SwingConstants.RIGHT);
    private ColorLabelEntry team = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                                                       ColorLabelEntry.BG_STANDARD,
                                                       SwingConstants.LEFT);
    private core.model.player.Spieler spieler;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SmilieEntry object.
     */
    public SmilieEntry() {
        super();
        this.setLabels(team, manuell);
    }

   public final void setSpieler(core.model.player.Spieler spieler) {
        this.spieler = spieler;
        updateComponent();
    }

    public final core.model.player.Spieler getSpieler() {
        return spieler;
    }

    @Override
	public final int compareTo(IHOTableEntry obj) {
        if (obj instanceof SmilieEntry) {
            final SmilieEntry entry = (SmilieEntry) obj;

            if ((entry.getSpieler() != null) && (getSpieler() != null)) {
                int ergebnis = 0;

                //Beide null -> Der ManuelleSmilie entscheidet
                if (((entry.getSpieler().getTeamInfoSmilie() == null)
                    || entry.getSpieler().getTeamInfoSmilie().equals(""))
                    && ((getSpieler().getTeamInfoSmilie() == null)
                    || getSpieler().getTeamInfoSmilie().equals(""))) {
                    ergebnis = 0;
                } else if ((entry.getSpieler().getTeamInfoSmilie() == null)
                           || entry.getSpieler().getTeamInfoSmilie().equals("")) {
                    ergebnis = 1;
                } else if ((getSpieler().getTeamInfoSmilie() == null)
                           || getSpieler().getTeamInfoSmilie().equals("")) {
                    ergebnis = -1;
                } else {
                    ergebnis = entry.getSpieler().getTeamInfoSmilie().compareTo(getSpieler()
                                                                                    .getTeamInfoSmilie());
                }

                //Bei "Gleichstand" die Aufstellung beachten
                if (ergebnis == 0) {
                    final core.model.player.SpielerPosition entrySort = core.model.HOVerwaltung.instance()
                                                                                                                        .getModel()
                                                                                                                        .getAufstellung()
                                                                                                                        .getPositionBySpielerId(entry.getSpieler()
                                                                                                                                                     .getSpielerID());
                    final core.model.player.SpielerPosition sort = core.model.HOVerwaltung.instance()
                                                                                                                   .getModel()
                                                                                                                   .getAufstellung()
                                                                                                                   .getPositionBySpielerId(getSpieler()
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
        if (spieler != null) {
            if ((spieler.getTeamInfoSmilie() != null) && !spieler.getTeamInfoSmilie().equals("")) {
                team.setIcon(ThemeManager.getIcon(spieler.getTeamInfoSmilie()));
            } else {
                team.clear();
            }

            if ((spieler.getManuellerSmilie() != null) && !spieler.getManuellerSmilie().equals("")) {
                manuell.setIcon(ThemeManager.getIcon(spieler.getManuellerSmilie()));
            } else {
                manuell.clear();
            }
        } else {
            team.clear();
            manuell.clear();
        }
    }
}
