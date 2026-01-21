package module.playerOverview;

import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.UserParameter;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Overview of all the players on the team (main class of the package)
 */
public class PlayerOverviewPanel extends ImagePanel implements Refreshable {

	private JSplitPane horizontalRightSplitPane;
	private JSplitPane verticalSplitPane;
	private PlayerDetailsPanel playerDetailsPanel;
	private SpielerTrainingsSimulatorPanel spielerTrainingsSimulatorPanel;
	private PlayersTable playerOverviewTable;
	private TeamSummaryPanel teamSummaryPanel;

	/**
	 * Creates a new player overview. (Players view panel)
	 */
	public PlayerOverviewPanel() {
		initComponents();
		RefreshManager.instance().registerRefreshable(this);
		this.playerOverviewTable.addListSelectionListener(e -> selectPlayer());
		this.playerOverviewTable.setRowSelectionInterval(0, 0);
	}

	/**
	 * Selects the player
	 */
	public void selectPlayer() {
		var players = this.playerOverviewTable.getSelectedPlayers();
		for (var player : players ) {
			playerDetailsPanel.setPlayer(player);
			spielerTrainingsSimulatorPanel.setSpieler(player);
		}
	}

	/**
	 * Returns the current Divider Locations so they can be stored.
	 */
	public final int[] getDividerLocations() {
		int[] locations = new int[3];
		locations[0] = 0;
		locations[1] = horizontalRightSplitPane.getDividerLocation();
		locations[2] = verticalSplitPane.getDividerLocation();
		return locations;
	}

	/**
	 * Refresh, if a player is changed in the lineup
	 */
	public final void refresh() {
		playerOverviewTable.refresh();
		playerDetailsPanel.refresh();
	}

	/**
	 * Updates all the columns affected by a comparison.
	 */
	public final void refreshHRFComparison() {
		var playerTableModel = (PlayerOverviewTableModel)playerOverviewTable.getModel();
		playerTableModel.reInitData();
	}

	/**
	 * Refreshes the table here and in the lineup panel when the groups / info has been changed
	 */
	public final void update() {
		refresh();
		Objects.requireNonNull(HOMainFrame.instance().getLineupPanel()).refresh();
	}

	// ----------init-----------------------------------------------
	private void initComponents() {
		setLayout(new BorderLayout());

		Component tabelle = initPlayersTable();
		horizontalRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		horizontalRightSplitPane.setLeftComponent(initSpielerDetail());
		horizontalRightSplitPane.setRightComponent(initSpielerHistory());

		verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		verticalSplitPane.setLeftComponent(tabelle);
		verticalSplitPane.setRightComponent(horizontalRightSplitPane);

		horizontalRightSplitPane
				.setDividerLocation(UserParameter.instance().spielerUebersichtsPanel_horizontalRightSplitPane);
		verticalSplitPane
				.setDividerLocation(UserParameter.instance().spielerUebersichtsPanel_verticalSplitPane);

		add(verticalSplitPane, BorderLayout.CENTER);
	}

	/*
	 * Initialise the players details
	 */
	private Component initSpielerDetail() {
		JTabbedPane tabbedPane = new JTabbedPane();
		playerDetailsPanel = new PlayerDetailsPanel();

		JScrollPane scrollPane = new JScrollPane(playerDetailsPanel);
		scrollPane.getVerticalScrollBar().setBlockIncrement(100);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		tabbedPane.addTab(TranslationFacility.tr("SpielerDetails"), scrollPane);

		spielerTrainingsSimulatorPanel = new SpielerTrainingsSimulatorPanel();
		scrollPane = new JScrollPane(spielerTrainingsSimulatorPanel);
		scrollPane.getVerticalScrollBar().setBlockIncrement(100);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		tabbedPane.addTab(TranslationFacility.tr("Skilltester"), scrollPane);

		return tabbedPane;
	}

	/*
	 * Initialise the players history
	 */
	private Component initSpielerHistory() {
		JPanel panel = new ImagePanel();
		panel.setLayout(new BorderLayout());
		SpielerTrainingsVergleichsPanel spielerTrainingsVergleichsPanel = new SpielerTrainingsVergleichsPanel();

		final JScrollPane scrollPane = new JScrollPane(spielerTrainingsVergleichsPanel);
		scrollPane.getVerticalScrollBar().setBlockIncrement(100);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		panel.add(spielerTrainingsVergleichsPanel, BorderLayout.CENTER);
		panel.add(new JScrollPane(new RemoveGruppenPanel()), BorderLayout.NORTH);

		if (teamSummaryPanel != null) {
			spielerTrainingsVergleichsPanel.addChangeListener(teamSummaryPanel);
		}

		return panel;
	}

	/*
	 * Initialise the players tables
	 */
	private Component initPlayersTable() {
		JPanel overviewPanel = new JPanel();
		overviewPanel.setLayout(new BorderLayout());

		// table with the player's details
		var playerOverviewTableModel = UserColumnController.instance().getPlayerOverviewModel();
		playerOverviewTable = new PlayersTable(playerOverviewTableModel, 1);
		overviewPanel.add(playerOverviewTable.getContainerComponent(), BorderLayout.CENTER);
		TeamSummaryModel teamSummaryModel = new TeamSummaryModel();
		teamSummaryModel.setPlayers(HOVerwaltung.instance().getModel().getCurrentPlayers());
		teamSummaryPanel = new TeamSummaryPanel(teamSummaryModel);
		var scrollPane = new JScrollPane(teamSummaryPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension((int) teamSummaryPanel.getPreferredSize().getWidth(),
				(int) teamSummaryPanel.getPreferredSize().getHeight() + 22));
		overviewPanel.add(scrollPane, BorderLayout.SOUTH);

		playerOverviewTableModel.initData();
		return overviewPanel;
	}

    public void storeUserSettings() {
		var playerOverviewTableModel = (PlayerOverviewTableModel) playerOverviewTable.getModel();
		playerOverviewTableModel.storeUserSettings();
	}

	@Override
	public void reInit() {
		refresh();
	}
}