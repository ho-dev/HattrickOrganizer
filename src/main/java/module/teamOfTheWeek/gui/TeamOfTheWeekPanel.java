// %487704913:hoplugins.toTW%
package module.teamOfTheWeek.gui;

import core.db.DBManager;
import core.db.JDBCAdapter;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyPanel;
import core.gui.comp.panel.RasenPanel;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.series.Paarung;
import module.series.Spielplan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TeamOfTheWeekPanel extends LazyPanel implements ChangeListener, ActionListener {

	private static final long serialVersionUID = 7990572479100871307L;
	private LineupPanel bestOfWeek;
	private LineupPanel bestOfYear;
	private LineupPanel worstOfWeek;
	private LineupPanel worstOfYear;
	private JComboBox seasonCombo;
	private JSpinner weekSpinner;

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
		MatchLineupPlayer[] sl = calcBestLineup(week, true);

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

	private MatchLineupPlayer[] calcBestLineup(int week, boolean best) {
		Spielplan plan = (Spielplan) seasonCombo.getSelectedItem();
		if (plan == null) {
			return null;
		}
		Map<String, MatchLineupPlayer> spieler = getPlayers(week, plan, best);
		MatchLineupPlayer[] mlp = new MatchLineupPlayer[11];

		for (int i = 0; i < 11; i++) {
			mlp[i] = spieler.get("" + (i + 1));
		}

		return mlp;
	}

	private JLabel createLabel(String text, Color farbe, int Bordertype) {
		JLabel bla = new JLabel(text);
		bla.setHorizontalAlignment(0);
		bla.setForeground(farbe);
		bla.setBorder(BorderFactory.createEtchedBorder(Bordertype));
		return bla;
	}

	private void fillLineup(LineupPanel lineupPanel, MatchLineupPlayer[] sl, boolean noStars) {
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

		lineupPanel.setTeamName("");
		lineupPanel.updateUI();
	}

	private void fillPanel(JPanel panel, MatchLineupPlayer mlp) {
		fillPanel(panel, mlp, false);
	}

	private void fillPanel(JPanel panel, MatchLineupPlayer mlp, boolean noStars) {
		panel.setOpaque(false);

		String posi = MatchRoleID.getNameForPosition((byte) mlp.getPositionCode());

		panel.removeAll();

		JLabel spielername = createLabel(mlp.getNname(), Color.black, 1);
		JLabel teamname = createLabel(mlp.getTeamName(), Color.black, 1);
		JLabel position = createLabel(posi, Color.black, 0);
		position.setOpaque(false);

		JPanel spielerdetails = new JPanel();
		spielerdetails.setBorder(BorderFactory.createEtchedBorder());
		spielerdetails.setBackground(Color.white);
		spielerdetails.setLayout(new BorderLayout());
		spielerdetails.add(spielername, BorderLayout.NORTH);
		spielerdetails.add(teamname, BorderLayout.SOUTH);
		JPanel sternzahl = (JPanel) new core.gui.comp.entry.RatingTableEntry(
				toInt(mlp.getRating())).getComponent(false);
		sternzahl.setOpaque(false);
		sternzahl.setBorder(BorderFactory.createEtchedBorder());

		JPanel leftPanel = new JPanel();
		leftPanel.setOpaque(false);
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		leftPanel.setPreferredSize(new Dimension(180, 80));
		leftPanel.add(position, BorderLayout.NORTH);
		leftPanel.add(spielerdetails, BorderLayout.CENTER);

		if (!noStars) {
			leftPanel.add(sternzahl, BorderLayout.SOUTH);
		}

		JPanel mainPanel = new ImagePanel();
		mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		mainPanel.add(leftPanel, BorderLayout.CENTER);

		panel.add(mainPanel);
	}

	private void fillSeasonCombo(JComboBox seasonCombo) {
		final Spielplan[] spielplaene = DBManager.instance().getAllSpielplaene(true);
		for (int i = 0; i < spielplaene.length; i++) {
			seasonCombo.addItem(spielplaene[i]);
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

	private int toInt(float i) {
		return (int) (i * 2.0F);
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
			week = 1;
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

	private Map<String, MatchLineupPlayer> getPlayers(int week, Spielplan plan, boolean isBest) {
		JDBCAdapter db = DBManager.instance().getAdapter();
		List<Paarung> matchIDs;
		if (week > 0)
			matchIDs = plan.getPaarungenBySpieltag(week);
		else {
			matchIDs = plan.getPaarungenBySpieltag(1);
			for (week = 2; week < 15; week++)
				matchIDs.addAll(plan.getPaarungenBySpieltag(week));
		}
		// TODO For match of year attention of doubles
		Map<String, MatchLineupPlayer> spieler = new HashMap<String, MatchLineupPlayer>();
		List<MatchLineupPlayer> players = getPlayetAt(db, matchIDs, IMatchRoleID.KEEPER, 1,
				isBest);
		spieler.put("1", players.get(0));
		players = getPlayetAt(db, matchIDs, IMatchRoleID.BACK, 2, isBest);
		spieler.put("2", players.get(0));
		spieler.put("5", players.get(1));
		players = getPlayetAt(db, matchIDs, IMatchRoleID.CENTRAL_DEFENDER, 2, isBest);
		spieler.put("3", players.get(0));
		spieler.put("4", players.get(1));
		players = getPlayetAt(db, matchIDs, IMatchRoleID.WINGER, 2, isBest);
		spieler.put("6", players.get(0));
		spieler.put("9", players.get(1));
		players = getPlayetAt(db, matchIDs, IMatchRoleID.MIDFIELDER, 2, isBest);
		spieler.put("7", players.get(0));
		spieler.put("8", players.get(1));
		players = getPlayetAt(db, matchIDs, IMatchRoleID.FORWARD, 2, isBest);
		spieler.put("10", players.get(0));
		spieler.put("11", players.get(1));
		return spieler;
	}

	private List<MatchLineupPlayer> getPlayetAt(JDBCAdapter db, List<Paarung> matchIDs,
			int position, int number, boolean isBest) {
		ResultSet rs;
		String posClase = "";

		switch (position) {
		case IMatchRoleID.KEEPER: {
			posClase += " FIELDPOS=" + IMatchRoleID.keeper + " ";
			break;
		}

		case IMatchRoleID.CENTRAL_DEFENDER: {
			posClase += " (FIELDPOS=" + IMatchRoleID.leftCentralDefender + " OR FIELDPOS="
					+ IMatchRoleID.middleCentralDefender + " OR FIELDPOS="
					+ IMatchRoleID.rightCentralDefender + ") ";
			break;
		}

		case IMatchRoleID.BACK: {
			posClase += " (FIELDPOS=" + IMatchRoleID.leftBack + " OR FIELDPOS="
					+ IMatchRoleID.rightBack + ") ";
			break;
		}

		case IMatchRoleID.WINGER: {
			posClase += " (FIELDPOS=" + IMatchRoleID.leftWinger + " OR FIELDPOS="
					+ IMatchRoleID.rightWinger + ") ";
			break;
		}

		case IMatchRoleID.MIDFIELDER: {
			posClase += " (FIELDPOS=" + IMatchRoleID.leftInnerMidfield + " OR FIELDPOS="
					+ IMatchRoleID.centralInnerMidfield + " OR FIELDPOS="
					+ IMatchRoleID.rightInnerMidfield + ") ";
			break;
		}

		case IMatchRoleID.FORWARD: {
			posClase += " (FIELDPOS=" + IMatchRoleID.leftForward + " OR FIELDPOS="
					+ IMatchRoleID.centralForward + " OR FIELDPOS="
					+ IMatchRoleID.rightForward + ") ";
			break;
		}
		}

		String matchClause = "";
		for (int i = 0; i < matchIDs.size(); i++) {
			if (matchClause.length() > 1) {
				matchClause += " OR ";
			}
			matchClause += " MATCHID=" + matchIDs.get(i).getMatchId();
		}

		String sql = "SELECT DISTINCT MATCHID, SPIELERID, NAME, RATING, HOPOSCODE, TEAMID FROM MATCHLINEUPPLAYER WHERE "
				+ posClase;
		if (matchClause.length() > 1) {
			sql += " AND (" + matchClause + ") ";
		}
		sql += "ORDER BY RATING ";

		if (isBest) {
			sql += " DESC";
		} else {
			sql += " ASC";
		}

		rs = db.executeQuery(sql);

		List<MatchLineupPlayer> ret = new ArrayList<MatchLineupPlayer>();

		for (int i = 0; i < number; i++) {
			ret.add(new MatchLineupPlayer(rs, matchIDs));
		}

		return ret;
	}
}
