// %2843598420:de.hattrickorganizer.gui.playeroverview%
package module.playerOverview;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoppelLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.player.Spieler;

import javax.swing.SwingConstants;



public class SpielerStatusLabelEntry extends DoppelLabelEntry {
    //~ Instance fields ----------------------------------------------------------------------------

    private ColorLabelEntry verletzung = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                                                             ColorLabelEntry.BG_STANDARD,
                                                             SwingConstants.RIGHT);
    private ColorLabelEntry verwarnungen = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                                                               ColorLabelEntry.BG_STANDARD,
                                                               SwingConstants.LEFT);
    private core.model.player.Spieler spieler;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpielerStatusLabelEntry object.
     */
    public SpielerStatusLabelEntry() {
        super();
        this.setLabels(verwarnungen, verletzung);
    }

    public final void setSpieler(Spieler spieler) {
        this.spieler = spieler;
        updateComponent();
    }

    public final Spieler getSpieler() {
        return spieler;
    }

    @Override
	public final int compareTo(IHOTableEntry obj) {
        if (obj instanceof SpielerStatusLabelEntry) {
            final SpielerStatusLabelEntry entry = (SpielerStatusLabelEntry) obj;

            if ((entry.getSpieler() != null) && (getSpieler() != null)) {
                if (entry.getSpieler().getVerletzt() > getSpieler().getVerletzt()) {
                    return 1;
                } else if (entry.getSpieler().getVerletzt() < getSpieler().getVerletzt()) {
                    return -1;
                } else {
                    if (entry.getSpieler().getGelbeKarten() > getSpieler().getGelbeKarten()) {
                        return 1;
                    } else if (entry.getSpieler().getGelbeKarten() < getSpieler().getGelbeKarten()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        }

        return 0;
    }

    @Override
	public final void updateComponent() {
        if (spieler != null) {
            if (spieler.isGesperrt()) {
                getLinks().setIcon(ThemeManager.getIcon(HOIconName.REDCARD));
            } else if (spieler.getGelbeKarten() == 1) {
                getLinks().setIcon(ThemeManager.getIcon(HOIconName.YELLOWCARD));
            } else if (spieler.getGelbeKarten() >= 2) {
                getLinks().setIcon(ThemeManager.getIcon(HOIconName.TWOCARDS));
            } else {
                getLinks().clear();
            }

            if (spieler.getVerletzt() == 0) {
                getRechts().setText("");
                getRechts().setIcon(ThemeManager.getIcon(HOIconName.PATCH));
            } else if (spieler.getVerletzt() > 0) {
                getRechts().setText(spieler.getVerletzt() + "");
                getRechts().setIcon(ThemeManager.getIcon(HOIconName.INJURED));
            } else {
                getRechts().clear();
            }
        } else {
            getLinks().clear();
            getRechts().clear();
        }

        //super.updateComponent();
    }
}
