package module.lineup;

import core.gui.HOMainFrame;
import core.gui.Updateable;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.Weather;
import core.model.player.Player;
import module.playerOverview.PlayerTable;
import module.playerOverview.SpielerUebersichtNamenTable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Panel to display players' positions.
 */
public class LineupPanel extends core.gui.comp.panel.ImagePanel {

	private IAufstellungsAssistentPanel aufstellungsAssistentPanel;
	private LineupSettingSimulationPanel lineupSettingSimulationPanel;
	private LineupPositionsPanel aufstellungsPositionsPanel;
	private AustellungSpielerTable aufstellungSpielerTable;
	private JSplitPane horizontalLeftSplitPane;
	private JSplitPane horizontalRightSplitPane;
	private JSplitPane verticalSplitPane;
	private JSplitPane verticalSplitPaneLow;
	private SpielerUebersichtNamenTable aufstellungSpielerTableName;
	private List<Updateable> updateables = new ArrayList<>();

	/**
	 * Creates a new AufstellungsPanel object.
	 */
	public LineupPanel() {
		initComponents();
		addListeners();
	}

	/**
	 * Selects the player with the given id.
	 * 
	 * @param idPlayer
	 *            the id of the player to select.
	 */
	public void setPlayer(int idPlayer) {
		aufstellungSpielerTableName.setSpieler(idPlayer);
		aufstellungSpielerTable.setSpieler(idPlayer);
	}

	/**
	 * Refreshes the view.
	 */
	public void refresh() {
		aufstellungSpielerTable.refresh();
	}

	/**
	 * Gibt das AufstellungsAssistentPanel zurück
	 * 
	 */
	public final IAufstellungsAssistentPanel getAufstellungsAssistentPanel() {
		return aufstellungsAssistentPanel;
	}

	/**
	 * Gibt das AufstellungsDetailPanel zurück
	 * 
	 */
	public final LineupSettingSimulationPanel getLineupSettingSimulationPanel() {
		return lineupSettingSimulationPanel;
	}

	/**
	 * Gibt das AufstellungsPositionsPanel zurück
	 * 
	 */
	public final LineupPositionsPanel getAufstellungsPositionsPanel() {
		return aufstellungsPositionsPanel;
	}

	/**
	 * Breite der BestPosSpalte zurückgeben
	 * 
	 */
	public final int getBestPosWidth() {
		return aufstellungSpielerTable.getBestPosWidth();
	}

	// --------------------------------------------------------

	/**
	 * Gibt die aktuellen DividerLocations zurück, damit sie gespeichert werden
	 * können
	 * 
	 */
	public final int[] getDividerLocations() {
		final int[] locations = new int[4];

		locations[0] = verticalSplitPaneLow.getDividerLocation();
		locations[1] = horizontalLeftSplitPane.getDividerLocation();
		locations[2] = horizontalRightSplitPane.getDividerLocation();
		locations[3] = verticalSplitPane.getDividerLocation();

		return locations;
	}

	public void saveColumnOrder() {
		aufstellungSpielerTable.saveColumnOrder();
	}

	/**
	 * Refresh the players and tactics of each Lineup panels
	 */
	public final void update() {
		aufstellungsPositionsPanel.refresh();
		lineupSettingSimulationPanel.refresh();
		aufstellungSpielerTable.refresh();
		aufstellungSpielerTableName.refresh();

		// Refresh the table and details of the player overview
		core.gui.HOMainFrame.instance().getSpielerUebersichtPanel().refresh();

		fireUpdate();
	}

	public void addUpdateable(Updateable updateable) {
		this.updateables.add(updateable);
	}


