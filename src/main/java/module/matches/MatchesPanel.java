package module.matches;

import core.datatype.CBItem;
import core.db.DBManager;
import core.gui.CursorToolkit;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
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
import core.rating.RatingPredictionModel;
import core.util.HODateTime;
import core.util.Helper;
import module.lineup.Lineup;
import module.matches.statistics.MatchesHighlightsTable;
import module.matches.statistics.MatchesOverviewTable;
import module.teamAnalyzer.ui.RatingUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static core.util.Helper.getTranslation;

public final class MatchesPanel extends LazyImagePanel {

	public static final int ALL_GAMES = 0;
	public static final int OWN_GAMES = 1;
	public static final int OWN_OFFICIAL_GAMES = 2; //own cup + league + qualif
	public static final int OWN_CUP_GAMES = 3;
	public static final int OWN_NATIONAL_CUP_GAMES = 4;
	public static final int OWN_SECONDARY_CUP_GAMES = 5;
	public static final int OWN_LEAGUE_GAMES = 6;
	public static final int OWN_QUALIF_GAMES = 7;
	public static final int OWN_FRIENDLY_GAMES = 8;
	public static final int OWN_TOURNAMENT_GAMES = 9;
	public static final int OTHER_TEAM_GAMES = 10;


	// Combobox matchesFilter =============================
	public static final CBItem[] matchesTypeCBItems = {
			new CBItem(Helper.getTranslation("AlleSpiele"), MatchesPanel.ALL_GAMES),
			new CBItem(Helper.getTranslation("NurEigeneSpiele"), MatchesPanel.OWN_GAMES),
			new CBItem(Helper.getTranslation("NurEigenePflichtspiele"), MatchesPanel.OWN_OFFICIAL_GAMES),
			new CBItem(Helper.getTranslation("AllCupMatches"), MatchesPanel.OWN_CUP_GAMES),
			new CBItem(Helper.getTranslation("NurEigenePokalspiele"), MatchesPanel.OWN_NATIONAL_CUP_GAMES),
			new CBItem(Helper.getTranslation("OnlySecondaryCup"), MatchesPanel.OWN_SECONDARY_CUP_GAMES),
			new CBItem(Helper.getTranslation("NurEigeneLigaspiele"), MatchesPanel.OWN_LEAGUE_GAMES),
			new CBItem(Helper.getTranslation("OnlyQualificationMatches"), MatchesPanel.OWN_QUALIF_GAMES),
			new CBItem(Helper.getTranslation("NurEigeneFreundschaftsspiele"), MatchesPanel.OWN_FRIENDLY_GAMES),
			new CBItem(Helper.getTranslation("NurEigeneTournamentsspiele"), MatchesPanel.OWN_TOURNAMENT_GAMES),
			new CBItem(Helper.getTranslation("NurFremdeSpiele"), MatchesPanel.OTHER_TEAM_GAMES)
	};

	private AufstellungsSternePanel aufstellungGastPanel;
	private AufstellungsSternePanel aufstellungHeimPanel;
	private JButton adoptLineupButton;
	private JButton deleteButton;
	private JButton reloadMatchButton;
	private JButton simulateMatchButton;
	private JComboBox<CBItem> gameSelectionFilter;
	private JSplitPane horizontalLeftSplitPane;
	private JSplitPane verticalSplitPane;
	private MatchesTable matchesTable;
	private MatchesOverviewTable matchesOverviewTable;
	private MatchesHighlightsTable matchesHighlightsTable;
	private MatchesModel matchesModel;


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
		this.reloadMatchButton.addActionListener(e -> reloadSelectedMatches());

		this.deleteButton.addActionListener(e -> deleteSelectedMatches());

		this.adoptLineupButton.addActionListener(e -> adoptLineup());

		this.simulateMatchButton.addActionListener(e -> simulateMatch());

