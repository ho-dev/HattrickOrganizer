package core.net;

import core.db.DBManager;
import core.db.user.UserManager;
import core.file.hrf.HRFStringParser;
import core.file.xml.TeamStats;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.CheckBoxTree.CheckBoxTree;
import core.gui.comp.panel.ImagePanel;
import core.model.*;
import core.model.enums.MatchType;
import core.model.match.MatchKurzInfo;
import core.model.player.Player;
import core.module.ModuleManager;
import core.net.login.ProxyDialog;
import core.util.HODateTime;
import core.util.HOLogger;
import core.util.Helper;
import module.nthrf.MainPanel;
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

import static core.module.IModule.HALL_OF_FAME;
import static core.module.IModule.STATUS_DEACTIVATED;

/**
 * User dialog to download different hattrick data
 */
public class DownloadDialog extends JDialog implements ActionListener {

	// ~ Instance fields--/
	private static final String DOWNLOAD_KEY = "ls.button.download";
	private static DownloadDialog m_clDownloadDialog;
	private static final HOVerwaltung hov = HOVerwaltung.instance();
	private final JButton abortButton = new JButton(TranslationFacility.tr("ls.button.cancel"));
	private final JButton downloadButton = new JButton(TranslationFacility.tr(DOWNLOAD_KEY));
	private final JButton proxyButton = new JButton(TranslationFacility.tr("ConfigureProxy"));
	private final DownloadFilter filterRoot = new DownloadFilter();
	private final CheckBoxTree downloadFilter = new CheckBoxTree();