	private void fireUpdate() {
		for (int i = this.updateables.size() - 1; i >= 0; i--) {
			this.updateables.get(i).update();
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		verticalSplitPaneLow = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);

		final AufstellungsVergleichHistoryPanel aufstellungsVergleichHistoryPanel = new AufstellungsVergleichHistoryPanel();

		final Lineup aufstellung = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
		final Weather weather = aufstellung.getWeather();

		aufstellungsAssistentPanel = new AufstellungsAssistentPanel(weather);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("", ThemeManager.getScaledIcon(HOIconName.BALL, 13, 13),
				new JScrollPane((Component)aufstellungsAssistentPanel));
		tabbedPane.addTab("", ThemeManager.getIcon(HOIconName.DISK),
				aufstellungsVergleichHistoryPanel);

		aufstellungsPositionsPanel = new LineupPositionsPanel(this);
		horizontalLeftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		horizontalLeftSplitPane.setLeftComponent(new JScrollPane(aufstellungsPositionsPanel));
		horizontalLeftSplitPane.setRightComponent(initSpielerTabelle());

		lineupSettingSimulationPanel = new LineupSettingSimulationPanel();
		horizontalRightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		horizontalRightSplitPane.setLeftComponent(new JScrollPane(lineupSettingSimulationPanel));
		horizontalRightSplitPane.setRightComponent(tabbedPane);

		verticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		verticalSplitPane.setLeftComponent(horizontalLeftSplitPane);
		verticalSplitPane.setRightComponent(horizontalRightSplitPane);

		UserParameter param = UserParameter.instance();
		verticalSplitPaneLow.setDividerLocation(param.aufstellungsPanel_verticalSplitPaneLow);
		horizontalLeftSplitPane.setDividerLocation(param.aufstellungsPanel_horizontalLeftSplitPane);
		horizontalRightSplitPane.setDividerLocation(param.aufstellungsPanel_horizontalRightSplitPane);
		verticalSplitPane.setDividerLocation(param.aufstellungsPanel_verticalSplitPane);

		add(verticalSplitPane, BorderLayout.CENTER);
	}

	private Component initSpielerTabelle() {
		aufstellungSpielerTable = new AustellungSpielerTable();
		aufstellungSpielerTableName = new SpielerUebersichtNamenTable(aufstellungSpielerTable.getSorter());

		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		JScrollPane scrollpane = new JScrollPane(aufstellungSpielerTableName);
		scrollpane.setPreferredSize(new Dimension(170, 100));
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JScrollPane scrollpane2 = new JScrollPane(aufstellungSpielerTable);
		scrollpane2.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		scrollpane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		final JScrollBar bar = scrollpane.getVerticalScrollBar();
		final JScrollBar bar2 = scrollpane2.getVerticalScrollBar();
		// setVisible(false) does not have an effect, so we set the size to
		// false
		// we can't disable the scrollbar with VERTICAL_SCROLLBAR_NEVER
		// because this will disable mouse wheel scrolling
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

		splitPane.add(scrollpane);
		splitPane.add(scrollpane2);

		return splitPane;
	}

	private void addListeners() {

		ListSelectionListener lsl = new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getSource() == aufstellungSpielerTable.getSelectionModel()) {
					synchronizeSelection(aufstellungSpielerTable);
				} else if (e.getSource() == aufstellungSpielerTableName.getSelectionModel()) {
					synchronizeSelection(aufstellungSpielerTableName);
				}
			}

			private void synchronizeSelection(JTable sourceTable) {
				JTable targetTable;
				if (sourceTable == aufstellungSpielerTable) {
					targetTable = aufstellungSpielerTableName;
				} else {
					targetTable = aufstellungSpielerTable;
				}

				int row = sourceTable.getSelectedRow();
				if (row == -1) {
					targetTable.clearSelection();
				} else {					
					if (targetTable.getSelectedRow() != row) {
						targetTable.setRowSelectionInterval(row, row);
					}
					Player player = ((PlayerTable) sourceTable).getSpieler(row);
					if (player != null) {
						HOMainFrame.instance().setActualSpieler(player);
					}
				}
			}
		};

		this.aufstellungSpielerTable.getSelectionModel().addListSelectionListener(lsl);
		this.aufstellungSpielerTableName.getSelectionModel().addListSelectionListener(lsl);
	}
}
