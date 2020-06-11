// %1605218128:de.hattrickorganizer.gui.playeroverview%
package module.playerOverview;

import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.Player;

import java.awt.*;
import java.awt.event.AdjustmentListener;

import javax.swing.*;

/**
 * Overview of all the players on the team (main class of the package)
 */
public class SpielerUebersichtsPanel extends ImagePanel {

	private static final long serialVersionUID = -5795792661614081193L;
	private JSplitPane horizontalRightSplitPane;
	private JSplitPane verticalSplitPane;
	private SpielerDetailPanel spielerDetailPanel;
	private SpielerTrainingsSimulatorPanel spielerTrainingsSimulatorPanel;
	private SpielerTrainingsVergleichsPanel spielerTrainingsVergleichsPanel;
	private SpielerUebersichtNamenTable spielerUebersichtTableName;
	private PlayerOverviewTable playerOverviewTable;
	private TeamSummaryPanel teamSummaryPanel;

	/**
	 * Creates a new SpielerUebersichtsPanel object. (Players view panel)
	 */
	public SpielerUebersichtsPanel() {
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
		spielerUebersichtTableName.setSpieler(player.getSpielerID());
		playerOverviewTable.setSpieler(player.getSpielerID());
		spielerDetailPanel.setSpieler(player);
		spielerTrainingsSimulatorPanel.setSpieler(player);
	}

	/**
	 * Returns Width of the best position column
	 */
	public final int getBestPosWidth() {
		return playerOverviewTable.getBestPosWidth();
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

	public final void saveColumnOrder() {
		playerOverviewTable.saveColumnOrder();
	}

	// ----------------------Refresh--

	/**
	 * Refresh, wenn ein Player in der Aufstellung geändert wird (Google
	 * translate) Refresh, if a player is changed in the statement
	 */
	public final void refresh() {
		spielerDetailPanel.refresh();
		playerOverviewTable.refresh();
	}

	/**
	 * Erneuert alle Spalten, die bei von einem Vergleich betroffen sind (Google
	 * translate) Renewed all the columns that are affected by a comparison with
	 */
	public final void refreshHRFVergleich() {
		playerOverviewTable.refreshHRFVergleich();

		Player player = playerOverviewTable.getSorter().getSpieler(playerOverviewTable.getSelectedRow());
		spielerDetailPanel.setSpieler(player);
	}

	/**
	 * Refeshes the table here and in the lineup panel when the groups / info has been changed
	 */
	public final void update() {
		refresh();
		HOMainFrame.instance().getAufstellungsPanel().refresh();
	}

	// ----------init-----------------------------------------------
	private void initComponents() {
		setLayout(new BorderLayout());

		Component tabelle = initSpielerTabelle();
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
		spielerDetailPanel = new SpielerDetailPanel();

		JScrollPane scrollPane = new JScrollPane(spielerDetailPanel);
		scrollPane.getVerticalScrollBar().setBlockIncrement(100);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("SpielerDetails"), scrollPane);

		spielerTrainingsSimulatorPanel = new SpielerTrainingsSimulatorPanel();
		scrollPane = new JScrollPane(spielerTrainingsSimulatorPanel);
		scrollPane.getVerticalScrollBar().setBlockIncrement(100);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Skilltester"), scrollPane);

		return tabbedPane;
	}

	/*
	 * Initialise the players history
	 */
	private Component initSpielerHistory() {
		JPanel panel = new ImagePanel();
		panel.setLayout(new BorderLayout());
		spielerTrainingsVergleichsPanel = new SpielerTrainingsVergleichsPanel();

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
	private Component initSpielerTabelle() {

		JPanel overviewPanel = new JPanel();
		overviewPanel.setLayout(new BorderLayout());

		// table with the player's details
		playerOverviewTable = new PlayerOverviewTable();

		// table with the player's name
		spielerUebersichtTableName = new SpielerUebersichtNamenTable(playerOverviewTable.getSorter());

		JScrollPane scrollpane = new JScrollPane(spielerUebersichtTableName);
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollpane.setPreferredSize(new Dimension(170, 100));

		JScrollPane scrollpane2 = new JScrollPane(playerOverviewTable);
		scrollpane2.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		scrollpane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		final JScrollBar bar = scrollpane.getVerticalScrollBar();
		final JScrollBar bar2 = scrollpane2.getVerticalScrollBar();

		// setVisible(false) does not have an effect, so we set the size to
		// false. We can't disable the scrollbar with VERTICAL_SCROLLBAR_NEVER
		// because this will disable mouse wheel scrolling.
		bar.setPreferredSize(new Dimension(0, 0));

		// Synchronize vertical scrolling
		AdjustmentListener adjustmentListener = e -> {
			if (e.getSource() == bar2) {
				bar.setValue(e.getValue());
			} else {
				bar2.setValue(e.getValue());
			}
		};
		bar.addAdjustmentListener(adjustmentListener);
		bar2.addAdjustmentListener(adjustmentListener);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		splitPane.setLeftComponent(scrollpane);
		splitPane.setRightComponent(scrollpane2);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(UserParameter.instance().playerTablePanel_horizontalSplitPane);

		overviewPanel.add(splitPane, BorderLayout.CENTER);

		TeamSummaryModel teamSummaryModel = new TeamSummaryModel();
		teamSummaryModel.setPlayers(HOVerwaltung.instance().getModel().getCurrentPlayers());
		teamSummaryPanel = new TeamSummaryPanel(teamSummaryModel);
		overviewPanel.add(teamSummaryPanel, BorderLayout.SOUTH);

		return overviewPanel;
	}

	private void selectRow(JTable table, int row) {
		if (row > -1) {
			table.setRowSelectionInterval(row, row);
		} else {
			table.clearSelection();
		}
	}

	/**
	 * Adds ListSelectionListener which keep the row selection of the table with
	 * the players name and the table with the players details in sync.
	 */
	private void addTableSelectionListeners() {
		playerOverviewTable.getSelectionModel().addListSelectionListener(
				e -> {
					selectRow(spielerUebersichtTableName, playerOverviewTable.getSelectedRow());
				});

		spielerUebersichtTableName.getSelectionModel().addListSelectionListener(
				e -> {
					final int row = spielerUebersichtTableName.getSelectedRow();
					selectRow(playerOverviewTable, row);

					// Set player on HOMainFrame to notify other tabs.
					if (row > -1) {
						Player player = playerOverviewTable.getSorter().getSpieler(row);
						if (player != null) {
							HOMainFrame.instance().setActualSpieler(player);
						}
					}
				});
	}
}
