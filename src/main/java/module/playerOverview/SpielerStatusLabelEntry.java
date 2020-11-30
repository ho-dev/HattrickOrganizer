package module.playerOverview;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoubleLabelEntries;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.icon.StatusIcon;
import core.gui.theme.ImageUtilities;
import core.model.player.Player;
import javax.swing.*;
import java.awt.*;

/**
 * Displays the status of a player using icons.
 *
 * <p>SpielerStatusLabelEntry is a {@link DoubleLabelEntries} that displays:</p>
 * <ul>
 *     <li>On the left, card status and whether the player is transferlisted;</li>
 *     <li>On the right, injury details.</li>
 * </ul>
 */
public class SpielerStatusLabelEntry extends DoubleLabelEntries {

    //~ Instance fields ----------------------------------------------------------------------------

    private Player player;


    public SpielerStatusLabelEntry() {
        super();
        ColorLabelEntry jlInjury = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                ColorLabelEntry.BG_STANDARD,
                SwingConstants.RIGHT);
        ColorLabelEntry warnings = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                ColorLabelEntry.BG_STANDARD,
                SwingConstants.LEFT);
        this.setLabels(warnings, jlInjury);
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
                getRechts().setIcon(ImageUtilities.getPlasterIcon(12, 12));
            } else if (player.getVerletzt() > 0) {
                if(player.getVerletzt() != 999) {
                    getRechts().setText(player.getVerletzt() + "  ");
                }
                else {
                    getRechts().setText("\u221E  ");
                }
                getRechts().setIcon(ImageUtilities.getInjuryIcon(12, 12));
                getRechts().setFont(new Font("Serif", Font.BOLD, 12));
            } else {
                getRechts().clear();
            }
        } else {
            getLinks().clear();
            getRechts().clear();
        }
    }
}
