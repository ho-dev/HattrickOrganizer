package core.net;

import core.datatype.CBItem;
import core.db.DBManager;
import core.db.user.UserManager;
import core.file.hrf.HRFStringParser;
import core.gui.HOMainFrame;
import core.gui.InfoPanel;
import core.gui.RefreshManager;
import core.gui.comp.CheckBoxTree.CheckBoxTree;
import core.gui.comp.panel.ImagePanel;
import core.model.HOModel;
import core.model.HOVerwaltung;
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
import module.series.Spielplan;
import tool.updater.UpdateController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;


/**
 * Dialog, der den User den Download von verschiedenen Daten aus Hattrick heraus
 * ermöglicht
 */
public class DownloadDialog extends JDialog implements ActionListener {

	// ~ Instance fields--/
	private static DownloadDialog m_clDownloadDialog;
	private static HOVerwaltung hov = HOVerwaltung.instance();
	private JButton m_jbAbort = new JButton(hov.getLanguageString("ls.button.cancel"));
	final private JButton m_jbDownload = new JButton(hov.getLanguageString("ls.button.download"));
	private JButton m_jbProxy = new JButton(hov.getLanguageString("ConfigureProxy"));
	private JCheckBox m_jchOldFixtures = new JCheckBox(hov.getLanguageString("download.oldseriesdata"), false);
	private DownloadFilter filterRoot = new DownloadFilter();
	private CheckBoxTree downloadFilter = new CheckBoxTree();

	private JCheckBox m_jchHRF = new JCheckBox(hov.getLanguageString("download.teamdata"),	core.model.UserParameter.instance().xmlDownload);
	private JCheckBox m_jchMatchArchive = new JCheckBox(hov.getLanguageString("download.oldmatches"), false);
	private JCheckBox m_jchFixtures = new JCheckBox(hov.getLanguageString("download.seriesdata"), core.model.UserParameter.instance().fixtures);
	private JList m_jlOldSeasons = new JList();
	private SpinnerDateModel m_clSpinnerModel = new SpinnerDateModel();
	private JSpinner m_jsSpinner = new JSpinner(m_clSpinnerModel);
	private JCheckBox m_jchShowSaveDialog = new JCheckBox(hov.getLanguageString("Show_SaveHRF_Dialog"), core.model.UserParameter.instance().showHRFSaveDialog);
	private boolean isNtTeam;


	/**
	 * Getter for the singleton HOMainFrame instance.
	 */
	public static DownloadDialog instance() {
		if (m_clDownloadDialog == null) {
			m_clDownloadDialog = new DownloadDialog();
		}

		return m_clDownloadDialog;
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
		if (e.getSource().equals(m_jchOldFixtures)) {
			m_jlOldSeasons.setEnabled(m_jchOldFixtures.isSelected());
		} else if (e.getSource().equals(m_jbDownload)) {
			if ( isNtTeam){
				startNtDownload();
			}
			else {
				startDownload();
			}
			RefreshManager.instance().doReInit();
			setVisible(false);
			dispose();
			m_clDownloadDialog = null;
			if (UserParameter.instance().updateCheck) {
				UpdateController.check4update(false);
			}
		} else if (e.getSource().equals(m_jbAbort)) {
			setVisible(false);
			dispose();
			m_clDownloadDialog = null;
		} else if (e.getSource().equals(m_jbProxy)) {
			new ProxyDialog(HOMainFrame.instance());
		}
	}

	/**
	 * Fill season list box.
	 */
	private void fillOldFixturesList() {
		final int aktuelleSaison = hov.getModel().getBasics().getSeason();
		var activationDate = hov.getModel().getBasics().getActivationDate();
		var htWeek = activationDate.toLocaleHTWeek();
		var seasons = DBManager.instance().getAllSpielplaene(false);
		final DefaultListModel listModel = new DefaultListModel();
		for (int i = aktuelleSaison; i >= htWeek.season; i--) {
			int finalI = i;
			var season = seasons.stream().filter(f -> f.getSaison() == finalI).findFirst();
			var itemText = new StringBuilder(hov.getLanguageString("Season")).append(" ").append(i);
			if (season.isPresent()) {
				itemText.append(" / ").append(season.get().getLigaName());
			}
			listModel.addElement(new CBItem(itemText.toString(), i));
		}
		m_jlOldSeasons.setModel(listModel);
		m_jchOldFixtures.setEnabled(!listModel.isEmpty());
	}

