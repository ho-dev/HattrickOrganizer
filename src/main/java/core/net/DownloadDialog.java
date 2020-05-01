// %1562433378:de.hattrickorganizer.gui.menu%
package core.net;

import core.datatype.CBItem;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.CheckBoxTree.CheckBoxTree;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.ho.HOTheme;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.MatchKurzInfo;
import core.model.player.Player;
import core.net.login.ProxyDialog;
import core.util.HOLogger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeModel;


/**
 * Dialog, der den User den Download von verschiedenen Daten aus Hattrick heraus
 * ermöglicht
 */
public class DownloadDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 7837303870465506844L;

	// ~ Instance fields
	// ----------------------------------------------------------------------------/
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


	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Creates a new DownloadDialog object.
	 */
	public DownloadDialog() {
		super(HOMainFrame.instance(), hov.getLanguageString("ls.menu.file.download"), true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initComponents();
	}

	// ~ Methods
	// ------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	@Override
	public final void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(m_jchOldFixtures)) {
			m_jlOldSeasons.setEnabled(m_jchOldFixtures.isSelected());
		} else if (e.getSource().equals(m_jbDownload)) {
			startDownload();
			RefreshManager.instance().doReInit();
			setVisible(false);
			dispose();
		} else if (e.getSource().equals(m_jbAbort)) {
			setVisible(false);
			dispose();
		} else if (e.getSource().equals(m_jbProxy)) {
			new ProxyDialog(HOMainFrame.instance());
		}
	}

	/**
	 * Fill season list box.
	 */
	private void fillOldFixturesList() {
		final int aktuelleSaison = hov.getModel().getBasics().getSeason();
		final DefaultListModel listModel = new DefaultListModel();

		for (int i = aktuelleSaison; i > 0; i--) {
			listModel.addElement(new CBItem(hov.getLanguageString("Season") + " " + i, i));
		}
		m_jlOldSeasons.setModel(listModel);
	}

	/**
	 * Initialize the GUI components.
	 */
	private void initComponents() {
		if (HOTheme.getDefaultFont() != null) {
			setFont(HOTheme.getDefaultFont());
		}
		setResizable(false);
		setContentPane(new ImagePanel(null));

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

		final JPanel diverseOptionsPanel = new JPanel(new BorderLayout(1,2));
		diverseOptionsPanel.add(matchArchivePanel, BorderLayout.NORTH);
		diverseOptionsPanel.add(m_jchShowSaveDialog, BorderLayout.SOUTH);

		oldFixturePanel.add(diverseOptionsPanel, BorderLayout.SOUTH);

		specialDownload.add(oldFixturePanel);



		specialDownload.setSize(260, 280);
		specialDownload.setLocation(260, 10);
		getContentPane().add(specialDownload);

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

		setSize(530, 360);

		final Dimension size = getToolkit().getScreenSize();

		if (size.width > this.getSize().width) {
			// Center
			this.setLocation((size.width / 2) - (this.getSize().width / 2), (size.height / 2) - (this.getSize().height / 2));
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				HOLogger.instance().log(getClass(), "Request focus 1");
				boolean succ = m_jbDownload.requestFocusInWindow();
				HOLogger.instance().log(getClass(), "Request success 1: " + succ);
			}
		});

		setVisible(true);
	}

	/**
	 * The download action.
	 */
	private void startDownload() {
		boolean bOK = true;
		HOModel model = hov.getModel();
		int teamId = model.getBasics().getTeamId();

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

		// Always test that teamId exists, it won't on the very first download
		if ( this.downloadFilter.isChecked(filterRoot.getCurrentMatches())  && (teamId > 0)) {
			// Only get lineups for own fixtures
			bOK = (OnlineWorker.getMatches(teamId, false, true, true) != null);
			if (bOK) {
				OnlineWorker.getAllLineups();
			}
		}
		if (bOK && m_jchMatchArchive.isSelected() && (teamId > 0)) {
			List<MatchKurzInfo> allmatches = OnlineWorker.getMatchArchive(teamId, m_clSpinnerModel.getDate(), false);
			allmatches = OnlineWorker.FilterUserSelection(allmatches);
			for ( MatchKurzInfo i: allmatches ) {
				OnlineWorker.downloadMatchData(i, true);
			}
		}

		if (bOK && m_jchFixtures.isSelected() && (teamId > 0)) {
			// Always get actual season and league
			OnlineWorker.getSpielplan(-1, model.getXtraDaten().getLeagueLevelUnitID());
		}

		if (bOK && m_jchOldFixtures.isSelected() && (teamId > 0)) {
			for (Object s : m_jlOldSeasons.getSelectedValuesList()) {
				if (s instanceof CBItem) {
					final int seasonId = ((CBItem) s).getId();
					// Abfragen!
					final LigaAuswahlDialog auswahlDialog = new LigaAuswahlDialog(this, seasonId);
					final int leagueId = auswahlDialog.getLigaID();

					if (leagueId > -2) {
						bOK = OnlineWorker.getSpielplan(seasonId, leagueId);
					}
					if (!bOK) {
						break;
					}
				}
			}
		}

		// Lastly, so that the matches for training are there
		if (bOK && m_jchHRF.isSelected()) {
			OnlineWorker.getHrf(this);
			List<Player> player = hov.getModel().getAllSpieler();
			for ( Player p : player){
				if ( p.getNationalTeamID() != 0){
					OnlineWorker.getMatches(p.getNationalTeamID(), false, true, true);
				}
			}
		}
	}
}
