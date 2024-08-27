package core.net;

import core.db.DBManager;
import core.db.user.UserManager;
import core.file.hrf.HRFStringParser;
import core.file.xml.TeamStats;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.CheckBoxTree.CheckBoxTree;
import core.gui.comp.panel.ImagePanel;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.enums.MatchType;
import core.model.match.MatchKurzInfo;
import core.model.player.Player;
import core.model.series.Paarung;
import core.net.login.ProxyDialog;
import core.util.HODateTime;
import core.util.HOLogger;
import core.util.Helper;
import module.nthrf.NtTeamChooser;
import module.nthrf.NthrfUtil;
import module.series.Spielplan;
import tool.updater.UpdateController;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * User dialog to download different hattrick data
 */
public class DownloadDialog extends JDialog implements ActionListener {

	// ~ Instance fields--/
	private static DownloadDialog m_clDownloadDialog;
	private static final HOVerwaltung hov = HOVerwaltung.instance();
	private final JButton m_jbAbort = new JButton(hov.getLanguageString("ls.button.cancel"));
	final private JButton m_jbDownload = new JButton(hov.getLanguageString("ls.button.download"));
	private final JButton m_jbProxy = new JButton(hov.getLanguageString("ConfigureProxy"));
	private final DownloadFilter filterRoot = new DownloadFilter();
	private final CheckBoxTree downloadFilter = new CheckBoxTree();

	private final JCheckBox m_jchMatchArchive = new JCheckBox(hov.getLanguageString("download.oldmatches"), false);
	private final SpinnerDateModel m_clSpinnerModel = new SpinnerDateModel();
	private final JSpinner m_jsSpinner = new JSpinner(m_clSpinnerModel);
	private final JCheckBox m_jchShowSaveDialog = new JCheckBox(hov.getLanguageString("Show_SaveHRF_Dialog"), core.model.UserParameter.instance().showHRFSaveDialog);
	private final boolean isNtTeam;

	/**
	 * Getter for the singleton HOMainFrame instance.
	 */
	public static DownloadDialog instance() {
		if (m_clDownloadDialog == null) {
			m_clDownloadDialog = new DownloadDialog();
		}

		return m_clDownloadDialog;
	}

	private static void clearInstance() {
		m_clDownloadDialog = null;
	}

	/**
	 * Singleton
	 */
	private DownloadDialog() {
		super(HOMainFrame.instance(), hov.getLanguageString("ls.menu.file.download"), ModalityType.MODELESS);
		this.isNtTeam = UserManager.instance().getCurrentUser().isNtTeam();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initComponents();
	}