	private final JCheckBox matchArchiveCheckBox = new JCheckBox(TranslationFacility.tr("download.oldmatches"), false);
	private final SpinnerDateModel archiveDateModel = new SpinnerDateModel();
	private final JSpinner archiveDateSpinner = new JSpinner(archiveDateModel);
	private final JCheckBox showSaveDialogCheckBox = new JCheckBox(TranslationFacility.tr("Show_SaveHRF_Dialog"), core.model.UserParameter.instance().showHRFSaveDialog);
	private final boolean isNtTeam;
	/** Progress bar step of each download stage. */
	private static final int PROGRESS_INCREMENT = 3;

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
		if (e.getSource().equals(downloadButton)) {
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
		} else if (e.getSource().equals(abortButton)) {
			close();
		} else if (e.getSource().equals(proxyButton)) {
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
		setContentPane(new ImagePanel(new GridBagLayout()));
		var c = new GridBagConstraints();

		if (!isNtTeam) {
			final JPanel normalDownloadPanel = new ImagePanel(new BorderLayout());
			normalDownloadPanel.setBorder(BorderFactory.createTitledBorder(TranslationFacility.tr(DOWNLOAD_KEY)));

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

			matchArchiveCheckBox.setToolTipText(TranslationFacility.tr("download.oldmatches.tt"));
			matchArchiveCheckBox.addActionListener(this);
			matchArchiveCheckBox.setOpaque(false);
			matchArchivePanel.add(matchArchiveCheckBox, BorderLayout.WEST);

			archiveDateModel.setCalendarField(Calendar.MONTH);
			((JSpinner.DateEditor) archiveDateSpinner.getEditor()).getFormat().applyPattern("dd.MM.yyyy");
			matchArchivePanel.add(archiveDateSpinner, BorderLayout.EAST);

			// Show HRF FileDialog
			showSaveDialogCheckBox.setToolTipText(TranslationFacility.tr("tt_Optionen_Show_SaveHRF_Dialog"));
			showSaveDialogCheckBox.setOpaque(false);
			showSaveDialogCheckBox.addActionListener(this);

			final JPanel diverseOptionsPanel = new ImagePanel(new BorderLayout(1, 2));
			diverseOptionsPanel.add(matchArchivePanel, BorderLayout.NORTH);
			diverseOptionsPanel.add(showSaveDialogCheckBox, BorderLayout.SOUTH);

			oldFixturePanel.add(diverseOptionsPanel, BorderLayout.NORTH);

			specialDownload.add(oldFixturePanel);
			c.gridx = 1;
			getContentPane().add(specialDownload, c);
		} else {
			// isNtTeam
			final JPanel normalDownloadPanel = new ImagePanel(new GridLayout(3, 1, 4, 4));
			normalDownloadPanel.setBorder(BorderFactory.createTitledBorder(TranslationFacility.tr(DOWNLOAD_KEY)));
			normalDownloadPanel.setLayout(new BorderLayout());
			normalDownloadPanel.add(new JScrollPane(MainPanel.createHintArea()), BorderLayout.CENTER);
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			getContentPane().add(normalDownloadPanel, c);

		}
		var buttonRowPanel = new JPanel(new GridLayout(1, 3, 10, 10));
		downloadButton.setToolTipText(TranslationFacility.tr("tt_Download_Start"));
		downloadButton.addActionListener(this);
		downloadButton.setFont(downloadButton.getFont().deriveFont(Font.BOLD));
		InputMap buttonKeys = downloadButton.getInputMap(JComponent.WHEN_FOCUSED);
		buttonKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
		buttonKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
		buttonRowPanel.add(downloadButton);

		proxyButton.setToolTipText(TranslationFacility.tr("tt_ConfigureProxy"));
		proxyButton.addActionListener(this);
		proxyButton.setFont(proxyButton.getFont().deriveFont(Font.BOLD));
		buttonRowPanel.add(proxyButton);

		abortButton.setToolTipText(TranslationFacility.tr("tt_Download_Abbrechen"));
		abortButton.addActionListener(this);
		buttonRowPanel.add(abortButton);

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
				downloadButton.requestFocusInWindow();
			}
		});

		setVisible(true);
	}

    /**
     * The download action.
     */
    private void startDownload() {
        saveDialogSettings();

        boolean bOK = true;
        if (UserParameter.instance().xmlDownload) {
            bOK = downloadTeamData();
        }

        HOModel model = hov.getModel();
        int teamId = model.getBasics().getTeamId();
        if (teamId > 0) {
            downloadTeamRelatedData(model, teamId, bOK);
        }

        HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.calc_subskills"), PROGRESS_INCREMENT);
        model.calcSubskills();

        HOMainFrame.instance().setInformationCompleted();
    }

    /**
     * Saves the dialog's settings.
     * The chpp-api only allows to download all current matches, the dialog settings only filter the match
     * types which of them should be stored in HO database. The settings are interpreted elsewhere.
     * Dialog's node "Current Matches" is checked, if any of the match types is checked.
     */
    private void saveDialogSettings() {
        var parameter = UserParameter.instance();
        parameter.downloadCurrentMatchlist = downloadFilter.isChecked(filterRoot.getOfficialMatches());
        parameter.downloadDivisionBattleMatches = downloadFilter.isChecked(filterRoot.getDivisionBattleMatches());
        parameter.downloadTournamentPlayoffMatches = downloadFilter.isChecked(filterRoot.getTournamentPlayoffMatches());
        parameter.downloadTournamentGroupMatches = downloadFilter.isChecked(filterRoot.getTournamentGroupMatches());
        parameter.downloadLadderMatches = downloadFilter.isChecked(filterRoot.getLadderMatches());
        parameter.downloadSingleMatches = downloadFilter.isChecked(filterRoot.getSingleMatches());

        parameter.showHRFSaveDialog = showSaveDialogCheckBox.isSelected();
        parameter.xmlDownload = downloadFilter.isChecked(filterRoot.getTeamData());
        parameter.fixtures = downloadFilter.isChecked(filterRoot.getCurrentSeriesData());
    }

    /**
     * Downloads the team data (hrf) and the matches of the national teams of the current players.
     *
     * @return false, if the team data download failed
     */
    private boolean downloadTeamData() {
        boolean ok = OnlineWorker.getHrf(this);
        for (Player p : hov.getModel().getCurrentPlayers()) {
            var nationalTeamId = p.getNationalTeamId();
            if (nationalTeamId != null && nationalTeamId != 0) {
                OnlineWorker.getMatches(nationalTeamId, false, true, true);
            }
        }
        return ok;
    }

    /**
     * Downloads everything that needs the own team id, in the order of the dialog.
     *
     * @param ok false, if a previous download failed
     */
    private void downloadTeamRelatedData(HOModel model, int teamId, boolean ok) {
        if (ModuleManager.instance().getModule(HALL_OF_FAME).getStatus() != STATUS_DEACTIVATED) {
            downloadHallOfFame(model, teamId);
        }

        boolean bOK = ok;
        if (this.downloadFilter.isChecked(filterRoot.getCurrentMatches())) {
            bOK = downloadCurrentMatches(model, teamId);
        }
        if (bOK && matchArchiveCheckBox.isSelected()) {
            downloadMatchArchive(teamId);
        }
        // in the last week of a season the LeagueLevelUnitID switches to the next season's value (no fixtures are available then)
        if (bOK && UserParameter.instance().fixtures && model.getBasics().getSpieltag() < 16) {
            bOK = downloadCurrentFixtures(model, teamId);
        }
        if (bOK) {
            HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.fixtures"), PROGRESS_INCREMENT);
            downloadOldFixtures(teamId, selectedPreviousSeries());
        }

        if (WorldDetailLeague.checkWorldDetailsDownload()) {
            downloadWorldDetails();
        }
    }

    private static void downloadHallOfFame(HOModel model, int teamId) {
        HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.hall_of_fame"), PROGRESS_INCREMENT);
        var hallOfFamesPlayer = OnlineWorker.downloadHallOfFame(teamId);
        model.setHallOfFamesPlayers(hallOfFamesPlayer);
        DBManager.instance().storeHallOfFame(model.getHrfId(), hallOfFamesPlayer);
    }

    /**
     * Downloads the current matches with the lineups of the own fixtures, and the youth team's matches and data.
     *
     * @return false, if the match list download failed
     */
    private static boolean downloadCurrentMatches(HOModel model, int teamId) {
        HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.match_info"), PROGRESS_INCREMENT);
        boolean ok = OnlineWorker.getMatches(teamId, false, true, true) != null;
        if (ok) {
            OnlineWorker.getAllLineups(10);
        }
        if (model.getBasics().hasYouthTeam()) {
            downloadYouthData(model);
        }
        return ok;
    }

    private static void downloadYouthData(HOModel model) {
        var dateSinceTimestamp = DBManager.instance().getMinScoutingDate();
        var dateSince = HODateTime.fromDbTimestamp(dateSinceTimestamp);
        OnlineWorker.downloadMissingYouthMatchData(model, dateSince);
        // delete old youth match lineups, no longer needed (no current youth player has trained then)
        DBManager.instance().deleteYouthMatchDataBefore(dateSinceTimestamp);
        OnlineWorker.downloadYouthTeamData(model);
    }

    private void downloadMatchArchive(int teamId) {
        HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.match_info"), PROGRESS_INCREMENT);
        var date = new HODateTime(archiveDateModel.getDate().toInstant());
        List<MatchKurzInfo> allMatches = OnlineWorker.getMatchArchive(teamId, date, false);
        if (allMatches != null) {
            for (MatchKurzInfo matchKurzInfo : OnlineWorker.FilterUserSelection(allMatches)) {
                OnlineWorker.downloadMatchData(matchKurzInfo, true);
            }
        }
    }

    /**
     * Downloads the fixtures of the current series and the matches of other teams played since the previous download.
     *
     * @return false, if the fixtures download failed
     */
    private static boolean downloadCurrentFixtures(HOModel model, int teamId) {
        HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.fixtures"), PROGRESS_INCREMENT);
        var leagueId = model.getXtraDaten().getLeagueLevelUnitID();
        var fixtures = OnlineWorker.downloadLeagueFixtures(-1, leagueId);
        if (fixtures == null) {
            return false;
        }
        if (fixtures.getMatches().isEmpty()) {
            // Matches are not available from hattrick. Initialize them
            var teamStats = OnlineWorker.getSeriesDetails(leagueId);
            var newFixtures = MatchFixtures.createFixtures(model.getXtraDaten().getSeriesMatchDate(),
                teamStats.values().stream().sorted(Comparator.comparing(TeamStats::getPosition)).toList());
            fixtures.addFixtures(newFixtures);
        }
        var modelFixtures = model.getFixtures();
        if (modelFixtures != null && modelFixtures.getMatches() != null) {
            downloadLatestPlayedMatches(modelFixtures, fixtures, teamId);
        }
        model.saveFixtures(fixtures);
        return true;
    }

    /**
     * Downloads the league matches of other teams that were not played on the previous download and are played now.
     *
     * @param previous fixtures of the previous download
     * @param current  fixtures just downloaded
     */
    private static void downloadLatestPlayedMatches(MatchFixtures previous, MatchFixtures current, int teamId) {
        var notPlayedYet = previous.getMatches().stream()
            .filter(i -> i.getToreHeim() < 0)
            .toList();
        // played matches of foreign teams, that were not played on previous download
        var latestPlayedMatches = current.getMatches().stream()
            .filter(i -> i.getToreHeim() >= 0 && i.getHeimId() != teamId && i.getGastId() != teamId)
            .filter(i -> notPlayedYet.stream().anyMatch(j -> j.getMatchId() == i.getMatchId()))
            .toList();
        for (var m : latestPlayedMatches) {
            if (!OnlineWorker.downloadMatchData(m.getMatchId(), MatchType.LEAGUE, true)) {
                HOLogger.instance().error(OnlineWorker.class, "Error fetching Match: " + m.getMatchId());
                break;
            }
        }
    }

    /**
     * @return user objects of the checked nodes below "Previous Series Data"
     */
    private List<Object> selectedPreviousSeries() {
        var selection = new ArrayList<>();
        var e = filterRoot.getPreviousSeriesData().children();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) e.nextElement();
            if (downloadFilter.isChecked(child)) {
                selection.add(child.getUserObject());
            }
        }
        return selection;
    }

    private static void downloadWorldDetails() {
        HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.world_details"), PROGRESS_INCREMENT);
        var allLeagues = OnlineWorker.downloadWorldDetails();
        DBManager.instance().storeWorldDetailLeagues(allLeagues);
        WorldDetailsManager.instance().refresh();
        WorldDetailLeague.setLatestDownloadNow();
    }

	private void downloadOldFixtures(int teamId, List<Object> selection) {
		var download = new OldFixturesDownload(teamId, selection.size() > 1);
		for (Object s : selection) {
			if (s instanceof Map.Entry<?, ?> entry && entry.getValue() instanceof Integer seasonId
					&& !download.downloadSeason(seasonId)) {
				break;
			}
		}
	}

	/**
	 * Downloads the fixtures of previous seasons, asking the user which league to take.
	 * The user may reuse the choice for the following seasons.
	 */
	private final class OldFixturesDownload {
		private final int teamId;
		private final boolean multipleSeasons;
		private LigaAuswahlDialog leagueSelectionDialog;
		private boolean useOwnLeague = true;
		private int leagueId = hov.getModel().getXtraDaten().getLeagueLevelUnitID();

		OldFixturesDownload(int teamId, boolean multipleSeasons) {
			this.teamId = teamId;
			this.multipleSeasons = multipleSeasons;
		}

		/**
		 * @return false, if the user aborted or the download failed: no further season is downloaded
		 */
		boolean downloadSeason(int seasonId) {
			if (useOwnLeague || !leagueSelectionDialog.getReuseSelection()) {
				leagueId = ownLeagueOfSeason(seasonId);
			}
			if (leagueSelectionDialog == null || !leagueSelectionDialog.getReuseSelection()) {
				leagueSelectionDialog = new LigaAuswahlDialog(DownloadDialog.this, seasonId, leagueId, multipleSeasons);
				if (leagueSelectionDialog.isAborted()) {
					return false;
				}
				leagueId = leagueSelectionDialog.getLigaID();
				useOwnLeague = leagueSelectionDialog.isOwnLeagueSelected();
			}
			if (leagueId <= -2) {
				return true;
			}
			var fixtures = OnlineWorker.downloadLeagueFixtures(seasonId, leagueId);
			if (fixtures == null) {
				return false;
			}
			hov.getModel().saveFixtures(fixtures);
			return true;
		}

		/**
		 * Downloads the own team's matches of the season to get its league id.
		 *
		 * @return league id of the own team in that season, the current one if no finished league match is found
		 */
		private int ownLeagueOfSeason(int seasonId) {
			var matches = OnlineWorker.downloadMatchesOfSeason(teamId, seasonId);
			if (matches == null) {
				return leagueId;
			}
			matches.stream().filter(i -> i.getMatchType().isTournament()).forEach(MatchKurzInfo::loadTournamentDetails);
			DBManager.instance().storeMatchKurzInfos(matches);
			return matches.stream()
					.filter(i -> i.getMatchType() == MatchType.LEAGUE && i.getMatchStatus() == MatchKurzInfo.FINISHED)
					.findFirst()
					.map(MatchKurzInfo::getMatchContextId)
					.orElse(leagueId);
		}
	}

	private void startNtDownload() {
		try {
			var teams = NthrfUtil.getNtTeams();
			if (teams == null || teams.isEmpty() || teams.get(0)[0] == null || teams.get(0)[0].isEmpty()) {
				return;
			}
			final long teamId = chooseNtTeam(teams);
			var hrf = NthrfUtil.createNthrf(teamId);
			if (hrf.isEmpty()) {
				HOLogger.instance().warning(getClass(), "No NT data: no players in team or training area closed");
				return;
			}
			HOModel homodel = HRFStringParser.parse(hrf);
			if (homodel == null) {
				HOLogger.instance().error(getClass(), "Download error: " + hrf);
				return;
			}
			storeNtModel(homodel, teamId);
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "Cannot download NT data from hattrick: " + e);
		}
	}

	/**
	 * @return the id of the only national team, or of the one the user picks
	 */
	private static long chooseNtTeam(List<String[]> teams) {
		if (teams.size() <= 1) {
			return Long.parseLong(teams.get(0)[0]);
		}
		NtTeamChooser chooser = new NtTeamChooser(teams);
		chooser.setModal(true);
		chooser.setVisible(true);
		long teamId = chooser.getSelectedTeamId();
		chooser.dispose();
		return teamId;
	}

	/**
	 * Stores the national team's model and downloads its matches and the opponent teams.
	 */
	private static void storeNtModel(HOModel homodel, long teamId) {
		var ntTeams = DBManager.instance().loadAllNtTeamDetails();
		// save the model in the database
		homodel.storeModel();
		// Only update when the model is newer than existing
		if (HOVerwaltung.isNewModel(homodel)) {
			hov.setModel(homodel);
		}
		var matches = OnlineWorker.getMatches((int) teamId, false, true, true);
		if (matches != null) {
			OnlineWorker.getAllLineups(null);
			OnlineWorker.downloadNtTeams(ntTeams, matches);
		}
	}
}
