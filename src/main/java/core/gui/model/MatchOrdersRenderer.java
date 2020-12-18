package core.gui.model;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class MatchOrdersRenderer implements javax.swing.ListCellRenderer<MatchOrdersCBItem>{

    @Override
    public Component getListCellRendererComponent(JList<? extends MatchOrdersCBItem> list, MatchOrdersCBItem value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value != null) {
            Component comp = value.getListCellRendererComponent(isSelected);
            if (isSelected) {
                comp.setBackground(HODefaultTableCellRenderer.SELECTION_BG);
            } else {
                comp.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
            }
//            comp.setPreferredSize(new Dimension(325, 25));
//            comp.setMinimumSize(new Dimension(325, 25));
//            comp.setMaximumSize(new Dimension(325, 25));
            return comp;
        } else {
            JLabel m_jlBlank = new JLabel(" ");
            m_jlBlank.setOpaque(true);
            if (isSelected) {
                m_jlBlank.setBackground(HODefaultTableCellRenderer.SELECTION_BG);
            } else {
                m_jlBlank.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
            }
//            m_jlBlank.setPreferredSize(new Dimension(325, 25));
//            m_jlBlank.setMinimumSize(new Dimension(325, 25));
//            m_jlBlank.setMaximumSize(new Dimension(325, 25));
            return m_jlBlank;
        }
    }

}