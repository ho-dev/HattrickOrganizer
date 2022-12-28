package module.teamAnalyzer.ui;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.util.HODateTime;
import module.teamAnalyzer.vo.Team;
import java.awt.*;
import javax.swing.*;


public class MatchComboBoxRenderer extends JLabel implements ListCellRenderer<Team> {

    private final String OWN_TEAM_NAME = HOVerwaltung.instance().getModel().getBasics().getTeamName();

    public enum RenderType {TYPE_1, TYPE_2}

    RenderType renderType;

    public MatchComboBoxRenderer()
    {
        this(RenderType.TYPE_1);
    }

    public MatchComboBoxRenderer(RenderType _renderType)
    {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
        renderType = _renderType;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Team> list, Team value,
                                                  int index,boolean isSelected,boolean cellHasFocus)
    {
        if (value == null) {
            JLabel m_jlBlank = new JLabel(" ");
            m_jlBlank.setOpaque(true);
            if (renderType == RenderType.TYPE_1) {
                if (isSelected) {
                    m_jlBlank.setBackground(list.getSelectionBackground());
                } else {
                    m_jlBlank.setBackground(list.getBackground());
                }
            }
            else if (renderType == RenderType.TYPE_2) {
                if (isSelected) {
                    m_jlBlank.setBackground(HODefaultTableCellRenderer.SELECTION_BG);
                } else {
                    m_jlBlank.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
                }
            }

            return m_jlBlank;
        }

        if (value.getMatchType() != MatchType.NONE) {
           setIcon(ThemeManager.getIcon(HOIconName.MATCHICONS[value.getMatchType().getIconArrayIndex()]));
        }
        else {
            setIcon(ThemeManager.getIcon(HOIconName.EMPTY));
        }

        if (renderType == RenderType.TYPE_1) {
            String sDate = "";
            if ( value.getTime() != null) {
                var matchSchedule = value.getTime();
                if (matchSchedule.isAfter(HODateTime.HT_START)) {
                    sDate = matchSchedule.toLocaleDateTime();
                    var htWeek = matchSchedule.toLocaleHTWeek();
                    int iHTSeason = htWeek.season;
                    int iHTWeek = htWeek.week;
                    sDate += " (" + iHTWeek + "/" + iHTSeason + ")";
                }
            }

            setText(value.getName() + "  " + sDate);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
        }
        else if (renderType == RenderType.TYPE_2) {
            String sMatch;
            if ( value.isTemplate()){
                sMatch = value.getName();
            }
            else if (value.isHomeMatch()) {
                sMatch = OWN_TEAM_NAME + " - " + value.getName();
            } else {
                sMatch = value.getName() + " - " + OWN_TEAM_NAME;
            }
            setText(sMatch);
            if (isSelected) {
                setBackground(HODefaultTableCellRenderer.SELECTION_BG);
            } else {
                setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
            }
        }
        else{
            setText("");
            if (isSelected) {
                setBackground(HODefaultTableCellRenderer.SELECTION_BG);
            } else {
                setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
            }
        }


        return this;
    }
}
