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
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.enums.MatchType;
import core.model.match.MatchKurzInfo;
import core.model.player.Player;
import core.net.login.ProxyDialog;
import core.util.HODateTime;
import core.util.HOLogger;
import core.util.Helper;
import module.nthrf.NtTeamChooser;
import module.nthrf.NthrfUtil;
import module.series.MatchFixtures;
import tool.updater.UpdateController;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

/**
 * User dialog to download different hattrick data
 */
public class DownloadDialog extends JDialog implements ActionListener {

	// ~ Instance fields--/
	private static DownloadDialog m_clDownloadDialog;
	private static final HOVerwaltung hov = HOVerwaltung.instance();
	private final JButton m_jbAbort = new JButton(TranslationFacility.tr("ls.button.cancel"));
	final private JButton m_jbDownload = new JButton(TranslationFacility.tr("ls.button.download"));
	private final JButton m_jbProxy = new JButton(TranslationFacility.tr("ConfigureProxy"));
	private final DownloadFilter filterRoot = new DownloadFilter();
	private final CheckBoxTree downloadFilter = new CheckBoxTree();

	private final JCheckBox m_jchMatchArchive = new JCheckBox(TranslationFacility.tr("download.oldmatches"), false);
	private final SpinnerDateModel m_clSpinnerModel = new SpinnerDateModel();
	private final JSpinner m_jsSpinner = new JSpinner(m_clSpinnerModel);
	private final JCheckBox m_jchShowSaveDialog = new JCheckBox(TranslationFacility.tr("Show_SaveHRF_Dialog"), core.model.UserParameter.instance().showHRFSaveDialog);
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
		super(HOMainFrame.instance(), TranslationFacility.tr("ls.menu.file.download"), ModalityType.MODELESS);
		this.isNtTeam = UserManager.instance().getCurrentUser().isNtTeam();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
			normalDownloadPanel.setBorder(BorderFactory.createTitledBorder(TranslationFacility.tr("ls.button.download")));

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
			specialDownload.setBorder(BorderFactory.createTitledBorder(TranslationFacility.tr("Verschiedenes")));

			// Alte Spielpläne
			final JPanel oldFixturePanel = new ImagePanel(new BorderLayout());

			// MatchArchive
			final JPanel matchArchivePanel = new JPanel(new BorderLayout(1, 2));
			matchArchivePanel.setOpaque(false);

			m_jchMatchArchive.setToolTipText(TranslationFacility.tr("download.oldmatches.tt"));
			m_jchMatchArchive.addActionListener(this);
			m_jchMatchArchive.setOpaque(false);
			matchArchivePanel.add(m_jchMatchArchive, BorderLayout.WEST);

			m_clSpinnerModel.setCalendarField(Calendar.MONTH);
			((JSpinner.DateEditor) m_jsSpinner.getEditor()).getFormat().applyPattern("dd.MM.yyyy");
			matchArchivePanel.add(m_jsSpinner, BorderLayout.EAST);

			// Show HRF FileDialog
			m_jchShowSaveDialog.setToolTipText(TranslationFacility.tr("tt_Optionen_Show_SaveHRF_Dialog"));
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
			normalDownloadPanel.setBorder(BorderFactory.createTitledBorder(TranslationFacility.tr("ls.button.download")));
			JTextArea ta = new JTextArea();
			ta.append(TranslationFacility.tr("nthrf.hint1") + "\n");
			ta.append(TranslationFacility.tr("nthrf.hint2") + "\n");
			ta.append(TranslationFacility.tr("nthrf.hint3") + "\n");
			ta.append(TranslationFacility.tr("nthrf.hint4") + " '");
			ta.append(TranslationFacility.tr("Start") + "' ");
			ta.append(TranslationFacility.tr("nthrf.hint5"));
			ta.setEditable(false);

			normalDownloadPanel.setLayout(new BorderLayout());
			normalDownloadPanel.add(new JScrollPane(ta), BorderLayout.CENTER);
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			getContentPane().add(normalDownloadPanel, c);

		}
		var buttonRowPanel = new JPanel(new GridLayout(1, 3, 10, 10));
		m_jbDownload.setToolTipText(TranslationFacility.tr("tt_Download_Start"));
		m_jbDownload.addActionListener(this);
		m_jbDownload.setFont(m_jbDownload.getFont().deriveFont(Font.BOLD));
		InputMap buttonKeys = m_jbDownload.getInputMap(JButton.WHEN_FOCUSED);
		buttonKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
		buttonKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
		buttonRowPanel.add(m_jbDownload);

		m_jbProxy.setToolTipText(TranslationFacility.tr("tt_ConfigureProxy"));
		m_jbProxy.addActionListener(this);
		m_jbProxy.setFont(m_jbProxy.getFont().deriveFont(Font.BOLD));
		buttonRowPanel.add(m_jbProxy);

		m_jbAbort.setToolTipText(TranslationFacility.tr("tt_Download_Abbrechen"));
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
							var newFixtures = MatchFixtures.createFixtures(model.getXtraDaten().getSeriesMatchDate(),
									teamStats.values().stream().sorted(Comparator.comparing(TeamStats::getPosition)).toList());
							fixtures.addFixtures(newFixtures);
						}
						final MatchFixtures modelFixtures = hov.getModel().getFixtures();
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
					homodel.storeModel();
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