	@Override
	public final void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(m_jbDownload)) {
			if ( isNtTeam){
				startNtDownload();
			}
			else {
				startDownload();
			}
			RefreshManager.instance().doReInit();
			close();
			if (UserParameter.instance().updateCheck) {
				UpdateController.check4update(false);
			}
		} else if (e.getSource().equals(m_jbAbort)) {
			close();
		} else if (e.getSource().equals(m_jbProxy)) {
			new ProxyDialog(HOMainFrame.instance());
		}
	}

	private void close(){
		setVisible(false);
		dispose();
	}

	@Override
	public void dispose() {
		super.dispose();
		clearInstance();
	}

	/**
	 * Initialize the GUI components.
	 */
	private void initComponents() {
		//setResizable(false);
		setContentPane(new ImagePanel(new GridBagLayout()));
		var c = new GridBagConstraints();

		if (!isNtTeam) {
			final JPanel normalDownloadPanel = new ImagePanel(new BorderLayout());
			normalDownloadPanel.setBorder(BorderFactory.createTitledBorder(hov.getLanguageString("ls.button.download")));

			// Download Filter
			final DefaultTreeModel newModel = new DefaultTreeModel(filterRoot);
			downloadFilter.setModel(newModel);
			newModel.reload();

			// Current Matches
			// - Official Matches
			//    currentMatchlist selects now the node OfficialMatches.
			//    It is the first subitem of current Matches in the Filter tree
			downloadFilter.checkNode(filterRoot.getOfficialMatches(), UserParameter.instance().downloadCurrentMatchlist);
			// - Integrated matches
			downloadFilter.checkNode(filterRoot.getSingleMatches(), UserParameter.instance().downloadSingleMatches);
			downloadFilter.checkNode(filterRoot.getLadderMatches(), UserParameter.instance().downloadLadderMatches);
			downloadFilter.checkNode(filterRoot.getTournamentGroupMatches(), UserParameter.instance().downloadTournamentGroupMatches);
			downloadFilter.checkNode(filterRoot.getTournamentPlayoffMatches(), UserParameter.instance().downloadTournamentPlayoffMatches);
			downloadFilter.checkNode(filterRoot.getDivisionBattleMatches(), UserParameter.instance().downloadDivisionBattleMatches);

			// Team Data
			downloadFilter.checkNode(filterRoot.getTeamData(), UserParameter.instance().xmlDownload);

			// Series Data (fixtures)
			downloadFilter.checkNode(filterRoot.getCurrentSeriesData(), UserParameter.instance().fixtures);

			var filterPanel = new JPanel(new BorderLayout());
			filterPanel.add(downloadFilter);
			var scrollPanel = new JScrollPane(filterPanel);
			scrollPanel.setPreferredSize(new Dimension(240,280));
			normalDownloadPanel.add(scrollPanel, BorderLayout.CENTER);
			c.gridx = 0;
			c.gridy = 0;
			c.weighty = 1;
			c.fill = GridBagConstraints.VERTICAL;
			c.anchor = GridBagConstraints.NORTHEAST;
			c.insets = new Insets(10, 10, 10, 10);
			getContentPane().add(normalDownloadPanel, c);

			final JPanel specialDownload = new ImagePanel(new BorderLayout());
			specialDownload.setBorder(BorderFactory.createTitledBorder(hov.getLanguageString("Verschiedenes")));

			// Alte Spielpläne
			final JPanel oldFixturePanel = new ImagePanel(new BorderLayout());

			// MatchArchive
			final JPanel matchArchivePanel = new JPanel(new BorderLayout(1, 2));
			matchArchivePanel.setOpaque(false);

			m_jchMatchArchive.setToolTipText(hov.getLanguageString("download.oldmatches.tt"));
			m_jchMatchArchive.addActionListener(this);
			m_jchMatchArchive.setOpaque(false);
			matchArchivePanel.add(m_jchMatchArchive, BorderLayout.WEST);

			m_clSpinnerModel.setCalendarField(Calendar.MONTH);
			((JSpinner.DateEditor) m_jsSpinner.getEditor()).getFormat().applyPattern("dd.MM.yyyy");
			matchArchivePanel.add(m_jsSpinner, BorderLayout.EAST);

			// Show HRF FileDialog
			m_jchShowSaveDialog.setToolTipText(hov.getLanguageString("tt_Optionen_Show_SaveHRF_Dialog"));
			m_jchShowSaveDialog.setOpaque(false);
			m_jchShowSaveDialog.addActionListener(this);

			final JPanel diverseOptionsPanel = new ImagePanel(new BorderLayout(1, 2));
			diverseOptionsPanel.add(matchArchivePanel, BorderLayout.NORTH);
			diverseOptionsPanel.add(m_jchShowSaveDialog, BorderLayout.SOUTH);

			oldFixturePanel.add(diverseOptionsPanel, BorderLayout.NORTH);

			specialDownload.add(oldFixturePanel);
			c.gridx = 1;
			getContentPane().add(specialDownload, c);
		} else {
			// isNtTeam
			final JPanel normalDownloadPanel = new ImagePanel(new GridLayout(3, 1, 4, 4));
			normalDownloadPanel.setBorder(BorderFactory.createTitledBorder(hov.getLanguageString("ls.button.download")));
			JTextArea ta = new JTextArea();
			ta.append(hov.getLanguageString("nthrf.hint1") + "\n");
			ta.append(hov.getLanguageString("nthrf.hint2") + "\n");
			ta.append(hov.getLanguageString("nthrf.hint3") + "\n");
			ta.append(hov.getLanguageString("nthrf.hint4") + " '");
			ta.append(hov.getLanguageString("Start") + "' ");
			ta.append(hov.getLanguageString("nthrf.hint5"));
			ta.setEditable(false);

			normalDownloadPanel.setLayout(new BorderLayout());
			normalDownloadPanel.add(new JScrollPane(ta), BorderLayout.CENTER);
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			getContentPane().add(normalDownloadPanel, c);

		}
		var buttonRowPanel = new JPanel(new GridLayout(1, 3, 10, 10));
		m_jbDownload.setToolTipText(hov.getLanguageString("tt_Download_Start"));
		m_jbDownload.addActionListener(this);
		m_jbDownload.setFont(m_jbDownload.getFont().deriveFont(Font.BOLD));
		InputMap buttonKeys = m_jbDownload.getInputMap(JButton.WHEN_FOCUSED);
		buttonKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
		buttonKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
		buttonRowPanel.add(m_jbDownload);

		m_jbProxy.setToolTipText(hov.getLanguageString("tt_ConfigureProxy"));
		m_jbProxy.addActionListener(this);
		m_jbProxy.setFont(m_jbProxy.getFont().deriveFont(Font.BOLD));
		buttonRowPanel.add(m_jbProxy);

		m_jbAbort.setToolTipText(hov.getLanguageString("tt_Download_Abbrechen"));
		m_jbAbort.addActionListener(this);
		buttonRowPanel.add(m_jbAbort);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		getContentPane().add(buttonRowPanel, c);
		pack();

		final Dimension size = getToolkit().getScreenSize();
		if (size.width > this.getSize().width) {
			// Center
			this.setLocation((size.width / 2) - (this.getSize().width / 2), (size.height / 2) - (this.getSize().height / 2));
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				m_jbDownload.requestFocusInWindow();
			}
		});

		setVisible(true);
	}

	/**
	 * The download action.
	 */
	private void startDownload() {

		boolean bOK = true;

		// Save Dialog's settings
		// the chpp-api only allows to download all current matches, the dialog settings only filters the match
		// types which of them should be stored in HO database. The settings are interpreted elsewhere.
		// Dialogs node "Current Matches" is checked, if any of the following is checked
		UserParameter.instance().downloadCurrentMatchlist = downloadFilter.isChecked(filterRoot.getOfficialMatches());
		UserParameter.instance().downloadDivisionBattleMatches = downloadFilter.isChecked(filterRoot.getDivisionBattleMatches());
		UserParameter.instance().downloadTournamentPlayoffMatches = downloadFilter.isChecked(filterRoot.getTournamentPlayoffMatches());
		UserParameter.instance().downloadTournamentGroupMatches = downloadFilter.isChecked(filterRoot.getTournamentGroupMatches());
		UserParameter.instance().downloadLadderMatches = downloadFilter.isChecked(filterRoot.getLadderMatches());
		UserParameter.instance().downloadSingleMatches = downloadFilter.isChecked(filterRoot.getSingleMatches());

		UserParameter.instance().showHRFSaveDialog = m_jchShowSaveDialog.isSelected();
		UserParameter.instance().xmlDownload = downloadFilter.isChecked(filterRoot.getTeamData());
		UserParameter.instance().fixtures = downloadFilter.isChecked(filterRoot.getCurrentSeriesData());

		if (UserParameter.instance().xmlDownload) { // download team data
			bOK = OnlineWorker.getHrf(this);
			List<Player> player = hov.getModel().getCurrentPlayers();
			for (Player p : player) {
				if (p.getNationalTeamId() != null && p.getNationalTeamId() != 0) {
					OnlineWorker.getMatches(p.getNationalTeamId(), false, true, true);
				}
			}
		}

		HOModel model = hov.getModel();
		int teamId = model.getBasics().getTeamId();

		int progressIncrement = 3;
		if (teamId > 0) {
			if (this.downloadFilter.isChecked(filterRoot.getCurrentMatches())) {
				// Only get lineups for own fixtures
				HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.match_info"), progressIncrement);
				bOK = (OnlineWorker.getMatches(teamId, false, true, true) != null);
				if (bOK) {
					OnlineWorker.getAllLineups(10);
				}

				if (model.getBasics().hasYouthTeam()) {
					var dateSinceTimestamp = DBManager.instance().getMinScoutingDate();
					var dateSince = HODateTime.fromDbTimestamp(dateSinceTimestamp);
					OnlineWorker.downloadMissingYouthMatchData(model, dateSince);
					// delete old youth match lineups, no longer needed (no current youth player has trained then)
					DBManager.instance().deleteYouthMatchDataBefore(dateSinceTimestamp);
				}
			}
			if (bOK && m_jchMatchArchive.isSelected()) {
				HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.match_info"), progressIncrement);
				var date = new HODateTime(m_clSpinnerModel.getDate().toInstant());
				List<MatchKurzInfo> allmatches = OnlineWorker.getMatchArchive(teamId, date, false);
				if (allmatches != null) {
					allmatches = OnlineWorker.FilterUserSelection(allmatches);
					for (MatchKurzInfo i : allmatches) {
						OnlineWorker.downloadMatchData(i, true);
					}
				}
			}

			if (bOK && UserParameter.instance().fixtures) {
				// in the last week of a season the LeagueLevelUnitID switches to the next season's value (no fixtures are available then)
				if (model.getBasics().getSpieltag() < 16) {
					HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.fixtures"), progressIncrement);
					var leagueId = model.getXtraDaten().getLeagueLevelUnitID();
					var fixtures = OnlineWorker.downloadLeagueFixtures(-1, leagueId);
					if (fixtures != null) {
						if ( fixtures.getMatches().isEmpty()){
							// Matches are not available from hattrick. Initialize them
							var teamStats = OnlineWorker.getSeriesDetails(leagueId);
							var teams = teamStats.values().stream().sorted(Comparator.comparing(TeamStats::getPosition)).toArray(TeamStats[]::new);
							var team1 = (TeamStats)teams[0];
							var team2 = (TeamStats)teams[1];
							var team3 = (TeamStats)teams[2];
							var team4 = (TeamStats)teams[3];
							var team5 = (TeamStats)teams[4];
							var team6 = (TeamStats)teams[5];
							var team7 = (TeamStats)teams[6];
							var team8 = (TeamStats)teams[7];

							var newFixtures = new ArrayList<Paarung>();
							// 1. round
							var date = model.getXtraDaten().getSeriesMatchDate();
							//1	-	2
							newFixtures.add(createFixture(date, 1, team1, team2));
							//3	-	4
							newFixtures.add(createFixture(date, 1, team3, team4));
							//5	-	6
							newFixtures.add(createFixture(date, 1, team5, team6));
							//7	-	8
							newFixtures.add(createFixture(date, 1, team7, team8));
							//2. round
							date = date.plusDaysAtSameLocalTime(7);
							//4	-	1
							newFixtures.add(createFixture(date, 2, team4, team1));
							//2	-	7
							newFixtures.add(createFixture(date, 2, team2, team7));
							//6	-	3
							newFixtures.add(createFixture(date, 2, team6, team3));
							//8	-	5
							newFixtures.add(createFixture(date, 2, team8, team5));
							//3. round
							date = date.plusDaysAtSameLocalTime(7);
							//1	-	8
							newFixtures.add(createFixture(date, 3, team1, team8));
							//3	-	5
							newFixtures.add(createFixture(date, 3, team3, team5));
							//4	-	2
							newFixtures.add(createFixture(date, 3, team4, team2));
							//7	-	6
							newFixtures.add(createFixture(date, 3, team7, team6));
							//4. round
							date = date.plusDaysAtSameLocalTime(7);
							//6	-	1
							newFixtures.add(createFixture(date, 4, team6, team1));
							//2	-	3
							newFixtures.add(createFixture(date, 4, team2, team3));
							//5	-	7
							newFixtures.add(createFixture(date, 4, team5, team7));
							//8	-	4
							newFixtures.add(createFixture(date, 4, team8, team4));
							//5. round
							date = date.plusDaysAtSameLocalTime(7);
							//1	-	7
							newFixtures.add(createFixture(date, 5, team1, team7));
							//4	-	5
							newFixtures.add(createFixture(date, 5, team4, team5));
							//3	-	8
							newFixtures.add(createFixture(date, 5, team3, team8));
							//2	-	6
							newFixtures.add(createFixture(date, 5, team2, team6));
							//6. round
							date = date.plusDaysAtSameLocalTime(7);
							//5	-	1
							newFixtures.add(createFixture(date, 6, team5, team1));
							//7	-	3
							newFixtures.add(createFixture(date, 6, team7, team3));
							//6	-	4
							newFixtures.add(createFixture(date, 6, team6, team4));
							//8	-	2
							newFixtures.add(createFixture(date, 6, team8, team2));
							//7. round
							date = date.plusDaysAtSameLocalTime(7);
							//1	-	3
							newFixtures.add(createFixture(date, 7, team1, team3));
							//2	-	5
							newFixtures.add(createFixture(date, 7, team2, team5));
							//4	-	7
							newFixtures.add(createFixture(date, 7, team4, team7));
							//6	-	8
							newFixtures.add(createFixture(date, 7, team6, team8));
							//8. round
							date = date.plusDaysAtSameLocalTime(7);
							//8	-	6
							newFixtures.add(createFixture(date, 8, team8, team6));
							//7	-	4
							newFixtures.add(createFixture(date, 8, team7, team4));
							//5	-	2
							newFixtures.add(createFixture(date, 8, team5, team2));
							//3	-	1
							newFixtures.add(createFixture(date, 8, team3, team1));
							//9. round
							date = date.plusDaysAtSameLocalTime(7);
							//2	-	8
							newFixtures.add(createFixture(date, 9, team2, team8));
							//4	-	6
							newFixtures.add(createFixture(date, 9, team4, team6));
							//3	-	7
							newFixtures.add(createFixture(date, 9, team3, team7));
							//1	-	5
							newFixtures.add(createFixture(date, 9, team1, team5));
							//10. round
							date = date.plusDaysAtSameLocalTime(7);
							//6	-	2
							newFixtures.add(createFixture(date, 10, team6, team2));
							//8	-	3
							newFixtures.add(createFixture(date, 10, team8, team3));
							//5	-	4
							newFixtures.add(createFixture(date, 10, team5, team4));
							//7	-	1
							newFixtures.add(createFixture(date, 10, team7, team1));
							//11. round
							date = date.plusDaysAtSameLocalTime(7);
							//4	-	8
							newFixtures.add(createFixture(date, 11, team4, team8));
							//7	-	5
							newFixtures.add(createFixture(date, 11, team7, team5));
							//3	-	2
							newFixtures.add(createFixture(date, 11, team3, team2));
							//1	-	6
							newFixtures.add(createFixture(date, 11, team1, team6));
							//12. round
							date = date.plusDaysAtSameLocalTime(7);
							//6	-	7
							newFixtures.add(createFixture(date, 12, team6, team7));
							//2	-	4
							newFixtures.add(createFixture(date, 12, team2, team4));
							//5	-	3
							newFixtures.add(createFixture(date, 12, team5, team3));
							//8	-	1
							newFixtures.add(createFixture(date, 12, team8, team1));
							//13. round
							date = date.plusDaysAtSameLocalTime(7);
							//5	-	8
							newFixtures.add(createFixture(date, 13, team5, team8));
							//3	-	6
							newFixtures.add(createFixture(date, 13, team3, team6));
							//7	-	2
							newFixtures.add(createFixture(date, 13, team7, team2));
							//1	-	4
							newFixtures.add(createFixture(date, 13, team1, team4));
							//14. round
							date = date.plusDaysAtSameLocalTime(7);
							//8	-	7
							newFixtures.add(createFixture(date, 14, team8, team7));
							//6	-	5
							newFixtures.add(createFixture(date, 14, team6, team5));
							//4	-	3
							newFixtures.add(createFixture(date, 14, team4, team3));
							//2	-	1
							newFixtures.add(createFixture(date, 14, team2, team1));
							fixtures.addFixtures(newFixtures);
						}
						final Spielplan modelFixtures = hov.getModel().getFixtures();
						if (modelFixtures != null) {
							// state of previous download
							var oldDownloadedFixtures = modelFixtures.getMatches();
							// extract played matches of foreign teams
							var newPlayedMatchesOfOtherTeams = fixtures.getMatches().stream()
									.filter(i -> i.getToreHeim() >= 0 && i.getHeimId() != teamId && i.getGastId() != teamId)
									.toList();
							if (oldDownloadedFixtures != null) {
								// matches that were not played on previous download
								var notPlayedYet = oldDownloadedFixtures.stream()
										.filter(i -> i.getToreHeim() < 0)
										.toList();
								// intersection of both lists gives the list of played matches since previous download
								var latestPlayedMatches = newPlayedMatchesOfOtherTeams.stream()
										.filter(i -> notPlayedYet.stream().anyMatch(j -> j.getMatchId() == i.getMatchId()))
										.toList();
								for (var m : latestPlayedMatches) {
									if (!OnlineWorker.downloadMatchData(m.getMatchId(), MatchType.LEAGUE, true)) {
										HOLogger.instance().error(OnlineWorker.class, "Error fetching Match: " + m.getMatchId());
										break;
									}
								}
							}
						}
						hov.getModel().saveFixtures(fixtures);
					} else {
						bOK = false;
					}
				}
			}

			if (bOK) {
				// Download previous series data
				var selection = new ArrayList<>();
				var e = filterRoot.getPreviousSeriesData().children();
				while (e.hasMoreElements()) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) e.nextElement();
					if (downloadFilter.isChecked(child)) {
						selection.add(child.getUserObject());
					}
				}
				HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.fixtures"), progressIncrement);
				downloadOldFixtures(teamId, selection);
			}
		}

		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.calc_subskills"), progressIncrement);
		model.calcSubskills();

		HOMainFrame.instance().setInformationCompleted();

	}

	/**
	 * Create new fixture
	 * @param date Match date
	 * @param round Match round
	 * @param team1 Home team
	 * @param team2 Guest team
	 * @return Fixture
	 */
	private Paarung createFixture(HODateTime date, int round,  TeamStats team1, TeamStats team2) {
		var ret = new Paarung();
		ret.setDatum(date);
		ret.setHeimId(team1.getTeamId());
		ret.setGastId(team2.getTeamId());
		ret.setHeimName(team1.getTeamName());
		ret.setGastName(team2.getTeamName());
		ret.setSpieltag(round);
		return ret;
	}

	private void downloadOldFixtures(int teamId, List<Object> selection) {
		LigaAuswahlDialog leagueSelectionDialog = null;
		boolean useOwnLeague = true;
		var leagueId = HOVerwaltung.instance().getModel().getXtraDaten().getLeagueLevelUnitID();
		for (Object s : selection) {
			if (s instanceof Map.Entry) {
				var e = (Map.Entry<String, Integer>)s;
				final int seasonId = e.getValue();

				if (useOwnLeague || !leagueSelectionDialog.getReuseSelection()) {
					// download matches of the season to get the league id of own team
					var matches = OnlineWorker.downloadMatchesOfSeason(teamId, seasonId);
					if (matches != null) {
						DBManager.instance().storeMatchKurzInfos(matches);
						var leagueMatch = matches.stream()
								.filter(i -> i.getMatchType() == MatchType.LEAGUE && i.getMatchStatus() == MatchKurzInfo.FINISHED).findFirst();
						if (leagueMatch.isPresent()) {
							leagueId = leagueMatch.get().getMatchContextId();
						}
					}
				}

				// confirm selection
				if (leagueSelectionDialog == null || !leagueSelectionDialog.getReuseSelection()) {
					leagueSelectionDialog = new LigaAuswahlDialog(this, seasonId, leagueId, selection.size() > 1);
					if ( leagueSelectionDialog.isAborted()) break;
					leagueId = leagueSelectionDialog.getLigaID();
					useOwnLeague = leagueSelectionDialog.isOwnLeagueSelected();
				}
				if (leagueId > -2) {
					var fixtures = OnlineWorker.downloadLeagueFixtures(seasonId, leagueId);
					if (fixtures != null) {
						hov.getModel().saveFixtures(fixtures);
					} else {
						break;
					}
				}
			}
		}
	}

	private void startNtDownload() {
		try {
			var teams = NthrfUtil.getNtTeams();
			if (teams == null || teams.isEmpty() || teams.get(0)[0] == null || teams.get(0)[0].isEmpty()) {
				return;
			}
			final long teamId;
			if (teams.size() > 1) {
				NtTeamChooser chooser = new NtTeamChooser(teams);
				chooser.setModal(true);
				chooser.setVisible(true);
				teamId = chooser.getSelectedTeamId();
				chooser.dispose();
			} else {
				teamId = Long.parseLong(teams.get(0)[0]);
			}
			var hrf = NthrfUtil.createNthrf(teamId);
			if ( !hrf.isEmpty()) {
				HOModel homodel = HRFStringParser.parse(hrf);
				if (homodel != null) {
					var ntTeams = DBManager.instance().loadAllNtTeamDetails();
					// save the model in the database
					homodel.saveHRF();
					// Only update when the model is newer than existing
					if (HOVerwaltung.isNewModel(homodel)) {
						hov.setModel(homodel);
					}

					var matches = OnlineWorker.getMatches((int)teamId, false, true, true);
					if (matches!= null) {
						OnlineWorker.getAllLineups(null);
						OnlineWorker.downloadNtTeams(ntTeams, matches);
					}
				}
				else {
					HOLogger.instance().error(getClass(), "Download error: " + hrf);
				}
			}
			else {
				// No players in team or training area closed
				// TODO message box
			}
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "Cannot download NT data from hattrick: " + e);
		}
	}
}