		this.matchesTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				newSelectionInform();
			}
		});

		HOMainFrame.instance().addApplicationClosingListener(this::saveSettings);

		gameSelectionFilter.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				doReInit();
			}
		});
	}

	private void saveSettings() {
		matchesTable.storeUserSettings();
		matchesOverviewTable.storeUserSettings();
		UserParameter parameter = UserParameter.instance();
		parameter.spielePanel_horizontalLeftSplitPane = horizontalLeftSplitPane.getDividerLocation();
		parameter.spielePanel_verticalSplitPane = verticalSplitPane.getDividerLocation();
	}

	private void adoptLineup() {
		if (matchesModel.getMatch() != null && (matchesModel.getMatch().getMatchStatus() == MatchKurzInfo.FINISHED)) {
			int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
			List<MatchLineupPosition> positions = DBManager.instance().getMatchLineupPlayers(
					matchesModel.getMatch().getMatchID(), matchesModel.getMatch().getMatchType(), teamid);
			Lineup lineup = HOVerwaltung.instance().getModel().getCurrentLineup();
			lineup.clearLineup(); // To make sure the old one is gone.
			if (positions != null) {
				for (MatchLineupPosition player : positions) {
					if (player.getRoleId() == IMatchRoleID.setPieces) {
						lineup.setKicker(player.getPlayerId());
					} else if (player.getRoleId() == IMatchRoleID.captain) {
						lineup.setCaptain(player.getPlayerId());
					} else {
						lineup.setSpielerAtPosition(player.getRoleId(), player.getPlayerId(),
								player.getBehaviour());
					}
				}
			}

			HOMainFrame.instance().getLineupPanel().update();
			HOMainFrame.instance().showTab(IModule.LINEUP);
		}
	}

	private void reloadSelectedMatches() {
		HOMainFrame.instance().resetInformation();
		int matchid = matchesModel.getMatch().getMatchID();
		int i=0;
		int n = matchesTable.getSelectedRows().length;
		for ( var selectedRowNumber : matchesTable.getSelectedRows()){
			var matchKurzInfo = matchesTable.getMatchAtRow(selectedRowNumber);
			OnlineWorker.downloadMatchData( matchKurzInfo.getMatchID(), matchKurzInfo.getMatchType(), true);
			HOMainFrame.instance().updateProgress((int)(++i * 100.0 / n));
		}
		RefreshManager.instance().doReInit();
		showMatch(matchid);
		HOMainFrame.instance().setInformationCompleted();
	}

	private void deleteSelectedMatches() {
		int[] rows = matchesTable.getSelectedRows();
		MatchKurzInfo[] infos = new MatchKurzInfo[rows.length];

		for (int i = 0; i < rows.length; i++) {
			infos[i] = matchesTable.getMatchAtRow(rows[i]);
		}

		StringBuilder text = new StringBuilder(100);
		text.append(TranslationFacility.tr("ls.button.delete"));
		if (infos.length > 1) {
			text.append(" (").append(infos.length).append(" ");
			text.append(TranslationFacility.tr("Spiele"));
			text.append(")");
		}
		text.append(":");

		for (int i = 0; (i < infos.length) && (i < 11); i++) {
			text.append("\n").append(infos[i].getHomeTeamName()).append(" – ").append(infos[i].getGuestTeamName());
			if (i == 10) {
				text.append("\n ... ");
			}
		}

		int value = JOptionPane.showConfirmDialog(MatchesPanel.this, text,
				TranslationFacility.tr("confirmation.title"), JOptionPane.YES_NO_OPTION);

		if (value == JOptionPane.YES_OPTION) {
			for (MatchKurzInfo info : infos) {
				DBManager.instance().deleteMatch(info);
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

			String matchName = matchesModel.getMatch().getHomeTeamName() + " – " + matchesModel.getMatch().getGuestTeamName();
			MatchEnginePanel matchPredictionPanel = new MatchEnginePanel(homeTeamValues, awayTeamValues);

			MatchPredictionDialog d = new MatchPredictionDialog(matchPredictionPanel, matchName);
		}
	}

	/**
	 * Helper to get at least the minimum rating value.
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
	 * Get the team data for the own team (current lineup).
	 */
	private TeamData getOwnLineupRatings(MatchPredictionManager manager) {
		var hoModel = HOVerwaltung.instance().getModel();
		var lineup = hoModel.getCurrentLineup();
		var ratingPredictionModel = hoModel.getRatingPredictionModel();

		TeamRatings teamRatings = manager.generateTeamRatings(
				getRatingValue(RatingUtil.getIntValue4Rating(ratingPredictionModel.getAverageRating(lineup, RatingPredictionModel.RatingSector.MIDFIELD, 90))),
				getRatingValue(RatingUtil.getIntValue4Rating(ratingPredictionModel.getAverageRating(lineup, RatingPredictionModel.RatingSector.DEFENCE_LEFT, 90))),
				getRatingValue(RatingUtil.getIntValue4Rating(ratingPredictionModel.getAverageRating(lineup, RatingPredictionModel.RatingSector.DEFENCE_CENTRAL, 90))),
				getRatingValue(RatingUtil.getIntValue4Rating(ratingPredictionModel.getAverageRating(lineup, RatingPredictionModel.RatingSector.DEFENCE_RIGHT, 90))),
				getRatingValue(RatingUtil.getIntValue4Rating(ratingPredictionModel.getAverageRating(lineup, RatingPredictionModel.RatingSector.ATTACK_LEFT, 90))),
				getRatingValue(RatingUtil.getIntValue4Rating(ratingPredictionModel.getAverageRating(lineup, RatingPredictionModel.RatingSector.ATTACK_CENTRAL, 90))),
				getRatingValue(RatingUtil.getIntValue4Rating(ratingPredictionModel.getAverageRating(lineup, RatingPredictionModel.RatingSector.ATTACK_RIGHT, 90))));

		int tactic = lineup.getTacticType();
		return manager.generateTeamData(HOVerwaltung.instance().getModel().getBasics().getTeamName(), teamRatings, tactic, getTacticStrength(lineup));
	}

	/**
	 * Get the tactic strength of the given lineup.
	 */
	private int getTacticStrength(Lineup lineup) {
		var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
		return (int)ratingPredictionModel.getTacticRating(lineup, 0);
	}

	private void doReInit() {
		if (gameSelectionFilter.getSelectedIndex() > -1) {
			CursorToolkit.startWaitCursor(this);
			try {
				// Update tables
				int id = ((CBItem) gameSelectionFilter.getSelectedItem()).getId();
				matchesTable.refresh(id, UserParameter.instance().matchLocation);
				matchesOverviewTable.refresh(id, UserParameter.instance().matchLocation);
				matchesHighlightsTable.refresh(id, UserParameter.instance().matchLocation);
				UserParameter.instance().spieleFilter = id;

				// then refresh all other panels
				newSelectionInform();
			} finally {
				CursorToolkit.stopWaitCursor(this);
			}
		}
	}


	public void showMatch(int matchid) {
		matchesTable.markiereMatch(matchid);

		// If no match found in table
		if (matchesTable.getSelectedRow() < 0) {
			// Select all games for the marker to work
			gameSelectionFilter.setSelectedIndex(0);
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

		JPanel linupPanel = new JPanel(new GridLayout(2, 1));
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
		JTabbedPane matchDetailsTabbedPane = new JTabbedPane();

		// Allgemein
		StaerkenvergleichPanel teamsComparePanel = new StaerkenvergleichPanel(this.matchesModel);
		matchDetailsTabbedPane.addTab(TranslationFacility.tr("Allgemein"), new JScrollPane(teamsComparePanel));


		// Rating Panel
		TeamsRatingPanel m_jpTeamsRatingPanel = new TeamsRatingPanel(this.matchesModel);
		matchDetailsTabbedPane.addTab(TranslationFacility.tr("matches.tabtitle.ratings"),
				new JScrollPane(m_jpTeamsRatingPanel));

		// Highlights
		SpielHighlightPanel matchHighlightPanel = new SpielHighlightPanel(this.matchesModel);
		matchDetailsTabbedPane.addTab(TranslationFacility.tr("Highlights"),
				new JScrollPane(matchHighlightPanel));

		// Match report
		MatchReportPanel matchReportPanel = new MatchReportPanel(this.matchesModel);
		matchDetailsTabbedPane.addTab(TranslationFacility.tr("Matchbericht"),
				matchReportPanel);

		mainpanel.add(matchDetailsTabbedPane, BorderLayout.CENTER);

		final JPanel buttonPanel = new ImagePanel(new FlowLayout(FlowLayout.LEFT));

		// Reloadbutton
		reloadMatchButton = new JButton(ThemeManager.getIcon(HOIconName.RELOAD));
		reloadMatchButton.setToolTipText(TranslationFacility.tr(
				"tt_Spiel_reload"));
		reloadMatchButton.setPreferredSize(new Dimension(24, 24));
		reloadMatchButton.setEnabled(false);
		buttonPanel.add(reloadMatchButton);

		deleteButton = new JButton(ThemeManager.getIcon(HOIconName.REMOVE));
		deleteButton.setBackground(ThemeManager.getColor(HOColorName.BUTTON_BG));
		deleteButton.setToolTipText(TranslationFacility.tr("tt_Spiel_loeschen"));
		deleteButton.setPreferredSize(new Dimension(24, 24));
		deleteButton.setEnabled(false);
		buttonPanel.add(deleteButton);

		adoptLineupButton = new JButton(ThemeManager.getIcon(HOIconName.GETLINEUP));
		adoptLineupButton.setToolTipText(TranslationFacility.tr(
				"tt_Spiel_aufstellunguebernehmen"));
		adoptLineupButton.setPreferredSize(new Dimension(24, 24));
		adoptLineupButton.setEnabled(false);
		buttonPanel.add(adoptLineupButton);

		simulateMatchButton = new JButton(ThemeManager.getIcon(HOIconName.SIMULATEMATCH));
		simulateMatchButton.setToolTipText(TranslationFacility.tr("Simulate"));
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

		gameSelectionFilter = new JComboBox<>(matchesTypeCBItems);
		Helper.setComboBoxFromID(gameSelectionFilter, UserParameter.instance().spieleFilter);
		gameSelectionFilter.setFont(gameSelectionFilter.getFont().deriveFont(Font.BOLD));
		panel.add(gameSelectionFilter);


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
		JPanel matchesOverviewPanel = new JPanel(new BorderLayout());
		matchesOverviewPanel.add(scrollpane1, BorderLayout.SOUTH);

		// Statistics Goals tab ===================================
		matchesHighlightsTable = new MatchesHighlightsTable(UserParameter.instance().spieleFilter);
		JScrollPane scrollpane3 = new JScrollPane(matchesHighlightsTable);

		JTabbedPane pane = new JTabbedPane();
		pane.addTab(TranslationFacility.tr("Spiele"), scrollpane);
		pane.addTab(
				TranslationFacility.tr("Statistik") + " ("
						+ TranslationFacility.tr("SerieAuswaertsSieg") + "-"
						+ TranslationFacility.tr("SerieAuswaertsUnendschieden") + "-"
						+ TranslationFacility.tr("SerieAuswaertsNiederlage") + ")", matchesOverviewPanel);

		pane.addTab(TranslationFacility.tr("Statistik") + " (" + TranslationFacility.tr("Tore")
				+ ")", scrollpane3);
		panel.add(pane);

		return panel;
	}

	@NotNull
	private JPanel getMatchesLocationButtonsPanel() {

		JRadioButton all = new JRadioButton(MatchLocation.getText(MatchLocation.ALL) + "  ", MatchLocation.ALL == UserParameter.instance().matchLocation);
		all.addChangeListener(e -> refreshOnButtonSelected(e, MatchLocation.ALL));

		JRadioButton home = new JRadioButton(MatchLocation.getText(MatchLocation.HOME) + "  ", MatchLocation.HOME == UserParameter.instance().matchLocation);
		home.addChangeListener(e -> refreshOnButtonSelected(e, MatchLocation.HOME));

		JRadioButton away = new JRadioButton(MatchLocation.getText(MatchLocation.AWAY) + "  ", MatchLocation.AWAY == UserParameter.instance().matchLocation);
		away.addChangeListener(e -> refreshOnButtonSelected(e, MatchLocation.AWAY));

		JRadioButton neutral = new JRadioButton(MatchLocation.getText(MatchLocation.NEUTRAL) + "  ", MatchLocation.NEUTRAL == UserParameter.instance().matchLocation);
		neutral.addChangeListener(e -> refreshOnButtonSelected(e, MatchLocation.NEUTRAL));

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
			MatchKurzInfo info = matchesTable.getMatchAtRow (row);
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
		var gameFinishTime = matchesModel.getMatch().getMatchSchedule().plus(3, ChronoUnit.HOURS); //assuming 3 hours to make sure the game is finished
		boolean gameFinished = matchesModel.getMatch().getMatchStatus() == MatchKurzInfo.FINISHED ||
				gameFinishTime.isBefore(HODateTime.now()) ;
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