	/**
	 * Initialize the GUI components.
	 */
	private void initComponents() {
		setResizable(false);
		setContentPane(new ImagePanel(null));

		if ( !isNtTeam) {
			final JPanel normalDownloadPanel = new ImagePanel(new GridLayout(3, 1, 4, 4));
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
			downloadFilter.checkNode(filterRoot.getSeriesData(), UserParameter.instance().fixtures);

			normalDownloadPanel.setLayout(new BorderLayout());
			normalDownloadPanel.add(new JScrollPane(downloadFilter), BorderLayout.CENTER);
			normalDownloadPanel.setSize(240, 280);
			normalDownloadPanel.setLocation(10, 10);
			getContentPane().add(normalDownloadPanel);

			final JPanel specialDownload = new ImagePanel(new GridLayout(1, 1, 4, 4));
			specialDownload.setBorder(BorderFactory.createTitledBorder(hov.getLanguageString("Verschiedenes")));

			// Alte Spielpläne
			final JPanel oldFixturePanel = new ImagePanel(new BorderLayout());

			m_jchOldFixtures.setToolTipText(hov.getLanguageString("download.oldseriesdata.tt"));
			m_jchOldFixtures.addActionListener(this);
			m_jchOldFixtures.setOpaque(false);
			oldFixturePanel.add(m_jchOldFixtures, BorderLayout.NORTH);

			m_jlOldSeasons.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			m_jlOldSeasons.setEnabled(false);
			fillOldFixturesList();
			oldFixturePanel.add(new JScrollPane(m_jlOldSeasons), BorderLayout.CENTER);

			// MatchArchive
			final JPanel matchArchivePanel = new JPanel(new BorderLayout(1, 2));
			matchArchivePanel.setOpaque(false);

			m_jchMatchArchive.setToolTipText(hov.getLanguageString("download.oldmatches.tt"));
			m_jchMatchArchive.addActionListener(this);
			m_jchMatchArchive.setOpaque(false);
			matchArchivePanel.add(m_jchMatchArchive, BorderLayout.WEST);

			m_clSpinnerModel.setCalendarField(java.util.Calendar.MONTH);
			((JSpinner.DateEditor) m_jsSpinner.getEditor()).getFormat().applyPattern("dd.MM.yyyy");
			matchArchivePanel.add(m_jsSpinner, BorderLayout.EAST);

			// Show HRF FileDialog
			m_jchShowSaveDialog.setToolTipText(hov.getLanguageString("tt_Optionen_Show_SaveHRF_Dialog"));
			m_jchShowSaveDialog.setOpaque(false);
			m_jchShowSaveDialog.addActionListener(this);

			final JPanel diverseOptionsPanel = new ImagePanel(new BorderLayout(1, 2));
			diverseOptionsPanel.add(matchArchivePanel, BorderLayout.NORTH);
			diverseOptionsPanel.add(m_jchShowSaveDialog, BorderLayout.SOUTH);

			oldFixturePanel.add(diverseOptionsPanel, BorderLayout.SOUTH);

			specialDownload.add(oldFixturePanel);

			specialDownload.setSize(260, 280);
			specialDownload.setLocation(260, 10);
			getContentPane().add(specialDownload);
		}
		else {
			// isNtTeam
			final JPanel normalDownloadPanel = new ImagePanel(new GridLayout(3, 1, 4, 4));
			normalDownloadPanel.setBorder(BorderFactory.createTitledBorder(hov.getLanguageString("ls.button.download")));
			JTextArea ta = new JTextArea();
			ta.append(hov.getLanguageString("nthrf.hint1")+"\n");
			ta.append(hov.getLanguageString("nthrf.hint2")+"\n");
			ta.append(hov.getLanguageString("nthrf.hint3")+"\n");
			ta.append(hov.getLanguageString("nthrf.hint4")+" '");
			ta.append(hov.getLanguageString("Start")+"' ");
			ta.append(hov.getLanguageString("nthrf.hint5"));
			ta.setEditable(false);

			normalDownloadPanel.setLayout(new BorderLayout());
			normalDownloadPanel.add(new JScrollPane(ta), BorderLayout.CENTER);
			normalDownloadPanel.setSize(480, 280);
			normalDownloadPanel.setLocation(10, 10);
			getContentPane().add(normalDownloadPanel);

		}
		m_jbDownload.setToolTipText(hov.getLanguageString("tt_Download_Start"));
		m_jbDownload.addActionListener(this);
		m_jbDownload.setFont(m_jbDownload.getFont().deriveFont(Font.BOLD));
		m_jbDownload.setSize(140, 30);
		m_jbDownload.setLocation(10, 300);
		InputMap buttonKeys = m_jbDownload.getInputMap(JButton.WHEN_FOCUSED);
		buttonKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0,false), "pressed");
		buttonKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0,true), "released");

		getContentPane().add(m_jbDownload);

		m_jbProxy.setToolTipText(hov.getLanguageString("tt_ConfigureProxy"));
		m_jbProxy.addActionListener(this);
		m_jbProxy.setFont(m_jbProxy.getFont().deriveFont(Font.BOLD));
		m_jbProxy.setSize(140, 30);
		m_jbProxy.setLocation(195, 300);

		getContentPane().add(m_jbProxy);

		m_jbAbort.setToolTipText(hov.getLanguageString("tt_Download_Abbrechen"));
		m_jbAbort.addActionListener(this);
		m_jbAbort.setSize(140, 30);
		m_jbAbort.setLocation(380, 300);
		getContentPane().add(m_jbAbort);

		setSize(550, 395);

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
		UserParameter.instance().xmlDownload = m_jchHRF.isSelected();
		UserParameter.instance().fixtures = m_jchFixtures.isSelected();

		if (m_jchHRF.isSelected()) {
			bOK = OnlineWorker.getHrf(this);
			List<Player> player = hov.getModel().getCurrentPlayers();
			for (Player p : player) {
				if (p.getNationalTeamID() != 0) {
					OnlineWorker.getMatches(p.getNationalTeamID(), false, true, true);
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

			if (bOK && m_jchFixtures.isSelected()) {
				// in the last week of a season the LeagueLevelUnitID switches to the next season's value (no fixtures are available then)
				if (model.getBasics().getSpieltag() < 16) {
					HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.fixtures"), progressIncrement);
					var fixtures = OnlineWorker.downloadLeagueFixtures(-1, model.getXtraDaten().getLeagueLevelUnitID());
					if (fixtures != null) {
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
							hov.getModel().saveFixtures(fixtures);
						}
					} else {
						bOK = false;
					}
				}
			}

			if (bOK && m_jchOldFixtures.isSelected()) {
				HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.fixtures"), progressIncrement);
				for (Object s : m_jlOldSeasons.getSelectedValuesList()) {
					if (s instanceof CBItem) {
						final int seasonId = ((CBItem) s).getId();

						// download matches of the season to get the correct league id
						var leagueId = HOVerwaltung.instance().getModel().getXtraDaten().getLeagueLevelUnitID();
						var matches = OnlineWorker.downloadMatchesOfSeason(teamId, seasonId);
						if ( matches != null) {
							DBManager.instance().storeMatchKurzInfos(matches);
							var leagueMatch = matches.stream()
									.filter(i -> i.getMatchType() == MatchType.LEAGUE && i.getMatchStatus() == MatchKurzInfo.FINISHED).findFirst();
							if (leagueMatch.isPresent()) {
								leagueId = leagueMatch.get().getMatchContextId();
							}
							// Abfragen!
							final LigaAuswahlDialog auswahlDialog = new LigaAuswahlDialog(this, seasonId, leagueId);
							leagueId = auswahlDialog.getLigaID();

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
			}
		}

		DBManager.instance().updateLatestData();
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.calc_subskills"), progressIncrement);
		model.calcSubskills();

		HOMainFrame.instance().setInformationCompleted();

	}

	private void startNtDownload() {
		try {
			var teams = NthrfUtil.getNtTeams();
			if (teams == null || teams.size() < 1 || teams.get(0)[0] == null || teams.get(0)[0].length() < 1) {
				return;
			}
			final long teamId;
			if (teams.size() > 1) {
				NtTeamChooser chooser = new NtTeamChooser(teams);
				chooser.setModal(true);
				chooser.setVisible(true);
				teamId = chooser.getSelectedTeamId();
				//System.out.println("Result is: " + chooser.getSelectedTeamId());
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
					DBManager.instance().updateLatestData();
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
			e.printStackTrace();
		}
	}
}
