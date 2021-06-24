package core.gui.model;

import core.datatype.ComboItem;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.match.MatchKurzInfo;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Map;
import static core.util.HTCalendarFactory.getHTSeason;
import static core.util.HTCalendarFactory.getHTWeek;

public class MatchOrdersCBItem extends MatchKurzInfo implements ComboItem {

    private JComponent m_jpComponent;
    private short m_clLocation;

    public short getLocation() {return m_clLocation;}

    public void setLocation(short location) {this.m_clLocation = location; }


    /**
     * Constructor
     */
    public MatchOrdersCBItem(MatchKurzInfo _matchKurzInfo, short sLocation){
        super(_matchKurzInfo);
        m_clLocation = sLocation;
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

        String sDate = new SimpleDateFormat("dd-MM-yyyy HH:mm ").format(this.getMatchDateAsTimestamp());
        int iHTSeason = getHTSeason(this.getMatchDateAsTimestamp(), true);
        int iHTWeek = getHTWeek(this.getMatchDateAsTimestamp(), true);
        sDate += "(" + iHTWeek + "/" + iHTSeason + ")";
        JLabel jlNextGame = new JLabel(this.getOpponentTeamName() + "  " + sDate);
        jlNextGame.setIcon(ThemeManager.getIcon(HOIconName.MATCHICONS[this.getMatchTypeExtended().getIconArrayIndex()]));
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
        if(this.isOrdersGiven()){
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

        if (this.getMatchID() != -1) {
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


    public boolean areOrdersSetInHT() {
        return this.isOrdersGiven();
    }

    public void setOrdersSetInHT(boolean b) {
        this.setOrdersGiven(b);
    }

    @Override
    public int getId() {
        return this.getMatchID();
    }

    @Override
    public String getText() {
        return "";
    }



}
