package core.gui.model;

import core.datatype.ComboItem;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchType;
import core.model.match.Weather;
import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

import static core.util.HTCalendarFactory.getHTSeason;
import static core.util.HTCalendarFactory.getHTWeek;

public class MatchOrdersCBItem extends MatchKurzInfo implements ComboItem {

    private boolean m_bOrdersSetInHT;
    private int m_iMatchID = -1;
    private String m_sOpponentName;
    private MatchType m_clMatchType;
    private java.sql.Timestamp m_tsMatchTime;
    private JComponent m_jpComponent;
    private short m_clLocation;
    private Weather m_clWeather;
    private Weather.Forecast m_clWeatherForecast;

    @Override
    public Weather getWeather() {return m_clWeather;}

    @Override
    public void setWeather(Weather Weather) {this.m_clWeather = Weather;}

    @Override
    public Weather.Forecast getWeatherForecast() {return m_clWeatherForecast;}

    @Override
    public void setWeatherForecast(Weather.Forecast WeatherForecast) {this.m_clWeatherForecast = WeatherForecast;}

    public short getLocation() {return m_clLocation;}

    public void setLocation(short location) {this.m_clLocation = location; }

    public boolean areOrdersSetInHT() {
        return m_bOrdersSetInHT;
    }

    public void setOrdersSetInHT(boolean m_bOrdersSetInHT) {
        this.m_bOrdersSetInHT = m_bOrdersSetInHT;
    }

    @Override
    public int getMatchID() {
        return m_iMatchID;
    }

    @Override
    public void setMatchID(int m_iMatchID) {
        this.m_iMatchID = m_iMatchID;
    }

    public String getM_sOpponentName() {
        return m_sOpponentName;
    }

    public void setOpponentName(String m_sTeamName) {
        this.m_sOpponentName = m_sTeamName;
    }

    public MatchType getMatchType() {
        return m_clMatchType;
    }

    @Override
    public void setMatchType(MatchType m_clMatchType) {
        this.m_clMatchType = m_clMatchType;
    }

    @Override
    public Timestamp getMatchDateAsTimestamp() {
        return m_tsMatchTime;
    }

    public void setMatchTime(Timestamp m_tsMatchTime) {
        this.m_tsMatchTime = m_tsMatchTime;
    }


    public MatchOrdersCBItem() {
    }

    public final JComponent getComponent(){
        if (m_jpComponent == null) createComponent();
        return m_jpComponent;
    }


    public final void createComponent() {
        m_jpComponent = new JPanel();

        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();

        m_jpComponent.setLayout(layout);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.weightx = 1.0;
        constraints.gridx = 0;

        String sDate = new SimpleDateFormat("dd-MM-yyyy HH:mm ").format(m_tsMatchTime);
        int iHTSeason = getHTSeason(m_tsMatchTime, true);
        int iHTWeek = getHTWeek(m_tsMatchTime, true);
        sDate += "(" + iHTWeek + "/" + iHTSeason + ")";
        JLabel jlNextGame = new JLabel(m_sOpponentName + "  " + sDate);
        jlNextGame.setIcon(ThemeManager.getIcon(HOIconName.MATCHICONS[m_clMatchType.getIconArrayIndex()]));
        layout.setConstraints(jlNextGame, constraints);
        m_jpComponent.add(jlNextGame);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.fill = GridBagConstraints.NONE;
        JLabel jlOrderSet = new JLabel(" ");
        int iHeight = 16;
        int iWidth = Math.round(iHeight * 66f / 47f);
        Map<Object, Object> mapColor;
        Icon icon;
        if(m_bOrdersSetInHT){
            mapColor = Map.of("lineupColor", HOColorName.ORDERS_LINEUP, "tickColor", HOColorName.ORDERS_TICK);
            icon = ImageUtilities.getSvgIcon(HOIconName.ORDERS_SENT, mapColor, iWidth, iHeight);
        }
        else{
            mapColor = Map.of("lineupColor", HOColorName.ORDERS_LINEUP, "penColor", HOColorName.ORDERS_PEN);
            icon = ImageUtilities.getSvgIcon(HOIconName.ORDERS_MISSING, mapColor, iWidth, iHeight);
        }
        jlOrderSet.setPreferredSize(new Dimension(iWidth, iHeight));
        jlOrderSet.setIcon(icon);
        layout.setConstraints(jlOrderSet, constraints);
        m_jpComponent.add(jlOrderSet);

    }

    public final Component getListCellRendererComponent(boolean isSelected) {
        JComponent comp;

        if (m_iMatchID != -1) {
            comp = getComponent();
        }
        else {
            comp = new javax.swing.JLabel(" ");
            comp.setOpaque(true);
            comp.setBackground(isSelected ? HODefaultTableCellRenderer.SELECTION_BG : ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
            int iHeight = 16;
            int iWidth = Math.round(iHeight * 66f / 47f);
            comp.setPreferredSize(new Dimension(iWidth, iHeight));
        }
        return comp;
    }


    @Override
    public int getId() {
        return m_iMatchID;
    }

    @Override
    public String getText() {
        return "";
    }


//            if ((obj.getPlayer() != null) && (getPlayer() != null)) {
//        if (getPositionsEvaluation() > obj.getPositionsEvaluation()) {
//            return -1;
//        } else if (getPositionsEvaluation() < obj.getPositionsEvaluation()) {
//            return 1;
//        } else {
//            return getPlayer().getLastName().compareTo(obj.getPlayer().getLastName());
//        }
//    } else if (obj.getPlayer() == null) {
//        return -1;
//    } else {
//        return 1;
//    }

}
