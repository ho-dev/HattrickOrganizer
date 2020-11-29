// %3815329211:de.hattrickorganizer.gui.league%
package module.series;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchType;
import core.model.series.Paarung;
import core.net.OnlineWorker;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;


/**
 * Display a game day.
 */
final class MatchDayPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 6884532906036202996L;
    static final int NAECHSTER_SPIELTAG = -2;
    static final int LETZTER_SPIELTAG = -1;

    private final JButton[] buttons = new JButton[4];
    private final JLabel[] homeTeams = new JLabel[4];
    private final JLabel[] visitorTeams = new JLabel[4];
    private final JLabel[] results = new JLabel[4];
    private int matchround = -1;
    private static final Color foreground = ThemeManager.getColor(HOColorName.LABEL_FG);
    private final Model model;

    protected MatchDayPanel(Model model, int spieltag) {
        this.model = model;
        // Kann codiert sein!
        matchround = spieltag;

        initComponents();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Das Match anzeigen
        int[] matchdata = new int[0];

        try {
            // Matchid, Heimid, Gastid, Heimtore, Gasttore
            matchdata = Helper.generateIntArray(e.getActionCommand());
        } catch (Exception ex) {
            HOLogger.instance().log(
                    getClass(),
                    "SpieltagPanel.actionPerformed: Matchid konnte nicht geparsed werden: "
                            + e.getActionCommand() + " / " + ex);
            return;
        }

        // --Match zeigen ggf runterladen--
        if (matchdata[0] > 0) {
            // Spiel nicht vorhanden, dann erst runterladen!
            if (!DBManager.instance().isMatchVorhanden(matchdata[0])) {

                OnlineWorker.downloadMatchData(matchdata[0], MatchType.LEAGUE, // not
                        // tournament
                        false);
                fillLabels();
                RefreshManager.instance().doReInit();

            } else {
                // Match zeigen
                HOMainFrame.instance().showMatch(matchdata[0]);
            }
        }
    }

    protected void changeSaison() {
        fillLabels();
    }

    private void setConstraintsValues(GridBagConstraints constraints, int fillValue,
                                      double weightxValue, int gridxValue, int gridyValue, int gridWithValue) {
        constraints.fill = fillValue;
        constraints.weightx = weightxValue;
        constraints.gridx = gridxValue;
        constraints.gridy = gridyValue;
        constraints.gridwidth = gridWithValue;
    }

    private void setMatchButton(JButton button, Paarung paarung) {
        button.setPreferredSize(new Dimension(27, 18));

        long gameFinishTime = 0;
        long nowTime = (new Date()).getTime();

        boolean gameFinished; // Hat das Spiel schon stattgefunden
        if (paarung != null) {
            gameFinishTime = paarung.getDatum().getTime();
            gameFinishTime = gameFinishTime + 3 * 60 * 60 * 1000L; //assuming 3 hours to make sure the game is finished
            // if paarung was not updated regularly, it could happen that hatStattgefunden would fail
            gameFinished = paarung.hatStattgefunden() || gameFinishTime < nowTime;
        }
        else {
            gameFinished = false;
        }

        if ( gameFinished ) {
            //Match already in the database
            if (DBManager.instance().isMatchVorhanden(paarung.getMatchId())) {
                button.setToolTipText(HOVerwaltung.instance().getLanguageString(
                        "tt_Ligatabelle_SpielAnzeigen"));
                button.setEnabled(true);
                button.setIcon(ThemeManager.getIcon(HOIconName.SHOW_MATCH));
                button.setDisabledIcon(ThemeManager.getIcon(HOIconName.SHOW_MATCH));
            }
            // Match not yet in the database
            else {
                button.setToolTipText(HOVerwaltung.instance().getLanguageString(
                        "tt_Ligatabelle_SpielDownloaden"));
                button.setEnabled(true);
                button.setIcon(ThemeManager.getIcon(HOIconName.DOWNLOAD_MATCH));
                button.setDisabledIcon(ThemeManager.getIcon(HOIconName.DOWNLOAD_MATCH));
            }
        }
        // Match has not taken place yet
        else {
            button.setToolTipText(HOVerwaltung.instance().getLanguageString(
                    "tt_Ligatabelle_SpielNochnichtgespielt"));
            button.setEnabled(false);
            button.setIcon(ThemeManager.getIcon(HOIconName.NO_MATCH));
            button.setDisabledIcon(ThemeManager.getIcon(HOIconName.NO_MATCH));
        }
    }

    private void fillLabels() {
        int spieltag = matchround;
        final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();

        if (this.model.getCurrentSeries() == null) {
            for (int i = 0; i < buttons.length; i++)
                setMatchButton(buttons[i], null);

            return;
        }

        // Letzte Spieltag
        if (spieltag == LETZTER_SPIELTAG) {
            spieltag = HOVerwaltung.instance().getModel().getBasics().getSpieltag() - 1;

            if (spieltag <= 0) {
                spieltag = 1;
            }
        }
        // Nächste Spieltag
        else if (spieltag == NAECHSTER_SPIELTAG) {
            spieltag = HOVerwaltung.instance().getModel().getBasics().getSpieltag();

            if (spieltag > 14) {
                spieltag = 14;
            }

        }

       List<Paarung> paarungen = this.model.getCurrentSeries().getPaarungenBySpieltag(spieltag);

        String bordertext = HOVerwaltung.instance().getLanguageString("Spieltag") + " " + spieltag;

        if (paarungen != null && paarungen.size() > 0) {
            try {
                bordertext += ("  ( "
                        + DateFormat.getDateTimeInstance().format(
                        ((Paarung) paarungen.get(0)).getDatum()) + " )");
            } catch (Exception e) {
                bordertext += ("  ( " + ((Paarung) paarungen.get(0)).getStringDate() + " )");
            }
        }

        setBorder(BorderFactory.createTitledBorder(bordertext));

        // Panel aktualisieren, wenn Paarungen gefunden
        if ((paarungen != null) && (paarungen.size() == 4)) {
            resetMarkierung();

            // Erste Paarung------------------------------------------

            for (int i = 0; i < buttons.length; i++) {
                Paarung paarung = (Paarung) paarungen.get(i);
                buttons[i].setActionCommand(paarung.getMatchId() + "," + paarung.getHeimId() + ","
                        + paarung.getGastId() + "," + paarung.getToreHeim() + ","
                        + paarung.getToreGast());
                setMatchButton(buttons[i], paarung);
                fillRow(homeTeams[i], visitorTeams[i], results[i], paarung, teamid);
            }
        }
        // Keine Paarungen
        else {
            for (int i = 0; i < buttons.length; i++)
                setMatchButton(buttons[i], null);
        }
    }

    private void fillRow(JLabel homeTeam, JLabel visitorTeam, JLabel result, Paarung paarung,
                         int teamid) {
        homeTeam.setText(paarung.getHeimName());
        visitorTeam.setText(paarung.getGastName());
        if ((paarung.getToreHeim() > -1) && (paarung.getToreGast() > -1)) {
            result.setText(StringUtils.getResultString(paarung.getToreHeim(), paarung.getToreGast(), ""));

            // HomeVictory
            if (paarung.getToreHeim() > paarung.getToreGast()) {
                homeTeam.setIcon(ImageUtilities.getStarIcon());
                visitorTeam.setIcon(ImageUtilities.NOIMAGEICON);
            }
            // VisitorVictory
            else if (paarung.getToreHeim() < paarung.getToreGast()) {
                homeTeam.setIcon(ImageUtilities.NOIMAGEICON);
                visitorTeam.setIcon(ImageUtilities.getStarIcon());
            }
            // drawn
            else {
                homeTeam.setIcon(ImageUtilities.getStarIcon());
                visitorTeam.setIcon(ImageUtilities.getStarIcon());
            }
        } else {
            result.setText(StringUtils.getResultString(-1, -1, ""));
            homeTeam.setIcon(ImageUtilities.NOIMAGEICON);
            visitorTeam.setIcon(ImageUtilities.NOIMAGEICON);
        }

        markMyTeam(homeTeam, paarung.getHeimId(), teamid);
        markMyTeam(visitorTeam, paarung.getGastId(), teamid);
        markSelectedTeam(homeTeam, paarung.getHeimName());
        markSelectedTeam(visitorTeam, paarung.getGastName());
    }

    private void initButton(JButton button, GridBagConstraints constraints, GridBagLayout layout,
                            int row) {
        setConstraintsValues(constraints, GridBagConstraints.NONE, 0.0, 6, row, 1);

        button.setBackground(this.getBackground());
        button.addActionListener(this);
        layout.setConstraints(button, constraints);
        add(button);
    }

    private void initColon(GridBagConstraints constraints, GridBagLayout layout, int row) {
        JLabel label = new JLabel(":");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.NONE, 0.0, 1, row, 1);
        layout.setConstraints(label, constraints);
        add(label);
    }

    private void initComponents() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            homeTeams[i] = new JLabel();
            visitorTeams[i] = new JLabel();
            results[i] = new JLabel();
        }
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(2, 2, 2, 2);

        this.setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));

        JLabel label;

        setLayout(layout);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Heim"));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.HORIZONTAL, 1.0, 0, 0, 1);
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.HORIZONTAL, 0.0, 1, 0, 1);
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Gast"));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.HORIZONTAL, 1.0, 2, 0, 1);
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.result"));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.NONE, 0.5, 3, 0, 3);
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel("");

        // label.setFont( label.getFont().deriveFont( Font.BOLD ) );
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.NONE, 0.0, 4, 0, 1);
        layout.setConstraints(label, constraints);
        add(label);

        for (int j = 0; j < buttons.length; j++) {
            initTeam(homeTeams[j], constraints, layout, j + 1, 0);
            initColon(constraints, layout, j + 1);
            initTeam(visitorTeams[j], constraints, layout, j + 1, 2);
            initResultLabel(results[j], constraints, layout, j + 1);
            initButton(buttons[j], constraints, layout, j + 1);
        }

        fillLabels();
    }

    private void initResultLabel(JLabel label, GridBagConstraints constraints,
                                 GridBagLayout layout, int row) {
        setConstraintsValues(constraints, GridBagConstraints.NONE, 0.5, 3, row, 3);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        layout.setConstraints(label, constraints);
        add(label);
    }

    private void initTeam(JLabel label, GridBagConstraints constraints, GridBagLayout layout,
                          int row, int column) {
        setConstraintsValues(constraints, GridBagConstraints.HORIZONTAL, 1.0, column, row, 1);
        layout.setConstraints(label, constraints);
        add(label);
    }

    private void markMyTeam(JLabel team, int matchTeamId, int myTeamId) {
        if (matchTeamId == myTeamId) {
            team.setFont(team.getFont().deriveFont(Font.BOLD));
            team.setForeground(ThemeManager.getColor(HOColorName.TEAM_FG));
        }
    }

    private void markSelectedTeam(JLabel team, String teamName) {
        if (teamName.equals(this.model.getCurrentTeam())) {
            team.setOpaque(true);
            team.setBackground(HODefaultTableCellRenderer.SELECTION_BG);
            team.setForeground(HODefaultTableCellRenderer.SELECTION_FG);
        }
    }

    private void resetMarkierung() {
        for (int i = 0; i < buttons.length; i++) {
            resetMarkup(homeTeams[i]);
            resetMarkup(visitorTeams[i]);
        }
    }

    private void resetMarkup(JLabel label) {
        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        label.setForeground(foreground);
        label.setOpaque(false);
    }
}
