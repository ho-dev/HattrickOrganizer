// %487704913:hoplugins.toTW%
package module.teamOfTheWeek.gui;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyPanel;
import core.gui.comp.panel.RasenPanel;
import core.model.HOVerwaltung;
import core.model.match.MatchLineupPosition;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.series.Paarung;
import module.series.Spielplan;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TeamOfTheWeekPanel extends LazyPanel implements ChangeListener, ActionListener {

	@Serial
	private static final long serialVersionUID = 7990572479100871307L;
	private LineupPanel bestOfWeek;
	private LineupPanel bestOfYear;
	private LineupPanel worstOfWeek;
	private LineupPanel worstOfYear;
	private JComboBox seasonCombo;
	private JSpinner weekSpinner;

	private List<Paarung> matches;

	@Override
	protected void initialize() {
		initComponents();
	}

	@Override
	protected void update() {
		// do nothing
	}
	
	private void reloadData(boolean isSeason) {
		int week = ((Number) weekSpinner.getValue()).intValue();
		var sl = calcBestLineup(week, true);

		if (sl == null) {
			return;
		}

		fillLineup(bestOfWeek, sl, false);
		sl = calcBestLineup(week, false);
		fillLineup(worstOfWeek, sl, false);

		if (isSeason) {
			sl = calcBestLineup(-1, true);
			fillLineup(bestOfYear, sl, false);
			sl = calcBestLineup(-1, false);
			fillLineup(worstOfYear, sl, false);
		}
	}

	private MatchLineupPosition[] calcBestLineup(int week, boolean best) {
		Spielplan plan = (Spielplan) seasonCombo.getSelectedItem();
		if (plan == null) {
			return null;
		}
		return  getPlayers(week, plan, best);
	}

	private JLabel createLabel(String text, Color farbe, int Bordertype) {
		JLabel bla = new JLabel(text);
		bla.setHorizontalAlignment(0);
		bla.setForeground(farbe);
		bla.setBorder(BorderFactory.createEtchedBorder(Bordertype));
		return bla;
	}

	private void fillLineup(LineupPanel lineupPanel, MatchLineupPosition[] sl, boolean noStars) {
		if (!noStars) {
			fillPanel(lineupPanel.getKeeperPanel(), sl[0]);
			fillPanel(lineupPanel.getLeftWingbackPanel(), sl[1]);
			fillPanel(lineupPanel.getLeftCentralDefenderPanel(), sl[2]);
			fillPanel(lineupPanel.getRightCentralDefenderPanel(), sl[3]);
			fillPanel(lineupPanel.getRightWingbackPanel(), sl[4]);
			fillPanel(lineupPanel.getLeftWingPanel(), sl[5]);
			fillPanel(lineupPanel.getLeftMidfieldPanel(), sl[6]);
			fillPanel(lineupPanel.getRightMidfieldPanel(), sl[7]);
			fillPanel(lineupPanel.getRightWingPanel(), sl[8]);
			fillPanel(lineupPanel.getLeftForwardPanel(), sl[9]);
			fillPanel(lineupPanel.getRightForwardPanel(), sl[10]);
		} else {
			fillPanel(lineupPanel.getKeeperPanel(), sl[0], true);
			fillPanel(lineupPanel.getLeftWingbackPanel(), sl[1], true);
			fillPanel(lineupPanel.getLeftCentralDefenderPanel(), sl[2], true);
			fillPanel(lineupPanel.getRightCentralDefenderPanel(), sl[3], true);
			fillPanel(lineupPanel.getRightWingbackPanel(), sl[4], true);
			fillPanel(lineupPanel.getLeftWingPanel(), sl[5], true);
			fillPanel(lineupPanel.getLeftMidfieldPanel(), sl[6], true);
			fillPanel(lineupPanel.getRightMidfieldPanel(), sl[7], true);
			fillPanel(lineupPanel.getRightWingPanel(), sl[8], true);
			fillPanel(lineupPanel.getLeftForwardPanel(), sl[9], true);
			fillPanel(lineupPanel.getRightForwardPanel(), sl[10], true);
		}
		lineupPanel.updateUI();
	}

	private void fillPanel(JComponent panel, MatchLineupPosition mlp) {
		fillPanel(panel, mlp, false);
	}

	private void fillPanel(JComponent panel, MatchLineupPosition mlp, boolean noStars) {
		if (mlp == null) return;
		var playerDetailsPanel = new JLayeredPane();
		playerDetailsPanel.setOpaque(true);

		String posi = MatchRoleID.getNameForPosition(mlp.getPosition());

		panel.removeAll();

		JLabel spielername = createLabel(mlp.getSpielerName(), Color.black, 1);
		JLabel teamname = createLabel(getTeamName(mlp.getTeamId()), Color.black, 1);
		JLabel position = createLabel(posi, Color.black, 0);
		position.setOpaque(false);

		playerDetailsPanel.setLayout(new GridBagLayout());
		var constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weightx = 1;

		playerDetailsPanel.setBorder(BorderFactory.createEtchedBorder());
		playerDetailsPanel.setBackground(Color.white);
		constraints.gridy = 0;
		playerDetailsPanel.add(position, constraints);
		constraints.gridy++;
		playerDetailsPanel.add(spielername, constraints);
		constraints.gridy++;
		playerDetailsPanel.add(teamname, constraints);
		JPanel sternzahl = (JPanel) new core.gui.comp.entry.RatingTableEntry(
				toInt(mlp.getRating())).getComponent(false);
		sternzahl.setOpaque(false);
		sternzahl.setBorder(BorderFactory.createEtchedBorder());

		if (!noStars) {
			constraints.gridy++;
			playerDetailsPanel.add(sternzahl, constraints);
		}

		panel.add(playerDetailsPanel);
	}

	private String getTeamName(int teamId) {
		for ( var match : matches){
			if ( match.getHeimId() == teamId) return match.getHeimName();
			else if ( match.getGastId() == teamId) return match.getGastName();
		}
		return "";
	}

	private void fillSeasonCombo(JComboBox seasonCombo) {
		var spielplaene = DBManager.instance().getAllSpielplaene(true);
		for (var fixture : spielplaene) {
			seasonCombo.addItem(fixture);
		}
	}

	private void initComponents() {
		JPanel m_jpPanel = new ImagePanel();
		m_jpPanel.setOpaque(false);
		seasonCombo = new JComboBox();
		fillSeasonCombo(seasonCombo);
		seasonCombo.addActionListener(this);

		weekSpinner = new JSpinner();
		weekSpinner.addChangeListener(this);
		weekSpinner.setPreferredSize(new Dimension(60, 22));
		weekSpinner.setFocusable(false);

		JLabel jl = new JLabel(HOVerwaltung.instance().getLanguageString("Spieltag"));
		JPanel north = new ImagePanel();
		jl.setForeground(Color.BLACK);
		jl.setLabelFor(weekSpinner);
		north.add(jl);
		north.add(weekSpinner);
		north.add(seasonCombo);
		north.setOpaque(true);
		m_jpPanel.setLayout(new BorderLayout());
		m_jpPanel.add(north, BorderLayout.NORTH);

		JTabbedPane tab = new JTabbedPane();
		bestOfWeek = new LineupPanel();
		tab.addTab(HOVerwaltung.instance().getLanguageString("bestOfWeek"),
				createTabPanel(bestOfWeek));
		worstOfWeek = new LineupPanel();
		tab.addTab(HOVerwaltung.instance().getLanguageString("worstOfWeek"),
				createTabPanel(worstOfWeek));
		bestOfYear = new LineupPanel();
		tab.addTab(HOVerwaltung.instance().getLanguageString("bestOfSeason"),
				createTabPanel(bestOfYear));
		worstOfYear = new LineupPanel();
		tab.addTab(HOVerwaltung.instance().getLanguageString("worstOfSeason"),
				createTabPanel(worstOfYear));
		m_jpPanel.add(tab, BorderLayout.CENTER);
		setLayout(new BorderLayout());
		add(m_jpPanel, BorderLayout.CENTER);
		
		setWeekLimits();
		reloadData(true);
	}

	private JPanel createTabPanel(LineupPanel p) {
		RasenPanel panel = new RasenPanel(new BorderLayout());
		panel.add(p, BorderLayout.CENTER);
		return panel;

	}

	private int toInt(double i) {
		return (int) (i * 2.0);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		reloadData(false);
		updateUI();
	}

	private void setWeekLimits() {
		Spielplan plan = (Spielplan) seasonCombo.getSelectedItem();
		int max_week = HOVerwaltung.instance().getModel().getBasics().getSpieltag() - 1;
		int week = 1;

		if (max_week < 1)
			max_week = 1;

		try {
			if (HOVerwaltung.instance().getModel().getBasics().getSeason() != plan.getSaison())
				max_week = 14;
			else
				week = max_week;
		} catch (Exception e) {
			/* new database */
			max_week = 1;
		}

		weekSpinner.setModel(new SpinnerNumberModel(week, 1, max_week, 1));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		setWeekLimits();
		reloadData(true);
		updateUI();

	}

	private MatchLineupPosition[] getPlayers(int week, Spielplan plan, boolean isBest) {
		if (week > 0)
			matches = plan.getPaarungenBySpieltag(week);
		else {
			matches = plan.getPaarungenBySpieltag(1);
			for (week = 2; week < 15; week++)
				matches.addAll(plan.getPaarungenBySpieltag(week));
		}
		// TODO For match of year attention of doubles
		var ret = new MatchLineupPosition[11];
		var players = DBManager.instance().loadTopFlopRatings(matches, IMatchRoleID.KEEPER, 1, isBest);
		ret[0] = getPlayer(players,0);
		players = DBManager.instance().loadTopFlopRatings(matches, IMatchRoleID.BACK, 2, isBest);
		ret[1] = getPlayer(players,0);
		ret[4] = getPlayer(players,1);
		players = DBManager.instance().loadTopFlopRatings(matches, IMatchRoleID.CENTRAL_DEFENDER, 2, isBest);
		ret[2] = getPlayer(players,0);
		ret[3] = getPlayer(players,1);
		players = DBManager.instance().loadTopFlopRatings(matches, IMatchRoleID.WINGER, 2, isBest);
		ret[5] = getPlayer(players,0);
		ret[8] = getPlayer(players,1);
		players = DBManager.instance().loadTopFlopRatings(matches, IMatchRoleID.MIDFIELDER, 2, isBest);
		ret[6] = getPlayer(players,0);
		ret[7] = getPlayer(players,1);
		players = DBManager.instance().loadTopFlopRatings(matches, IMatchRoleID.FORWARD, 2, isBest);
		ret[9] = getPlayer(players,0);
		ret[10] = getPlayer(players,1);
		return ret;
	}

	private MatchLineupPosition getPlayer(List<MatchLineupPosition> players, int index){
		if ( index < players.size()){
			return players.get(index);
		}
		return null;
	}

}
