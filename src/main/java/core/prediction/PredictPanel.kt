// %3942717939:de.hattrickorganizer.gui.matchprediction%
package core.prediction;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.prediction.engine.MatchResult;
import core.util.HOLogger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;


class PredictPanel extends JPanel {
	private static final long serialVersionUID = -9143223883848733076L;

    private JLabel m_jlGesamtChancenGuest;
    private JLabel m_jlGesamtChancenHome;
    private JLabel m_jlGesamtToreGuest;
    private JLabel m_jlGesamtToreHome;
    private JLabel m_jlGewonnen;
    private JLabel m_jlGuestTeam;
    private JLabel m_jlHomeTeam;
    private JLabel m_jlLinksChancenGuest;
    private JLabel m_jlLinksChancenHome;
    private JLabel m_jlLinksToreGuest;
    private JLabel m_jlLinksToreHome;
    private JLabel m_jlMitteChancenGuest;
    private JLabel m_jlMitteChancenHome;
    private JLabel m_jlMitteToreGuest;
    private JLabel m_jlMitteToreHome;
    private JLabel m_jlRechtsChancenGuest;
    private JLabel m_jlRechtsChancenHome;
    private JLabel m_jlRechtsToreGuest;
    private JLabel m_jlRechtsToreHome;
    private JLabel m_jlUnendschieden;
    private JLabel m_jlVerloren;
    private JProgressBar m_jpbGesamtGuest;
    private JProgressBar m_jpbGesamtHome;
    private JProgressBar m_jpbGewonnen;
    private JProgressBar m_jpbLinksGuest;
    private JProgressBar m_jpbLinksHome;
    private JProgressBar m_jpbMitteGuest;
    private JProgressBar m_jpbMitteHome;
    private JProgressBar m_jpbRechtsGuest;
    private JProgressBar m_jpbRechtsHome;
    private JProgressBar m_jpbUnendschieden;
    private JProgressBar m_jpbVerloren;

    PredictPanel(String hometeam, String guestteam) {
        m_jlHomeTeam = new JLabel(hometeam, SwingConstants.CENTER);
        m_jlGuestTeam = new JLabel(guestteam, SwingConstants.CENTER);
        initComponents();
    }

    public final void setGuestTeamName(String teamname) {
    	m_jlGuestTeam.setText(getShortenedTeamName(teamname));
    }

    public final void setHomeTeamName(String teamname) {
    	m_jlHomeTeam.setText(getShortenedTeamName(teamname));
    }

    private String getShortenedTeamName(String in) {
	    try {
	    	if (in != null) {
	    		int pos = in.lastIndexOf("(");
	    		String id = "";
	    		String team = in;
	    		if (pos > -1) {
	    			id = in.substring(pos).trim();
	    			team = in.substring(0, pos-1).trim();
	    		}
	    		if (team.length() > 25) {
	    			return team.substring(0, 24) + ". " + id;
	    		}
	    	}
	    } catch (Exception e) {
	    	HOLogger.instance().log(getClass(), "Error parsing team name (" + in + "): " + e);
	    }
    	return in;
    }

