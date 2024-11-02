package module.playerOverview;

import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.player.Player;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Overview of all the players on the team (main class of the package)
 */
public class PlayerOverviewPanel extends ImagePanel {

	private JSplitPane horizontalRightSplitPane;
	private JSplitPane verticalSplitPane;
	private PlayerDetailsPanel playerDetailsPanel;
	private SpielerTrainingsSimulatorPanel spielerTrainingsSimulatorPanel;
	private PlayerOverviewTable playerOverviewTable;
	private TeamSummaryPanel teamSummaryPanel;

	/**
	 * Creates a new SpielerUebersichtsPanel object. (Players view panel)
	 */
	public PlayerOverviewPanel() {
		initComponents();
		addTableSelectionListeners();
	}

	/**
	 * Selects the player with the given id.
	 * 
	 * @param player
	 *            the id of the player to select.
	 */
	public void setPlayer(Player player) {
		playerOverviewTable.selectPlayer(player.getPlayerId());
		playerDetailsPanel.setPlayer(player);
		spielerTrainingsSimulatorPanel.setSpieler(player);
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
		playerDetailsPanel.refresh();
		playerOverviewTable.refresh();
	}

	/**
	 * Updates all the columns affected by a comparison.
	 */
	public final void refreshHRFComparison() {
		playerOverviewTable.refreshHRFComparison();
		playerDetailsPanel.setPlayer(playerOverviewTable.getSelectedPlayer());
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
		playerDetailsPanel = new PlayerDetailsPanel(playerOverviewTable);

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
		panel.add(new JScrollPane(new RemoveGruppenPanel(playerOverviewTable)), BorderLayout.NORTH);

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
		playerOverviewTable = new PlayerOverviewTable();
		overviewPanel.add(playerOverviewTable.getContainerComponent(), BorderLayout.CENTER);
		TeamSummaryModel teamSummaryModel = new TeamSummaryModel();
		teamSummaryModel.setPlayers(HOVerwaltung.instance().getModel().getCurrentPlayers());
		teamSummaryPanel = new TeamSummaryPanel(teamSummaryModel);
		overviewPanel.add(teamSummaryPanel, BorderLayout.SOUTH);
		return overviewPanel;
	}


	private boolean areSelecting = false;
	/**
	 * Adds ListSelectionListener which keep the row selection of the table with
	 * the players name and the table with the players details in sync.
	 */
	private void addTableSelectionListeners() {
		playerOverviewTable.getSelectionModel().addListSelectionListener(
				e -> {
					if (!areSelecting) {
						areSelecting = true;
						var player = playerOverviewTable.getSelectedPlayer();
						if (player == null) {
							player = HOMainFrame.instance().getSelectedPlayer();
							playerOverviewTable.selectPlayer(player.getPlayerId());
						} else {
							HOMainFrame.instance().selectPlayer(player);
						}
						areSelecting = false;
					}
				}
		);
	}

    public void storeUserSettings() {
		playerOverviewTable.getPlayerTableModel().storeUserSettings();
    }
}