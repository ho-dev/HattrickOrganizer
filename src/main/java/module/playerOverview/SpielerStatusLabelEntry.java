// %2843598420:de.hattrickorganizer.gui.playeroverview%
package module.playerOverview;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoppelLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.player.Player;

import javax.swing.SwingConstants;
import java.awt.*;


public class SpielerStatusLabelEntry extends DoppelLabelEntry {
    //~ Instance fields ----------------------------------------------------------------------------

    private ColorLabelEntry verletzung = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                                                             ColorLabelEntry.BG_STANDARD,
                                                             SwingConstants.RIGHT);
    private ColorLabelEntry verwarnungen = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                                                               ColorLabelEntry.BG_STANDARD,
                                                               SwingConstants.LEFT);
    private Player player;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpielerStatusLabelEntry object.
     */
    public SpielerStatusLabelEntry() {
        super();
        this.setLabels(verwarnungen, verletzung);
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
        if (obj instanceof SpielerStatusLabelEntry) {
            final SpielerStatusLabelEntry entry = (SpielerStatusLabelEntry) obj;

            if ((entry.getPlayer() != null) && (getPlayer() != null)) {
                if (entry.getPlayer().getVerletzt() > getPlayer().getVerletzt()) {
                    return 1;
                } else if (entry.getPlayer().getVerletzt() < getPlayer().getVerletzt()) {
                    return -1;
                } else {
                    if (entry.getPlayer().getGelbeKarten() > getPlayer().getGelbeKarten()) {
                        return 1;
                    } else if (entry.getPlayer().getGelbeKarten() < getPlayer().getGelbeKarten()) {
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
        if (player != null) {
            if (player.isGesperrt()) {
                getLinks().setIcon(ThemeManager.getIcon(HOIconName.REDCARD));
            } else if (player.getGelbeKarten() == 1) {
                getLinks().setIcon(ThemeManager.getIcon(HOIconName.YELLOWCARD));
            } else if (player.getGelbeKarten() >= 2) {
                getLinks().setIcon(ThemeManager.getIcon(HOIconName.TWOCARDS));
            } else {
                getLinks().clear();
            }

            if (player.getVerletzt() == 0) {
                getRechts().setText("");
                getRechts().setIcon(ThemeManager.getIcon(HOIconName.BRUISED_SMALL));
            } else if (player.getVerletzt() > 0) {
                if(player.getVerletzt() != 999) {
                    getRechts().setText(player.getVerletzt() + "");
                }
                else {
                    getRechts().setText("\u221E");
                }
                getRechts().setIcon(ThemeManager.getIcon(HOIconName.INJURED_SMALL));
                getRechts().setFont(new Font("Serif", Font.BOLD, 14));
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