	public final void refresh(MatchResult mr) {

        m_jlGewonnen.setText(mr.getHomeWin() + "");
        m_jlUnendschieden.setText(mr.getDraw() + "");
        m_jlVerloren.setText(mr.getAwayWin() + "");
        m_jpbGewonnen.setValue((mr.getHomeWin() * 100) / (mr.getHomeWin() + mr.getAwayWin() + mr.getDraw()));
        m_jpbUnendschieden.setValue((mr.getDraw() * 100) / (mr.getHomeWin() + mr.getAwayWin() + mr.getDraw()));
        m_jpbVerloren.setValue((mr.getAwayWin() * 100) / (mr.getHomeWin() + mr.getAwayWin() + mr.getDraw()));

        m_jlGesamtToreHome.setText(mr.getHomeGoals() + "");
        m_jlGesamtChancenHome.setText("(" + mr.getHomeChances() + ")");
        m_jpbGesamtHome.setValue((mr.getHomeGoals() * 100) / Math.max(1, mr.getHomeChances()));
        m_jlGesamtToreGuest.setText(mr.getGuestGoals() + "");
        m_jlGesamtChancenGuest.setText("(" + mr.getGuestChances() + ")");
        m_jpbGesamtGuest.setValue((mr.getGuestGoals() * 100) / Math.max(1, mr.getGuestChances()));

        m_jlRechtsToreHome.setText(mr.getHomeSuccess()[2] + "");
        m_jlRechtsChancenHome.setText("(" + (mr.getHomeFailed()[2] + mr.getHomeSuccess()[2]) + ")");
        m_jpbRechtsHome.setValue((mr.getHomeSuccess()[2] * 100) / (mr.getHomeFailed()[2]
                                 + mr.getHomeSuccess()[2]));
        m_jlRechtsToreGuest.setText(mr.getGuestSuccess()[2] + "");
        m_jlRechtsChancenGuest.setText("(" + (mr.getGuestFailed()[2] + mr.getGuestSuccess()[2]) + ")");
        m_jpbRechtsGuest.setValue((mr.getGuestSuccess()[2] * 100) / (mr.getGuestFailed()[2]
                                  + mr.getGuestSuccess()[2]));

        m_jlMitteToreHome.setText(mr.getHomeSuccess()[1] + "");
        m_jlMitteChancenHome.setText("(" + (mr.getHomeFailed()[1] + mr.getHomeSuccess()[1]) + ")");
        m_jpbMitteHome.setValue((mr.getHomeSuccess()[1] * 100) / (mr.getHomeFailed()[1]
                                + mr.getHomeSuccess()[1]));
        m_jlMitteToreGuest.setText(mr.getGuestSuccess()[1] + "");
        m_jlMitteChancenGuest.setText("(" + (mr.getGuestFailed()[1] + mr.getGuestSuccess()[1]) + ")");
        m_jpbMitteGuest.setValue((mr.getGuestSuccess()[1] * 100) / (mr.getGuestFailed()[1]
                                 + mr.getGuestSuccess()[1]));

        m_jlLinksToreHome.setText(mr.getHomeSuccess()[0] + "");
        m_jlLinksChancenHome.setText("(" + (mr.getHomeFailed()[0] + mr.getHomeSuccess()[0]) + ")");
        m_jpbLinksHome.setValue((mr.getHomeSuccess()[0] * 100) / (mr.getHomeFailed()[0]
                                + mr.getHomeSuccess()[0]));
        m_jlLinksToreGuest.setText(mr.getGuestSuccess()[0] + "");
        m_jlLinksChancenGuest.setText("(" + (mr.getGuestFailed()[0] + mr.getGuestSuccess()[0]) + ")");
        m_jpbLinksGuest.setValue((mr.getGuestSuccess()[0] * 100) / (mr.getGuestFailed()[0]
                                 + mr.getGuestSuccess()[0]));
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));
        setBorder(BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)));

        final JPanel toppanel = new JPanel();
        toppanel.setOpaque(false);

        final GridBagLayout toplayout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(2, 2, 2, 2);
        toppanel.setLayout(toplayout);

        JLabel label;

        constraints.gridx = 0;
        constraints.gridy = 1;
        label = new JLabel(core.model.HOVerwaltung.instance().getLanguageString("Heim"));
        label.setFont(m_jlHomeTeam.getFont().deriveFont(Font.BOLD));
        toppanel.add(label, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        m_jlGewonnen = new JLabel("", SwingConstants.CENTER);
        toppanel.add(m_jlGewonnen, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;
        label = new JLabel(core.model.HOVerwaltung.instance().getLanguageString("Unendschieden"));
        label.setFont(m_jlHomeTeam.getFont().deriveFont(Font.BOLD));
        toppanel.add(label, constraints);

        constraints.gridx = 3;
        constraints.gridy = 1;
        m_jlUnendschieden = new JLabel("", SwingConstants.CENTER);
        toppanel.add(m_jlUnendschieden, constraints);

        constraints.gridx = 4;
        constraints.gridy = 1;
        label = new JLabel(core.model.HOVerwaltung.instance().getLanguageString("Gast"));
        label.setFont(m_jlHomeTeam.getFont().deriveFont(Font.BOLD));
        toppanel.add(label, constraints);

        constraints.gridx = 5;
        constraints.gridy = 1;
        m_jlVerloren = new JLabel("", SwingConstants.CENTER);
        toppanel.add(m_jlVerloren, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        m_jpbGewonnen = new JProgressBar(0, 100);
        m_jpbGewonnen.setStringPainted(true);
        m_jpbGewonnen.setPreferredSize(new Dimension(100, 16));
        toppanel.add(m_jpbGewonnen, constraints);

        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        m_jpbUnendschieden = new JProgressBar(0, 100);
        m_jpbUnendschieden.setStringPainted(true);
        m_jpbUnendschieden.setPreferredSize(new Dimension(100, 16));
        toppanel.add(m_jpbUnendschieden, constraints);

        constraints.gridx = 4;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        m_jpbVerloren = new JProgressBar(0, 100);
        m_jpbVerloren.setStringPainted(true);
        m_jpbVerloren.setPreferredSize(new Dimension(100, 16));
        toppanel.add(m_jpbVerloren, constraints);

        add(toppanel, BorderLayout.NORTH);

        ////////////////////////////////////////////////////////////////////////
        final JPanel bottompanel = new JPanel();
        bottompanel.setOpaque(false);

        final GridBagLayout bottomplayout = new GridBagLayout();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(2, 2, 2, 2);
        bottompanel.setLayout(bottomplayout);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        m_jlHomeTeam.setFont(m_jlHomeTeam.getFont().deriveFont(Font.BOLD));
        bottompanel.add(m_jlHomeTeam, constraints);

        constraints.gridx = 5;
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        m_jlGuestTeam.setFont(m_jlGuestTeam.getFont().deriveFont(Font.BOLD));
        bottompanel.add(m_jlGuestTeam, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        label = new JLabel(core.model.HOVerwaltung.instance().getLanguageString("Gesamt"));
        bottompanel.add(label, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        m_jlGesamtToreHome = new JLabel("", SwingConstants.RIGHT);
        m_jlGesamtToreHome.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlGesamtToreHome, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        m_jlGesamtChancenHome = new JLabel("", SwingConstants.RIGHT);
        m_jlGesamtChancenHome.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlGesamtChancenHome, constraints);

        constraints.gridx = 3;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        m_jpbGesamtHome = new JProgressBar(0, 100);
        m_jpbGesamtHome.setStringPainted(true);
        m_jpbGesamtHome.setPreferredSize(new Dimension(40, 16));
        bottompanel.add(m_jpbGesamtHome, constraints);

        constraints.gridx = 4;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        label = new JLabel(" ");
        bottompanel.add(label, constraints);

        constraints.gridx = 5;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        m_jlGesamtToreGuest = new JLabel("", SwingConstants.RIGHT);
        m_jlGesamtToreGuest.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlGesamtToreGuest, constraints);

        constraints.gridx = 6;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        m_jlGesamtChancenGuest = new JLabel("", SwingConstants.RIGHT);
        m_jlGesamtChancenGuest.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlGesamtChancenGuest, constraints);

        constraints.gridx = 7;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        m_jpbGesamtGuest = new JProgressBar(0, 100);
        m_jpbGesamtGuest.setStringPainted(true);
        m_jpbGesamtGuest.setPreferredSize(new Dimension(40, 16));
        bottompanel.add(m_jpbGesamtGuest, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        label = new JLabel(core.model.HOVerwaltung.instance().getLanguageString("Rechts"));
        bottompanel.add(label, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        m_jlRechtsToreHome = new JLabel("", SwingConstants.RIGHT);
        m_jlRechtsToreHome.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlRechtsToreHome, constraints);

        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        m_jlRechtsChancenHome = new JLabel("", SwingConstants.RIGHT);
        m_jlRechtsChancenHome.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlRechtsChancenHome, constraints);

        constraints.gridx = 3;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        m_jpbRechtsHome = new JProgressBar(0, 100);
        m_jpbRechtsHome.setStringPainted(true);
        m_jpbRechtsHome.setPreferredSize(new Dimension(40, 16));
        bottompanel.add(m_jpbRechtsHome, constraints);

        constraints.gridx = 4;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        label = new JLabel(" ");
        bottompanel.add(label, constraints);

        constraints.gridx = 5;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        m_jlRechtsToreGuest = new JLabel("", SwingConstants.RIGHT);
        m_jlRechtsToreGuest.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlRechtsToreGuest, constraints);

        constraints.gridx = 6;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        m_jlRechtsChancenGuest = new JLabel("", SwingConstants.RIGHT);
        m_jlRechtsChancenGuest.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlRechtsChancenGuest, constraints);

        constraints.gridx = 7;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        m_jpbRechtsGuest = new JProgressBar(0, 100);
        m_jpbRechtsGuest.setStringPainted(true);
        m_jpbRechtsGuest.setPreferredSize(new Dimension(40, 16));
        bottompanel.add(m_jpbRechtsGuest, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        label = new JLabel(core.model.HOVerwaltung.instance().getLanguageString("Mitte"));
        bottompanel.add(label, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        m_jlMitteToreHome = new JLabel("", SwingConstants.RIGHT);
        m_jlMitteToreHome.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlMitteToreHome, constraints);

        constraints.gridx = 2;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        m_jlMitteChancenHome = new JLabel("", SwingConstants.RIGHT);
        m_jlMitteChancenHome.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlMitteChancenHome, constraints);

        constraints.gridx = 3;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        m_jpbMitteHome = new JProgressBar(0, 100);
        m_jpbMitteHome.setStringPainted(true);
        m_jpbMitteHome.setPreferredSize(new Dimension(40, 16));
        bottompanel.add(m_jpbMitteHome, constraints);

        constraints.gridx = 4;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        label = new JLabel(" ");
        bottompanel.add(label, constraints);

        constraints.gridx = 5;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        m_jlMitteToreGuest = new JLabel("", SwingConstants.RIGHT);
        m_jlMitteToreGuest.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlMitteToreGuest, constraints);

        constraints.gridx = 6;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        m_jlMitteChancenGuest = new JLabel("", SwingConstants.RIGHT);
        m_jlMitteChancenGuest.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlMitteChancenGuest, constraints);

        constraints.gridx = 7;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        m_jpbMitteGuest = new JProgressBar(0, 100);
        m_jpbMitteGuest.setStringPainted(true);
        m_jpbMitteGuest.setPreferredSize(new Dimension(40, 16));
        bottompanel.add(m_jpbMitteGuest, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        label = new JLabel(core.model.HOVerwaltung.instance().getLanguageString("Links"));
        bottompanel.add(label, constraints);

        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        m_jlLinksToreHome = new JLabel("", SwingConstants.RIGHT);
        m_jlLinksToreHome.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlLinksToreHome, constraints);

        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        m_jlLinksChancenHome = new JLabel("", SwingConstants.RIGHT);
        m_jlLinksChancenHome.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlLinksChancenHome, constraints);

        constraints.gridx = 3;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        m_jpbLinksHome = new JProgressBar(0, 100);
        m_jpbLinksHome.setStringPainted(true);
        m_jpbLinksHome.setPreferredSize(new Dimension(40, 16));
        bottompanel.add(m_jpbLinksHome, constraints);

        constraints.gridx = 4;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        label = new JLabel(" ");
        bottompanel.add(label, constraints);

        constraints.gridx = 5;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        m_jlLinksToreGuest = new JLabel("", SwingConstants.RIGHT);
        m_jlLinksToreGuest.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlLinksToreGuest, constraints);

        constraints.gridx = 6;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        m_jlLinksChancenGuest = new JLabel("", SwingConstants.RIGHT);
        m_jlLinksChancenGuest.setPreferredSize(new Dimension(25, 16));
        bottompanel.add(m_jlLinksChancenGuest, constraints);

        constraints.gridx = 7;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        m_jpbLinksGuest = new JProgressBar(0, 100);
        m_jpbLinksGuest.setStringPainted(true);
        m_jpbLinksGuest.setPreferredSize(new Dimension(40, 16));
        bottompanel.add(m_jpbLinksGuest, constraints);

        add(bottompanel, BorderLayout.SOUTH);
    }
}
