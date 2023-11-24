package module.playerOverview;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoubleLabelEntries;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.icon.StatusIcon;
import core.gui.theme.ImageUtilities;
import core.model.player.Player;
import org.jetbrains.annotations.NotNull;

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
public class PlayerStatusLabelEntry extends DoubleLabelEntries {

    //~ Instance fields ----------------------------------------------------------------------------

    private Player player;
    private Boolean m_isLarge;


    public PlayerStatusLabelEntry(Color colorBG, Boolean isLarge) {
        super();
        m_isLarge = isLarge;
        ColorLabelEntry jlInjury = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                colorBG,
                SwingConstants.RIGHT);
        ColorLabelEntry warnings = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
                colorBG,
                SwingConstants.LEFT);
        this.setLabels(warnings, jlInjury);
    }

    public PlayerStatusLabelEntry() {
        this(ColorLabelEntry.BG_STANDARD, false);
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
        if (obj instanceof PlayerStatusLabelEntry) {
            final PlayerStatusLabelEntry entry = (PlayerStatusLabelEntry) obj;

            if ((entry.getPlayer() != null) && (getPlayer() != null)) {
                if (entry.getPlayer().getInjuryWeeks() > getPlayer().getInjuryWeeks()) {
                    return 1;
                } else if (entry.getPlayer().getInjuryWeeks() < getPlayer().getInjuryWeeks()) {
                    return -1;
                } else {
                    return Integer.compare(entry.getPlayer().getTotalCards(), getPlayer().getTotalCards());
                }
            }
        }

        return 0;
    }

    @Override
	public final void updateComponent() {
        if (player != null) {
            getLeft().clear();
            getLeft().setIcon(new StatusIcon(player, m_isLarge));

            if (player.getInjuryWeeks() == 0) {
                getRight().setText("");
                getRight().setIcon(ImageUtilities.getPlasterIcon(12, 12));
            } else if (player.getInjuryWeeks() > 0) {
                if(player.getInjuryWeeks() != 999) {
                    getRight().setText(player.getInjuryWeeks() + "  ");
                }
                else {
                    getRight().setText("\u221E  ");
                }
                getRight().setIcon(ImageUtilities.getInjuryIcon(12, 12));
                getRight().setFont(new Font("Serif", Font.BOLD, 12));
            } else {
                getRight().clear();
            }
        } else {
            getLeft().clear();
            getRight().clear();
        }
    }
}
