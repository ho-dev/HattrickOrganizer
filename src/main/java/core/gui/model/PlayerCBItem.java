package core.gui.model;

import core.datatype.ComboItem;
import core.gui.comp.entry.PlayerLabelEntry;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchLineupPosition;
import core.model.player.Player;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;

public class PlayerCBItem implements Comparable<PlayerCBItem>, ComboItem {

    protected static int PLAYER_COMBO_HEIGHT = 35;
    public static javax.swing.JLabel m_jlLeer = new javax.swing.JLabel(" ");
    public PlayerLabelEntry m_clEntry;
    private @Nullable Player m_clPlayer;
    private String m_sText;
    private float m_fPositionsBewertung;
    private boolean m_bSetInBestPosition;
    private boolean m_bMultiLine = false;

    public boolean isSetInBestPosition() {
        return m_bSetInBestPosition;
    }

    public PlayerCBItem(String text, float positionRating, @Nullable Player player) {
        m_sText = text;
        m_clPlayer = player;
        m_fPositionsBewertung = positionRating;
        m_bSetInBestPosition = false;
        m_clEntry = new PlayerLabelEntry(null, null, 0f, false, true);
    }

    public PlayerCBItem(String text, float positionRating, @Nullable Player player, boolean useCustomText, boolean multiLine) {
        m_sText = text;
        m_clPlayer = player;
        m_fPositionsBewertung = positionRating;
        m_bSetInBestPosition = false;
        m_bMultiLine = multiLine;
        if (useCustomText) {
            m_clEntry = new PlayerLabelEntry(null, null, 0f, true, true, true, text, m_bMultiLine);
        } else {
            m_clEntry = new PlayerLabelEntry(null, null, 0f, true, true, false, "", m_bMultiLine);
        }
    }

    public final Component getListCellRendererComponent(int index, boolean isSelected) {
        final Player player = getPlayer();

        if (player != null) {
            var lineup = HOVerwaltung.instance().getModel().getCurrentLineup();
            MatchLineupPosition matchLineupPosition;
            matchLineupPosition = lineup.getPositionByPlayerId(player.getPlayerId());
            m_clEntry.updateComponent(player, matchLineupPosition, getPositionsEvaluation(), m_bSetInBestPosition, m_sText);
            JComponent comp = m_clEntry.getComponent(isSelected, index==-1);
            if (m_bMultiLine) comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, PLAYER_COMBO_HEIGHT));
            return comp;
        } else {
            m_jlLeer.setOpaque(true);
            m_jlLeer.setBackground(isSelected ? HODefaultTableCellRenderer.SELECTION_BG : ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
            return m_jlLeer;
        }
    }

    public PlayerLabelEntry getEntry() {
        return m_clEntry;
    }

    public final float getPositionsEvaluation() {
        return m_fPositionsBewertung;
    }

    public final void setPlayer(Player player) {
        m_clPlayer = player;
    }

    public final @Nullable Player getPlayer() {
        return m_clPlayer;
    }

    public final void setText(String text) {
        m_sText = text;
    }

    @Override
    public final String getText() {
        return m_sText;
    }

    public final void setValues(String text, float positionRating, Player player, boolean bestPosition) {
        m_sText = text;
        m_clPlayer = player;
        m_fPositionsBewertung = positionRating;
        m_bSetInBestPosition = bestPosition;
    }

    @Override
    public final int compareTo(PlayerCBItem obj) {

        if ((obj.getPlayer() != null) && (getPlayer() != null)) {
            if (getPositionsEvaluation() > obj.getPositionsEvaluation()) {
                return -1;
            } else if (getPositionsEvaluation() < obj.getPositionsEvaluation()) {
                return 1;
            } else {
                return getPlayer().getLastName().compareTo(obj.getPlayer().getLastName());
            }
        } else if (obj.getPlayer() == null) {
            return -1;
        } else {
            return 1;
        }

    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof final PlayerCBItem temp) {
            if ((this.getPlayer() != null) && (temp.getPlayer() != null)) {
                return this.getPlayer().getPlayerId() == temp.getPlayer().getPlayerId();
            } else return (this.getPlayer() == null) && (temp.getPlayer() == null);
        }

        return false;
    }

    @Override
    public final String toString() {
        return m_sText;
    }

    @Override
    public int getId() {
        if (this.m_clPlayer != null) {
            return this.m_clPlayer.getPlayerId();
        }
        return -1;
    }
}
