// %393632151:de.hattrickorganizer.gui.matches%
package module.matches;

import core.datatype.CBItem;
import core.db.DBManager;
import core.gui.ApplicationClosingListener;
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
import core.model.match.MatchLineupPlayer;
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class SpielePanel extends LazyImagePanel {

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
	/** Only cup Matches of suplied team */
	public static final int NUR_EIGENE_POKALSPIELE = 3;
	/** Only cup +league + quali Matches of suplied team */
	public static final int NUR_EIGENE_PFLICHTSPIELE = 2;
	/** Only Matches of suplied team */
	public static final int NUR_EIGENE_SPIELE = 1;
	/** Only Secondary cup matchs */
	public static final int ONLY_SECONDARY_CUP = 9;
	/** Only Qualifification matchs */
	public static final int ONLY_QUALIF_MATCHES = 8;
	public static final int ALL_MATCHS = 0;
	private static final long serialVersionUID = -6337569355347545083L;
	private AufstellungsSternePanel aufstellungGastPanel;
	private AufstellungsSternePanel aufstellungHeimPanel;
	private JButton adoptLineupButton;
	private JButton printButton;
	private JButton deleteButton;
	private JButton reloadMatchButton;
	private JButton simulateMatchButton;
	private JComboBox m_jcbSpieleFilter;
	private JPanel linupPanel;
	private JSplitPane horizontalLeftSplitPane;
	private JSplitPane verticalSplitPane;
	private JTabbedPane matchDetailsTabbedPane;
	private ManschaftsBewertungsPanel m_jpManschaftsBewertungsPanel;
	private ManschaftsBewertungs2Panel m_jpManschaftsBewertungs2Panel;
	private MatchReportPanel matchReportPanel;
	private SpielHighlightPanel matchHighlightPanel;
	private MatchesTable matchesTable;
	private MatchesOverviewTable matchesOverviewTable;
	//	private MatchesOverviewCommonPanel matchesOverviewCommonPanel;
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

	private void saveColumnOrder() {
		matchesTable.saveColumnOrder();
		matchesOverviewTable.saveColumnOrder();
	}

	private void addListeners() {
		this.reloadMatchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int matchid = matchesModel.getMatch().getMatchID();
				OnlineWorker.downloadMatchData(matchesModel.getMatch().getMatchID(), matchesModel
						.getMatch().getMatchTyp(), true);
				RefreshManager.instance().doReInit();
				showMatch(matchid);
			}
		});

		this.deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSelectedMatches();
			}
		});

		this.printButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (matchesModel.getMatch() != null) {
					final SpielePrintDialog printDialog = new SpielePrintDialog(matchesModel);
					printDialog.doPrint(matchesModel.getMatch().getHeimName() + " : "
							+ matchesModel.getMatch().getGastName() + " - "
							+ matchesModel.getMatch().getMatchDate());
					printDialog.setVisible(false);
					printDialog.dispose();
				}
			}
		});

		this.adoptLineupButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				adoptLineup();
			}
		});

		this.simulateMatchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				simulateMatch();
			}
		});

		this.matchesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					newSelectionInform();
				}
			}
		});

		HOMainFrame.instance().addApplicationClosingListener(new ApplicationClosingListener() {

			@Override
			public void applicationClosing() {
				saveSettings();
			}
		});

		m_jcbSpieleFilter.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
					doReInit();
				}
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
			List<MatchLineupPlayer> teamspieler = DBManager.instance().getMatchLineupPlayers(
					matchesModel.getMatch().getMatchID(), teamid);
			Lineup aufstellung = HOVerwaltung.instance().getModel().getLineup();

			aufstellung.clearLineup(); // To make sure the old one is
			// gone.

			if (teamspieler != null) {
				for (MatchLineupPlayer player : teamspieler) {
					if (player.getId() == IMatchRoleID.setPieces) {
						aufstellung.setKicker(player.getSpielerId());
					} else if (player.getId() == IMatchRoleID.captain) {
						aufstellung.setKapitaen(player.getSpielerId());
					} else {
						aufstellung.setSpielerAtPosition(player.getId(), player.getSpielerId(),
								player.getTaktik());
					}
				}
			}
			// Alles Updaten
			HOMainFrame.instance().getAufstellungsPanel().update();
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
			text.append(" (" + infos.length + " ");
			text.append(HOVerwaltung.instance().getLanguageString("Spiele"));
			text.append(")");
		}
		text.append(":");

		for (int i = 0; (i < infos.length) && (i < 11); i++) {
			text.append("\n" + infos[i].getHeimName() + " - " + infos[i].getGastName());
			if (i == 10) {
				text.append("\n ... ");
			}
		}

		int value = JOptionPane.showConfirmDialog(SpielePanel.this, text,
				HOVerwaltung.instance().getLanguageString("confirmation.title"), JOptionPane.YES_NO_OPTION);

		if (value == JOptionPane.YES_OPTION) {
			for (int i = 0; i < infos.length; i++) {
				DBManager.instance().deleteMatch(infos[i].getMatchID());
			}
			RefreshManager.instance().doReInit();
		}
	}

	private void simulateMatch() {
		if (matchesModel.getMatch() != null) {
			Matchdetails details = DBManager.instance().getMatchDetails(
					matchesModel.getMatch().getMatchID());
			MatchPredictionManager manager = MatchPredictionManager.instance();
			int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
			boolean homeMatch = false;
			if (teamId == matchesModel.getMatch().getHeimID()) {
				homeMatch = true;
			}

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
				homeTeamValues = manager.generateTeamData(matchesModel.getMatch().getHeimName(),
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
				awayTeamValues = manager.generateTeamData(matchesModel.getMatch().getGastName(),
						awayTeamRatings, details != null ? details.getGuestTacticType()
								: IMatchDetails.TAKTIK_NORMAL,
						details != null ? getRatingValue(details.getGuestTacticSkill() - 1) : 1);
			}

			String match = matchesModel.getMatch().getHeimName() + " - "
					+ matchesModel.getMatch().getGastName();
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
		Lineup lineup = HOVerwaltung.instance().getModel().getLineup();
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
		double tacticLevel = 1d;
		switch (tacticType) {
			case IMatchDetails.TAKTIK_KONTER:
				tacticLevel = lineup.getTacticLevelCounter();
				break;
			case IMatchDetails.TAKTIK_MIDDLE:
				tacticLevel = lineup.getTacticLevelAimAow();
				break;
			case IMatchDetails.TAKTIK_PRESSING:
				tacticLevel = lineup.getTacticLevelPressing();
				break;
			case IMatchDetails.TAKTIK_WINGS:
				tacticLevel = lineup.getTacticLevelAimAow();
				break;
			case IMatchDetails.TAKTIK_LONGSHOTS:
				tacticLevel = lineup.getTacticLevelLongShots();
				break;
		}
		tacticLevel -= 1;
		return (int) Math.max(tacticLevel, 0);
	}

	private void doReInit() {
		if (m_jcbSpieleFilter.getSelectedIndex() > -1) {
			CursorToolkit.startWaitCursor(this);
			try {
				// Tabelle updaten
				int id = ((CBItem) m_jcbSpieleFilter.getSelectedItem()).getId();
				matchesTable.refresh(id);
				matchesOverviewTable.refresh(id);
//				matchesOverviewCommonPanel.refresh(id);
				matchesHighlightsTable.refresh(id);
				UserParameter.instance().spieleFilter = id;

				// Dann alle anderen Panels
				newSelectionInform();
			} finally {
				CursorToolkit.stopWaitCursor(this);
			}
			this.needsRefresh = false;
		}
	}

	/**
	 * Zeigt das Match mit der ID an.
	 */
	public void showMatch(int matchid) {
		matchesTable.markiereMatch(matchid);

		// Wenn kein Match in Tabelle gefunden
		if (matchesTable.getSelectedRow() < 0) {
			// Alle Spiele auswählen, damit die Markierung funktioniert
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
				initSpieleTabelle(), initSpieldetails());

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

		// Bewertung
		m_jpManschaftsBewertungsPanel = new ManschaftsBewertungsPanel(this.matchesModel);
		matchDetailsTabbedPane.addTab(HOVerwaltung.instance().getLanguageString("matches.tabtitle.ratings"),
				new JScrollPane(m_jpManschaftsBewertungsPanel));

		// //Bewertung2
		m_jpManschaftsBewertungs2Panel = new ManschaftsBewertungs2Panel(this.matchesModel);
		matchDetailsTabbedPane.addTab(HOVerwaltung.instance().getLanguageString("matches.tabtitle.percentageratings"),
				new JScrollPane(m_jpManschaftsBewertungs2Panel));

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

		printButton = new JButton(ThemeManager.getIcon(HOIconName.PRINTER));
		printButton.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Spiel_drucken"));
		printButton.setPreferredSize(new Dimension(24, 24));
		printButton.setEnabled(false);
		buttonPanel.add(printButton);

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
	private Component initSpieleTabelle() {
		ImagePanel panel = new ImagePanel(new BorderLayout());

		CBItem[] matchesFilter = {
				new CBItem(HOVerwaltung.instance().getLanguageString("AlleSpiele"),
						SpielePanel.ALL_MATCHS),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurEigeneSpiele"),
						SpielePanel.NUR_EIGENE_SPIELE),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurEigenePflichtspiele"),
						SpielePanel.NUR_EIGENE_PFLICHTSPIELE),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurEigenePokalspiele"),
						SpielePanel.NUR_EIGENE_POKALSPIELE),
				new CBItem(HOVerwaltung.instance().getLanguageString("OnlySecondaryCup"),
						SpielePanel.ONLY_SECONDARY_CUP),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurEigeneLigaspiele"),
						SpielePanel.NUR_EIGENE_LIGASPIELE),
				new CBItem(HOVerwaltung.instance().getLanguageString("OnlyQualificationMatches"),
						SpielePanel.ONLY_QUALIF_MATCHES),
				new CBItem(HOVerwaltung.instance()
						.getLanguageString("NurEigeneFreundschaftsspiele"),
						SpielePanel.NUR_EIGENE_FREUNDSCHAFTSSPIELE),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurEigeneTournamentsspiele"),
						SpielePanel.NUR_EIGENE_TOURNAMENTSPIELE),
				new CBItem(HOVerwaltung.instance().getLanguageString("NurFremdeSpiele"),
						SpielePanel.OTHER_TEAM_MATCHS) };

		m_jcbSpieleFilter = new JComboBox(matchesFilter);
		Helper.markierenComboBox(m_jcbSpieleFilter, UserParameter.instance().spieleFilter);
		m_jcbSpieleFilter.setFont(m_jcbSpieleFilter.getFont().deriveFont(Font.BOLD));
		panel.add(m_jcbSpieleFilter, BorderLayout.NORTH);

		matchesTable = new MatchesTable(UserParameter.instance().spieleFilter);
		matchesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollpane = new JScrollPane(matchesTable);

		matchesOverviewTable = new MatchesOverviewTable(UserParameter.instance().spieleFilter);
		JScrollPane scrollpane1 = new JScrollPane(matchesOverviewTable);

