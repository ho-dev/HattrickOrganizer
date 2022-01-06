package module.matches;

import core.datatype.CBItem;
import core.db.DBManager;
import core.gui.CursorToolkit;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.MatchesColumnModel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.IMatchDetails;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineupPosition;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import core.module.IModule;
import core.net.OnlineWorker;
import core.prediction.MatchEnginePanel;
import core.prediction.MatchPredictionDialog;
import core.prediction.engine.MatchPredictionManager;
import core.prediction.engine.TeamData;
import core.prediction.engine.TeamRatings;
import core.util.Helper;
import module.lineup.Lineup;
import module.matches.statistics.MatchesHighlightsTable;
import module.matches.statistics.MatchesOverviewTable;
import module.teamAnalyzer.ui.RatingUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

import static core.util.Helper.getTranslation;

public final class MatchesPanel extends LazyImagePanel {

	/** Only played Matches of suplied team (unsupported for now) */
	public static final int NUR_GESPIELTEN_SPIELE = 10;
	/** Only tournament matches of supplied team */
	public static final int NUR_EIGENE_TOURNAMENTSPIELE = 7;
	/** Only Other Team Matches*/
	public static final int OTHER_TEAM_MATCHS = 6;
	/** Only friendly Matches of suplied team */
	public static final int NUR_EIGENE_FREUNDSCHAFTSSPIELE = 5;
	/** Only league Matches of suplied team */
	public static final int NUR_EIGENE_LIGASPIELE = 4;
	/** Only national cup Matches of suplied team */
	public static final int ONLY_NATIONAL_CUP = 3;
	/** Only cup +league + quali Matches of suplied team */
	public static final int OWN_OFFICIAL_GAMES = 2;
	/** Only Matches of suplied team */
	public static final int OWN_GAMES = 1;
	/** Only Secondary cup matchs */
	public static final int ONLY_SECONDARY_CUP = 9;
	/** Only Qualifification matchs */
	public static final int ONLY_QUALIF_MATCHES = 8;
	public static final int ALL_MATCHS = 0;
	private static final long serialVersionUID = -6337569355347545083L;
	private AufstellungsSternePanel aufstellungGastPanel;
	private AufstellungsSternePanel aufstellungHeimPanel;
	private JButton adoptLineupButton;
	private JButton deleteButton;
	private JButton reloadMatchButton;
	private JButton simulateMatchButton;
	private JComboBox m_jcbSpieleFilter;

	private JPanel matchesOverviewPanel;
	private JPanel matchesLocationButtonsPanel;

