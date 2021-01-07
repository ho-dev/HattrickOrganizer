package module.lineup.lineup;

import core.gui.Refreshable;
import core.gui.model.MatchOrdersCBItem;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.MatchType;
import core.net.HattrickLink;
import core.util.DateTimeUtils;
import core.util.HTCalendarFactory;
import core.util.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class MatchBanner extends JPanel implements Refreshable {

    MatchAndLineupSelectionPanel m_jpMatchSelectionPanel;
    MatchOrdersCBItem m_clSelectedMatch;
    JLabel jlHomeTeam, jlAwayTeam, jlMatchTypeIcon, jlWeather, jlMatchSchedule;

    public MatchBanner(MatchAndLineupSelectionPanel _matchSelectionPanel) {
        m_jpMatchSelectionPanel = _matchSelectionPanel;
        initComponents();
        refresh();
        core.gui.RefreshManager.instance().registerRefreshable(this);
    }

    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints gbc = new GridBagConstraints();

        setLayout(layout);

        gbc.insets = new Insets(3,3 ,3 ,3 );
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;


        // 1st row ================================================
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        jlMatchTypeIcon = new JLabel();
        layout.setConstraints(jlMatchTypeIcon, gbc);
        add(jlMatchTypeIcon);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        jlMatchSchedule= new JLabel();
        layout.setConstraints(jlMatchSchedule, gbc);
        add(jlMatchSchedule);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        jlWeather = new JLabel("");
        jlWeather.setHorizontalTextPosition(JLabel.LEFT);
        layout.setConstraints(jlWeather, gbc);
        add(jlWeather);

        // 2nd row ================================================

        final GridBagLayout subLayout = new GridBagLayout();
        final GridBagConstraints subConstraints = new GridBagConstraints();
        JPanel jpBottom = new JPanel();

        jpBottom.setLayout(subLayout);

        subConstraints.insets = new Insets(3,3,3,10);
        subConstraints.fill = GridBagConstraints.NONE;
        subConstraints.weightx = 0.0;
        subConstraints.weighty = 0.0;


        subConstraints.gridy = 0;
        subConstraints.gridx = 0;
        jlHomeTeam = new JLabel();
        jlHomeTeam.setHorizontalTextPosition(JLabel.CENTER);
        jlHomeTeam.setVerticalTextPosition(JLabel.BOTTOM);
        jlHomeTeam.setCursor(new Cursor(Cursor.HAND_CURSOR));
        subLayout.setConstraints(jlHomeTeam, subConstraints);
        jpBottom.add(jlHomeTeam);


        subConstraints.gridx = 2;
        subConstraints.insets = new Insets(3,10,3 ,3);
        jlAwayTeam = new JLabel();
        jlAwayTeam.setHorizontalTextPosition(JLabel.CENTER);
        jlAwayTeam.setVerticalTextPosition(JLabel.BOTTOM);
        jlAwayTeam.setCursor(new Cursor(Cursor.HAND_CURSOR));
        subLayout.setConstraints(jlAwayTeam, subConstraints);
        jpBottom.add(jlAwayTeam);


        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(jpBottom, gbc);
        add(jpBottom);

        setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, ThemeManager.getColor(HOColorName.PLAYER_POSITION_PANEL_BORDER)));

        initListeners();

    }



    private void initListeners(){
        jlHomeTeam.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                browseToTeam(m_clSelectedMatch.getHomeTeamID());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                jlHomeTeam.setText(String.format("<html><a href=''>%s</a></html>",m_clSelectedMatch.getHomeTeamName()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jlHomeTeam.setText(m_clSelectedMatch.getHomeTeamName());
            }
        });

        jlAwayTeam.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                browseToTeam(m_clSelectedMatch.getGuestTeamID());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                jlAwayTeam.setText(String.format("<html><a href=''>%s</a></html>",m_clSelectedMatch.getGuestTeamName()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jlAwayTeam.setText(m_clSelectedMatch.getGuestTeamName());
            }

        });
    }

    private void browseToTeam(int teamId){
            HattrickLink.showTeam(String.valueOf(teamId));
    }

    @Override
    public void refresh() {
        m_clSelectedMatch = m_jpMatchSelectionPanel.getSelectedMatch();
        if (m_clSelectedMatch == null){
            this.setVisible(false);
        }

        else{
            this.setVisible(true);
            MatchType matchType = m_clSelectedMatch.getMatchType();

            ThemeManager tm = ThemeManager.instance();

            Icon weatherIcon = tm.getIcon(HOIconName.WEATHER[m_clSelectedMatch.getWeather().getId()]);
            jlWeather.setIcon(weatherIcon);

            Icon MatchTypeIcon = tm.getScaledIcon(HOIconName.MATCHICONS[matchType.getIconArrayIndex()], 22, 22);
            jlMatchTypeIcon.setIcon(MatchTypeIcon);

            Icon homeTeamIcon = tm.getClubLogo(m_clSelectedMatch.getHomeTeamID());
            jlHomeTeam.setIcon(homeTeamIcon);
            jlHomeTeam.setText(m_clSelectedMatch.getHomeTeamName());


            Icon AwayTeamIcon = tm.getClubLogo(m_clSelectedMatch.getGuestTeamID());
            jlAwayTeam.setIcon(AwayTeamIcon);
            jlAwayTeam.setText(m_clSelectedMatch.getGuestTeamName());

            Date matchSchedule = m_clSelectedMatch.getMatchDateAsTimestamp();
            Long nbDays = ChronoUnit.DAYS.between(new Date().toInstant(), matchSchedule.toInstant());

            String sDate;
            if (nbDays<7) {
                sDate = DateTimeUtils.Format(matchSchedule, "EEEEE HH:mm");
            }
            else{
                sDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(matchSchedule);
            }

            String sLabel = "<html><div style='text-align: center;'>" + sDate + "\n";
            if (matchType == MatchType.LEAGUE) {
                int iHTWeek = HTCalendarFactory.getHTWeek(m_clSelectedMatch.getMatchDateAsTimestamp(), true);
                sLabel += String.format(Helper.getTranslation("ls.module.lineup.matchSchedule"), iHTWeek, m_clSelectedMatch.getMatchContextId());
            }
            else if (matchType.isFriendly()) {
                sLabel += "<br>" + matchType.getName();
            }
            sLabel += "</div></html>";

            jlMatchSchedule.setText(sLabel);
        }
    }

    @Override
    public void reInit() {

    }
}