//		matchesOverviewCommonPanel = new MatchesOverviewCommonPanel(
//				UserParameter.instance().spieleFilter);
//		JScrollPane scrollpane2 = new JScrollPane(matchesOverviewCommonPanel);

		matchesHighlightsTable = new MatchesHighlightsTable(UserParameter.instance().spieleFilter);
		JScrollPane scrollpane3 = new JScrollPane(matchesHighlightsTable);

		JTabbedPane pane = new JTabbedPane();
		HOVerwaltung hov = HOVerwaltung.instance();
		pane.addTab(hov.getLanguageString("Spiele"), scrollpane);
		pane.addTab(
				hov.getLanguageString("Statistik") + " ("
						+ hov.getLanguageString("SerieAuswaertsSieg") + "-"
						+ hov.getLanguageString("SerieAuswaertsUnendschieden") + "-"
						+ hov.getLanguageString("SerieAuswaertsNiederlage") + ")", scrollpane1);
//		pane.addTab(hov.getLanguageString("Statistik") + " (" + hov.getLanguageString("Allgemein")
//				+ ")", scrollpane2);
		pane.addTab(hov.getLanguageString("Statistik") + " (" + hov.getLanguageString("Tore")
				+ ")", scrollpane3);
		panel.add(pane, BorderLayout.CENTER);

		return panel;
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
				aufstellungHeimPanel.refresh(info.getMatchID(), info.getHeimID());
				aufstellungGastPanel.refresh(info.getMatchID(), info.getGastID());
			}
		} else {
			this.matchesModel.setMatch(null);
			// Alle Panels zurücksetzen
			reloadMatchButton.setEnabled(false);
			deleteButton.setEnabled(false);
			printButton.setEnabled(false);
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
		if(gameFinished)
		{
			reloadMatchButton.setEnabled(true);
		}
		else
		{
			reloadMatchButton.setEnabled(false);
		}

		if (matchesModel.getMatch().getMatchStatus() == MatchKurzInfo.FINISHED) {
			final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
			if ((matchesModel.getMatch().getHeimID() == teamid)
					|| (matchesModel.getMatch().getGastID() == teamid)) {
				adoptLineupButton.setEnabled(true);
			} else {
				adoptLineupButton.setEnabled(false);
			}
			printButton.setEnabled(true);
		} else {
			adoptLineupButton.setEnabled(false);
			printButton.setEnabled(false);
		}
	}
}