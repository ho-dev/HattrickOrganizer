package core.gui.model;

import core.datatype.ComboItem;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.SpielerLabelEntry;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.model.HOVerwaltung;
import core.model.player.Player;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;

public class SpielerCBItem implements Comparable<SpielerCBItem>, ComboItem {

    protected static int PLAYER_COMBO_HEIGHT = 35;
    public static javax.swing.JLabel m_jlLeer = new javax.swing.JLabel(" ");
    public SpielerLabelEntry m_clEntry;
    private @Nullable Player m_clPlayer;
    private String m_sText;
    private float m_fPositionsBewertung;
    private boolean m_bAlternativePosition;
    private boolean m_bMultiLine = false;


    public SpielerCBItem(String text, float positionRating, @Nullable Player player) {
        m_sText = text;
        m_clPlayer = player;
        m_fPositionsBewertung = positionRating;
        m_bAlternativePosition = false;
        m_clEntry = new SpielerLabelEntry(null, null, 0f, true, true);
    }

    public SpielerCBItem(String text, float positionRating, @Nullable Player player, boolean useCustomText, boolean multiLine) {
        m_sText = text;
        m_clPlayer = player;
        m_fPositionsBewertung = positionRating;
        m_bAlternativePosition = false;
        m_bMultiLine = multiLine;
        if (useCustomText) {
            m_clEntry = new SpielerLabelEntry(null, null, 0f, true, true, true, text, m_bMultiLine);
        } else {
            m_clEntry = new SpielerLabelEntry(null, null, 0f, true, true, false, "", m_bMultiLine);
        }
    }

    public final Component getListCellRendererComponent(JList<? extends SpielerCBItem>  jList, int index, boolean isSelected) {
        final Player player = getPlayer();

        if (player != null) {
            m_clEntry.updateComponent(player, HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc()
                            .getPositionBySpielerId(player.getSpielerID()), getPositionsEvaluation(), m_bAlternativePosition,
                    m_sText);

            JComponent comp = m_clEntry.getComponent(isSelected, index==-1);
            if (m_bMultiLine) comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, PLAYER_COMBO_HEIGHT));
            return comp;

        } else {
            m_jlLeer.setOpaque(true);
            m_jlLeer.setBackground(isSelected ? HODefaultTableCellRenderer.SELECTION_BG : ColorLabelEntry.BG_STANDARD);
            return m_jlLeer;
        }
    }

    public SpielerLabelEntry getEntry() {
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

    public final void setValues(String text, float positionRating, Player player, boolean alternativePosition) {
        m_sText = text;
        m_clPlayer = player;
        m_fPositionsBewertung = positionRating;
        m_bAlternativePosition = alternativePosition;
    }

    @Override
    public final int compareTo(SpielerCBItem obj) {

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
        if (obj instanceof SpielerCBItem) {
            final SpielerCBItem temp = (SpielerCBItem) obj;

            if ((this.getPlayer() != null) && (temp.getPlayer() != null)) {
                return this.getPlayer().getSpielerID() == temp.getPlayer().getSpielerID();
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
            return this.m_clPlayer.getSpielerID();
        }
        return -1;
    }
}
