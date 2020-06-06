package module.teamAnalyzer.ui;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import module.teamAnalyzer.vo.Team;

import java.awt.*;
import javax.swing.*;

public class ComboBoxRenderer extends JLabel implements ListCellRenderer {
    //~ Constructors -------------------------------------------------------------------------------
	private static final long serialVersionUID = -3551665867069804794L;

    public ComboBoxRenderer()
    {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }
    public Component getListCellRendererComponent(JList list,Object value,
                                                  int index,boolean isSelected,boolean cellHasFocus)
    {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        Team team = (Team) value;

        if (team.getMatchType() != -1) {
           setIcon(ThemeManager.getIcon(HOIconName.MATCHICONS[team.getMatchType()]));
        }
        else {
            setIcon(ThemeManager.getIcon(HOIconName.EMPTY));
        }
        setText(team.getName());

        return this;
    }
}
