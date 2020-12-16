package module.teamAnalyzer.ui;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import module.teamAnalyzer.vo.Team;
import java.awt.*;
import java.text.SimpleDateFormat;
import javax.swing.*;

import static core.util.HTCalendarFactory.getHTSeason;
import static core.util.HTCalendarFactory.getHTWeek;

public class MatchComboBoxRenderer extends JLabel implements ListCellRenderer<Team> {

    public MatchComboBoxRenderer()
    {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Team> list, Team value,
                                                  int index,boolean isSelected,boolean cellHasFocus)
    {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if (value.getMatchType() != -1) {
           setIcon(ThemeManager.getIcon(HOIconName.MATCHICONS[value.getMatchType()]));
        }
        else {
            setIcon(ThemeManager.getIcon(HOIconName.EMPTY));
        }

        String sDate = new SimpleDateFormat("dd-MM-yyyy HH:mm ").format(value.getTime());
        int iHTSeason = getHTSeason(value.getTime(), true);
        int iHTWeek = getHTWeek(value.getTime(), true);
        sDate += "("+ iHTWeek + "/" +  iHTSeason +")";

        setText(value.getName() + "  " + sDate);

        return this;
    }
}
