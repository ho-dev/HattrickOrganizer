// %2843598420:de.hattrickorganizer.gui.playeroverview%
package module.playerOverview;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoppelLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.icon.StatusIcon;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.player.Player;

import javax.swing.*;
import java.awt.*;

/**
 * Displays the status of a player using icons.
 *
 * <p>SpielerStatusLabelEntry is a {@link DoppelLabelEntry} that displays:</p>
 * <ul>
 *     <li>On the left, card status and whether the player is transferlisted;</li>
 *     <li>On the right, injury details.</li>
 * </ul>
 */
public class SpielerStatusLabelEntry extends DoppelLabelEntry {

    //~ Instance fields ----------------------------------------------------------------------------

    private final ColorLabelEntry injury = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                                                             ColorLabelEntry.BG_STANDARD,
                                                             SwingConstants.RIGHT);
    private final ColorLabelEntry warnings = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                                                               ColorLabelEntry.BG_STANDARD,
                                                               SwingConstants.LEFT);
    private Player player;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpielerStatusLabelEntry object.
     */
    public SpielerStatusLabelEntry() {
        super();
        this.setLabels(warnings, injury);
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
                    return Integer.compare(entry.getPlayer().getGelbeKarten(), getPlayer().getGelbeKarten());
                }
            }
        }

        return 0;
    }

    @Override
	public final void updateComponent() {
        if (player != null) {
            getLinks().clear();
            getLinks().setIcon(new StatusIcon(player));

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
    }
}
