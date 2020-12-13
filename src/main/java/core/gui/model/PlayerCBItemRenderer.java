package core.gui.model;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import javax.swing.*;
import java.awt.*;


public class PlayerCBItemRenderer implements javax.swing.ListCellRenderer<PlayerCBItem> {

    @Override
    public Component getListCellRendererComponent(JList<? extends PlayerCBItem> list, PlayerCBItem value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value != null) {
            return value.getListCellRendererComponent(list, index, isSelected);
        } else {
            javax.swing.JLabel m_jlBlank = new javax.swing.JLabel(" ");
            m_jlBlank.setOpaque(true);
            if (isSelected) {
                m_jlBlank.setBackground(HODefaultTableCellRenderer.SELECTION_BG);
            } else {
                m_jlBlank.setBackground(ColorLabelEntry.BG_STANDARD);
            }
            return m_jlBlank;
        }
    }

}