	private JPanel linupPanel;
	private JSplitPane horizontalLeftSplitPane;
	private JSplitPane verticalSplitPane;
	private JTabbedPane matchDetailsTabbedPane;
	private TeamsRatingPanel m_jpTeamsRatingPanel;
	private MatchReportPanel matchReportPanel;
	private SpielHighlightPanel matchHighlightPanel;
	private MatchesTable matchesTable;
	private MatchesOverviewTable matchesOverviewTable;
	private MatchesHighlightsTable matchesHighlightsTable;
	private StaerkenvergleichPanel teamsComparePanel;
	private MatchesModel matchesModel;
	private boolean initialized = false;
	private boolean needsRefresh = false;

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		registerRefreshable(true);
		setNeedsRefresh(true);
	}

	@Override
	protected void update() {
		doReInit();
	}


	private void addListeners() {
		this.reloadMatchButton.addActionListener(e -> {
			int matchid = matchesModel.getMatch().getMatchID();
			OnlineWorker.downloadMatchData(matchesModel.getMatch().getMatchID(), matchesModel
					.getMatch().getMatchType(), true);
			RefreshManager.instance().doReInit();
			showMatch(matchid);
		});

		this.deleteButton.addActionListener(e -> deleteSelectedMatches());

		this.adoptLineupButton.addActionListener(e -> adoptLineup());

		this.simulateMatchButton.addActionListener(e -> simulateMatch());

		this.matchesTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				newSelectionInform();
			}
		});

		HOMainFrame.instance().addApplicationClosingListener(this::saveSettings);

		m_jcbSpieleFilter.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				doReInit();
			}
		});
	}

	private void saveSettings() {
		matchesTable.saveColumnOrder();
		matchesOverviewTable.saveColumnOrder();
		UserParameter parameter = UserParameter.instance();
		parameter.spielePanel_horizontalLeftSplitPane = horizontalLeftSplitPane
				.getDividerLocation();
		parameter.spielePanel_verticalSplitPane = verticalSplitPane.getDividerLocation();
	}

	private void adoptLineup() {
		if ((matchesModel.getMatch() != null)
				&& (matchesModel.getMatch().getMatchStatus() == MatchKurzInfo.FINISHED)) {
			int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
			List<MatchLineupPosition> teamspieler = DBManager.instance().getMatchLineupPlayers(
					matchesModel.getMatch().getMatchID(), matchesModel.getMatch().getMatchType(), teamid);
			Lineup aufstellung = HOVerwaltung.instance().getModel().getCurrentLineupTeamRecalculated().getLineup();

			aufstellung.clearLineup(); // To make sure the old one is
			// gone.

			if (teamspieler != null) {
				for (MatchLineupPosition player : teamspieler) {
					if (player.getRoleId() == IMatchRoleID.setPieces) {
						aufstellung.setKicker(player.getPlayerId());
					} else if (player.getRoleId() == IMatchRoleID.captain) {
						aufstellung.setCaptain(player.getPlayerId());
					} else {
						aufstellung.setSpielerAtPosition(player.getRoleId(), player.getPlayerId(),
								player.getBehaviour());
					}
				}
			}
			// Alles Updaten
			HOMainFrame.instance().getLineupPanel().update();
			// Aufstellung zeigen
			HOMainFrame.instance().showTab(IModule.LINEUP);
		}
	}

	private void deleteSelectedMatches() {
		int[] rows = matchesTable.getSelectedRows();
		MatchKurzInfo[] infos = new MatchKurzInfo[rows.length];

		for (int i = 0; i < rows.length; i++) {
			infos[i] = ((MatchesColumnModel) matchesTable.getSorter().getModel())
					.getMatch((int) ((ColorLabelEntry) matchesTable.getSorter().getValueAt(rows[i],
							7)).getNumber());
		}

		StringBuilder text = new StringBuilder(100);
		text.append(HOVerwaltung.instance().getLanguageString("ls.button.delete"));
		if (infos.length > 1) {
			text.append(" (").append(infos.length).append(" ");
			text.append(HOVerwaltung.instance().getLanguageString("Spiele"));
			text.append(")");
		}
		text.append(":");

		for (int i = 0; (i < infos.length) && (i < 11); i++) {
			text.append("\n").append(infos[i].getHomeTeamName()).append(" - ").append(infos[i].getGuestTeamName());
			if (i == 10) {
				text.append("\n ... ");
			}
		}

		int value = JOptionPane.showConfirmDialog(MatchesPanel.this, text,
				HOVerwaltung.instance().getLanguageString("confirmation.title"), JOptionPane.YES_NO_OPTION);

		if (value == JOptionPane.YES_OPTION) {
			for (MatchKurzInfo info : infos) {
				DBManager.instance().deleteMatch(info.getMatchID());
			}
			RefreshManager.instance().doReInit();
		}
	}

	private void simulateMatch() {
		if (matchesModel.getMatch() != null) {
			Matchdetails details = matchesModel.getDetails();
			MatchPredictionManager manager = MatchPredictionManager.instance();
			int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
			boolean homeMatch = teamId == matchesModel.getMatch().getHomeTeamID();

			TeamRatings homeTeamRatings = manager.generateTeamRatings(
					details != null ? getRatingValue(details.getHomeMidfield()) : 1,
					details != null ? getRatingValue(details.getHomeLeftDef()) : 1,
					details != null ? getRatingValue(details.getHomeMidDef()) : 1,
					details != null ? getRatingValue(details.getHomeRightDef()) : 1,
					details != null ? getRatingValue(details.getHomeLeftAtt()) : 1,
					details != null ? getRatingValue(details.getHomeMidAtt()) : 1,
					details != null ? getRatingValue(details.getHomeRightAtt()) : 1);

			TeamData homeTeamValues;
			if (homeMatch && !ratingsAreKnown(homeTeamRatings)) {
				homeTeamValues = getOwnLineupRatings(manager);
			} else {
				homeTeamValues = manager.generateTeamData(matchesModel.getMatch().getHomeTeamName(),
						homeTeamRatings, details != null ? details.getHomeTacticType()
								: IMatchDetails.TAKTIK_NORMAL,
						details != null ? getRatingValue(details.getHomeTacticSkill() - 1) : 1);
			}

			TeamRatings awayTeamRatings = manager.generateTeamRatings(
					details != null ? getRatingValue(details.getGuestMidfield()) : 1,
					details != null ? getRatingValue(details.getGuestLeftDef()) : 1,
					details != null ? getRatingValue(details.getGuestMidDef()) : 1,
					details != null ? getRatingValue(details.getGuestRightDef()) : 1,
					details != null ? getRatingValue(details.getGuestLeftAtt()) : 1,
					details != null ? getRatingValue(details.getGuestMidAtt()) : 1,
					details != null ? getRatingValue(details.getGuestRightAtt()) : 1);

			TeamData awayTeamValues;
			if (!homeMatch && !ratingsAreKnown(awayTeamRatings)) {
				awayTeamValues = getOwnLineupRatings(manager);
			} else {
				awayTeamValues = manager.generateTeamData(matchesModel.getMatch().getGuestTeamName(),
						awayTeamRatings, details != null ? details.getGuestTacticType()
								: IMatchDetails.TAKTIK_NORMAL,
						details != null ? getRatingValue(details.getGuestTacticSkill() - 1) : 1);
			}

			String match = matchesModel.getMatch().getHomeTeamName() + " - "
					+ matchesModel.getMatch().getGuestTeamName();
			MatchEnginePanel matchPredictionPanel = new MatchEnginePanel(homeTeamValues,
					awayTeamValues);

			MatchPredictionDialog d = new MatchPredictionDialog(matchPredictionPanel, match);
		}
	}

	/**
	 * Helper to get at least the minium rating value.
	 */
	private int getRatingValue(int in) {
		if (in > 0) {
			return in;
		}
		return 1;
	}

	/**
	 * Check, if the ratings are ok/known or if all are at the default.
	 */
	private boolean ratingsAreKnown(TeamRatings ratings) {
		return (ratings != null && ratings.getMidfield() > 1d && ratings.getLeftDef() > 1d
				&& ratings.getMiddleDef() > 1d && ratings.getRightDef() > 1d
				&& ratings.getLeftAttack() > 1d && ratings.getMiddleAttack() > 1d && ratings
				.getRightAttack() > 1d);
	}

	/**
	 * Get the team data for the own team (current linep).
	 */
	private TeamData getOwnLineupRatings(MatchPredictionManager manager) {
		Lineup lineup = HOVerwaltung.instance().getModel().getCurrentLineupTeamRecalculated().getLineup();
		TeamRatings teamRatings = manager.generateTeamRatings(
				getRatingValue(RatingUtil.getIntValue4Rating(lineup.getRatings().getMidfield().get(-90d))),
				getRatingValue(RatingUtil.getIntValue4Rating(lineup.getRatings().getLeftDefense().get(-90d))),
				getRatingValue(RatingUtil.getIntValue4Rating(lineup.getRatings().getCentralDefense().get(-90d))),
				getRatingValue(RatingUtil.getIntValue4Rating(lineup.getRatings().getRightDefense().get(-90d))),
				getRatingValue(RatingUtil.getIntValue4Rating(lineup.getRatings().getLeftAttack().get(-90d))),
				getRatingValue(RatingUtil.getIntValue4Rating(lineup.getRatings().getCentralAttack().get(-90d))),
				getRatingValue(RatingUtil.getIntValue4Rating(lineup.getRatings().getRightAttack().get(-90d))));

		int tactic = lineup.getTacticType();
		return manager.generateTeamData(HOVerwaltung.instance().getModel().getBasics()
				.getTeamName(), teamRatings, tactic, getTacticStrength(lineup, tactic));
	}

	/**
	 * Get the tactic strength of the given lineup.
	 */
	private int getTacticStrength(Lineup lineup, int tacticType) {
		double tacticLevel = switch (tacticType) {
			case IMatchDetails.TAKTIK_KONTER -> lineup.getTacticLevelCounter();
			case IMatchDetails.TAKTIK_MIDDLE, IMatchDetails.TAKTIK_WINGS -> lineup.getTacticLevelAimAow();
			case IMatchDetails.TAKTIK_PRESSING -> lineup.getTacticLevelPressing();
			case IMatchDetails.TAKTIK_LONGSHOTS -> lineup.getTacticLevelLongShots();
			default -> 1d;
		};
		tacticLevel -= 1;
		return (int) Math.max(tacticLevel, 0);
	}

	private void doReInit() {
		if (m_jcbSpieleFilter.getSelectedIndex() > -1) {
			CursorToolkit.startWaitCursor(this);
			try {
				// Update tables
				int id = ((CBItem) m_jcbSpieleFilter.getSelectedItem()).getId();
				matchesTable.refresh(id, UserParameter.instance().matchLocation);
				matchesOverviewTable.refresh(id, UserParameter.instance().matchLocation);
				matchesHighlightsTable.refresh(id, UserParameter.instance().matchLocation);
				UserParameter.instance().spieleFilter = id;

				// then refresh all other panels
				newSelectionInform();
			} finally {
				CursorToolkit.stopWaitCursor(this);
			}
			this.needsRefresh = false;
		}
	}


	public void showMatch(int matchid) {
		matchesTable.markiereMatch(matchid);

		// If no match found in table
		if (matchesTable.getSelectedRow() < 0) {
			// Select all games for the marker to work
			m_jcbSpieleFilter.setSelectedIndex(0);
			UserParameter.instance().spieleFilter = 0;
			matchesTable.markiereMatch(matchid);
		}

		newSelectionInform();
	}

	private void initComponents() {
		this.matchesModel = new MatchesModel();
		setLayout(new BorderLayout());

		horizontalLeftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false,
				initMatchesTable(), initSpieldetails());

		linupPanel = new JPanel(new GridLayout(2, 1));
		aufstellungHeimPanel = new AufstellungsSternePanel(true);
		linupPanel.add(aufstellungHeimPanel);
		aufstellungGastPanel = new AufstellungsSternePanel(false);
		linupPanel.add(aufstellungGastPanel);
		verticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false,
				horizontalLeftSplitPane, new JScrollPane(linupPanel));

		horizontalLeftSplitPane
				.setDividerLocation(UserParameter.instance().spielePanel_horizontalLeftSplitPane);

		verticalSplitPane
				.setDividerLocation(UserParameter.instance().spielePanel_verticalSplitPane);

		add(verticalSplitPane, BorderLayout.CENTER);
	}

	/**
	 * Initialise player details GUI components.
	 */
	private Component initSpieldetails() {
		JPanel mainpanel = new ImagePanel(new BorderLayout());
		matchDetailsTabbedPane = new JTabbedPane();

		// Allgemein
		teamsComparePanel = new StaerkenvergleichPanel(this.matchesModel);
		matchDetailsTabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Allgemein"),
				new JScrollPane(teamsComparePanel));


		// Rating Panel
		m_jpTeamsRatingPanel = new TeamsRatingPanel(this.matchesModel);
		matchDetailsTabbedPane.addTab(HOVerwaltung.instance().getLanguageString("matches.tabtitle.ratings"),
				new JScrollPane(m_jpTeamsRatingPanel));

		// Highlights
		matchHighlightPanel = new SpielHighlightPanel(this.matchesModel);
		matchDetailsTabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Highlights"),
				new JScrollPane(matchHighlightPanel));

		// Match report
		matchReportPanel = new MatchReportPanel(this.matchesModel);
		matchDetailsTabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Matchbericht"),
				matchReportPanel);

		mainpanel.add(matchDetailsTabbedPane, BorderLayout.CENTER);

		final JPanel buttonPanel = new ImagePanel(new FlowLayout(FlowLayout.LEFT));

		// Reloadbutton
		reloadMatchButton = new JButton(ThemeManager.getIcon(HOIconName.RELOAD));
		reloadMatchButton.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"tt_Spiel_reload"));
		reloadMatchButton.setPreferredSize(new Dimension(24, 24));
		reloadMatchButton.setEnabled(false);
		buttonPanel.add(reloadMatchButton);

		deleteButton = new JButton(ThemeManager.getIcon(HOIconName.REMOVE));
		deleteButton.setBackground(ThemeManager.getColor(HOColorName.BUTTON_BG));
		deleteButton.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Spiel_loeschen"));
		deleteButton.setPreferredSize(new Dimension(24, 24));
		deleteButton.setEnabled(false);
		buttonPanel.add(deleteButton);

		adoptLineupButton = new JButton(ThemeManager.getIcon(HOIconName.GETLINEUP));
		adoptLineupButton.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"tt_Spiel_aufstellunguebernehmen"));
		adoptLineupButton.setPreferredSize(new Dimension(24, 24));
		adoptLineupButton.setEnabled(false);
		buttonPanel.add(adoptLineupButton);

		simulateMatchButton = new JButton(ThemeManager.getIcon(HOIconName.SIMULATEMATCH));
		simulateMatchButton.setToolTipText(HOVerwaltung.instance().getLanguageString("Simulate"));
		simulateMatchButton.setPreferredSize(new Dimension(24, 24));
		simulateMatchButton.setEnabled(false);
		buttonPanel.add(simulateMatchButton);

		mainpanel.add(buttonPanel, BorderLayout.SOUTH);
		return mainpanel;
	}

	/**
	 * Initialise matches panel.
	 */
	private Component initMatchesTable() {
		ImagePanel panel = new ImagePanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// Combobox matchesFilter =============================
		CBItem[] matchesFilter = {
				new CBItem(HOVerwaltung.instance().getLanguageString("AlleSpiele"),
						MatchesPanel.ALL_MATCHS),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurEigeneSpiele"),
						MatchesPanel.OWN_GAMES),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurEigenePflichtspiele"),
						MatchesPanel.OWN_OFFICIAL_GAMES),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurEigenePokalspiele"),
						MatchesPanel.ONLY_NATIONAL_CUP),
				new CBItem(HOVerwaltung.instance().getLanguageString("OnlySecondaryCup"),
						MatchesPanel.ONLY_SECONDARY_CUP),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurEigeneLigaspiele"),
						MatchesPanel.NUR_EIGENE_LIGASPIELE),
				new CBItem(HOVerwaltung.instance().getLanguageString("OnlyQualificationMatches"),
						MatchesPanel.ONLY_QUALIF_MATCHES),
				new CBItem(HOVerwaltung.instance()
						.getLanguageString("NurEigeneFreundschaftsspiele"),
						MatchesPanel.NUR_EIGENE_FREUNDSCHAFTSSPIELE),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurEigeneTournamentsspiele"),
						MatchesPanel.NUR_EIGENE_TOURNAMENTSPIELE),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurFremdeSpiele"),
						MatchesPanel.OTHER_TEAM_MATCHS) };

		m_jcbSpieleFilter = new JComboBox(matchesFilter);
		Helper.setComboBoxFromID(m_jcbSpieleFilter, UserParameter.instance().spieleFilter);
		m_jcbSpieleFilter.setFont(m_jcbSpieleFilter.getFont().deriveFont(Font.BOLD));
		panel.add(m_jcbSpieleFilter);


		// Add Match Location filtering =======
		var jpMatchLocation = new JPanel(new BorderLayout());
		jpMatchLocation.add(getMatchesLocationButtonsPanel());
		panel.add(jpMatchLocation);

		// Matches tab ===================================
		matchesTable = new MatchesTable(UserParameter.instance().spieleFilter);
		matchesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollpane = new JScrollPane(matchesTable);

		// Statistics tab ===================================
		matchesOverviewTable = new MatchesOverviewTable(UserParameter.instance().spieleFilter);
		JScrollPane scrollpane1 = new JScrollPane(matchesOverviewTable);
		matchesOverviewPanel = new JPanel(new BorderLayout());
		matchesOverviewPanel.add(scrollpane1, BorderLayout.SOUTH);

		// Statistics Goals tab ===================================
		matchesHighlightsTable = new MatchesHighlightsTable(UserParameter.instance().spieleFilter);
		JScrollPane scrollpane3 = new JScrollPane(matchesHighlightsTable);

		JTabbedPane pane = new JTabbedPane();
		HOVerwaltung hov = HOVerwaltung.instance();
		pane.addTab(hov.getLanguageString("Spiele"), scrollpane);
		pane.addTab(
				hov.getLanguageString("Statistik") + " ("
						+ hov.getLanguageString("SerieAuswaertsSieg") + "-"
						+ hov.getLanguageString("SerieAuswaertsUnendschieden") + "-"
						+ hov.getLanguageString("SerieAuswaertsNiederlage") + ")", matchesOverviewPanel);

		pane.addTab(hov.getLanguageString("Statistik") + " (" + hov.getLanguageString("Tore")
				+ ")", scrollpane3);
		panel.add(pane);

		return panel;
	}

	@NotNull
	private JPanel getMatchesLocationButtonsPanel() {

		JRadioButton all = new JRadioButton(MatchLocation.getText(MatchLocation.ALL) + "  ", MatchLocation.ALL == UserParameter.instance().matchLocation);
		all.addChangeListener(e -> {
			refreshOnButtonSelected(e, MatchLocation.ALL);
		});

		JRadioButton home = new JRadioButton(MatchLocation.getText(MatchLocation.HOME) + "  ", MatchLocation.HOME == UserParameter.instance().matchLocation);
		home.addChangeListener(e -> {
			refreshOnButtonSelected(e, MatchLocation.HOME);
		});

		JRadioButton away = new JRadioButton(MatchLocation.getText(MatchLocation.AWAY) + "  ", MatchLocation.AWAY == UserParameter.instance().matchLocation);
		away.addChangeListener(e -> {
			refreshOnButtonSelected(e, MatchLocation.AWAY);
		});

		JRadioButton neutral = new JRadioButton(MatchLocation.getText(MatchLocation.NEUTRAL) + "  ", MatchLocation.NEUTRAL == UserParameter.instance().matchLocation);
		neutral.addChangeListener(e -> {
			refreshOnButtonSelected(e, MatchLocation.NEUTRAL);
		});

		ButtonGroup matchesLocationButtons = new ButtonGroup();
		matchesLocationButtons.add(all);
		matchesLocationButtons.add(home);
		matchesLocationButtons.add(away);
		matchesLocationButtons.add(neutral);

		JPanel matchesLocationButtonsPanel = new JPanel();
		matchesLocationButtonsPanel.setLayout(new BoxLayout(matchesLocationButtonsPanel, BoxLayout.X_AXIS));
		matchesLocationButtonsPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		JLabel label = new JLabel(getTranslation("ls.module.lineup.matchlocation.label") + ": ");
		label.setFont(new Font(this.getFont().getFontName(), Font.BOLD, this.getFont().getSize()));
		matchesLocationButtonsPanel.add(label);

		matchesLocationButtonsPanel.add(all);
		matchesLocationButtonsPanel.add(home);
		matchesLocationButtonsPanel.add(away);
		matchesLocationButtonsPanel.add(neutral);
		return matchesLocationButtonsPanel;
	}

	private void refreshOnButtonSelected(ChangeEvent e, MatchLocation selectedLocation) {
		if (((JRadioButton) e.getSource()).isSelected()) {
			UserParameter.instance().matchLocation = selectedLocation;
			doReInit();
		}
	}

	private void newSelectionInform() {
		final int row = matchesTable.getSelectedRow();

		if (row > -1) {
			// Selektiertes Spiel des Models holen und alle 3 Panel informieren
			MatchKurzInfo info = ((MatchesColumnModel) matchesTable.getSorter().getModel())
					.getMatch((int) ((ColorLabelEntry) matchesTable.getSorter().getValueAt(row, 7))
							.getNumber());
			this.matchesModel.setMatch(info);

			updateButtons();
			Matchdetails details = this.matchesModel.getDetails();
			if (details == null || details.getMatchID() < 0
					|| info.getMatchStatus() != MatchKurzInfo.FINISHED) {
				aufstellungHeimPanel.clearAll();
				aufstellungGastPanel.clearAll();
			} else {
				aufstellungHeimPanel.refresh(info, info.getHomeTeamID());
				aufstellungGastPanel.refresh(info, info.getGuestTeamID());
			}
		} else {
			this.matchesModel.setMatch(null);
			// Alle Panels zurücksetzen
			reloadMatchButton.setEnabled(false);
			deleteButton.setEnabled(false);
			adoptLineupButton.setEnabled(false);
			simulateMatchButton.setEnabled(false);

			aufstellungHeimPanel.clearAll();
			aufstellungGastPanel.clearAll();
		}
	}

	/**
	 * Refresh button states.
	 */
	private void updateButtons() {
		deleteButton.setEnabled(true);
		simulateMatchButton.setEnabled(true);
		long gameFinishTime = matchesModel.getMatch().getMatchDateAsTimestamp().getTime() + 3 * 60 * 60 * 1000L; //assuming 3 hours to make sure the game is finished

		boolean gameFinished = matchesModel.getMatch().getMatchStatus() == MatchKurzInfo.FINISHED ||
				gameFinishTime < new Date().getTime();
		reloadMatchButton.setEnabled(gameFinished);

		if (matchesModel.getMatch().getMatchStatus() == MatchKurzInfo.FINISHED) {
			final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
			adoptLineupButton.setEnabled((matchesModel.getMatch().getHomeTeamID() == teamid)
					|| (matchesModel.getMatch().getGuestTeamID() == teamid));
		} else {
			adoptLineupButton.setEnabled(false);
		}
	}
}