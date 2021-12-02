package module.lineup;

import core.gui.HOMainFrame;
import core.gui.Updatable;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.Ratings;
import core.model.UserParameter;
import core.model.match.Weather;
import core.model.player.Player;
import module.lineup.assistant.LineupAssistantPanel;
import module.lineup.lineup.LineupPositionsPanel;
import module.lineup.lineup.PlayerPositionPanel;
import module.lineup.ratings.LineupRatingPanel;
import module.playerOverview.LineupPlayersTableNameColumn;
import module.playerOverview.PlayerTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Master panel of the Lineup module
 */
public class LineupPanel extends core.gui.comp.panel.ImagePanel {

	public final static Color TITLE_FG = ThemeManager.getColor(HOColorName.LINEUP_HIGHLIGHT_FG);
	private LineupPositionsPanel lineupPositionsPanel;
	private LineupPlayersTable lineupPlayersTable;
	private LineupRatingAssistantPanel lineupRatingAssistantPanel;
	private JSplitPane horizontalSplitPane;
	private JSplitPane verticalSplitPane;
	private LineupPlayersTableNameColumn lineupPlayersTableNameColumn;
	private List<Updatable> updatables = new ArrayList<>();

	public LineupPanel() {
		initComponents();
		addListeners();
	}

	public void setPlayer(int idPlayer) {
		lineupPlayersTableNameColumn.setPlayer(idPlayer);
		lineupPlayersTable.setPlayer(idPlayer);
	}

	public void refresh() {
		lineupPlayersTable.refresh();
	}

	private LineupAssistantPanel getLineupAssistantPanel() {return lineupRatingAssistantPanel.getLineupAssistantPanel(); }

	private LineupSettingsPanel getLineupSettingsPanel() {return lineupRatingAssistantPanel.getLineupSettingsPanel();}

	private LineupRatingPanel getLineupRatingPanel(){ return lineupRatingAssistantPanel.getLineupRatingPanel();}

	private LineupPositionsPanel getLineupPositionsPanel() {
		if ( lineupPositionsPanel == null){
			lineupPositionsPanel = new LineupPositionsPanel(this, getLineupRatingAssistantPanel().getLineupSettingsPanel().getWeather(), true);
		}
		return lineupPositionsPanel;
	}


	/**
	 * Get the divider location to restore user previous view organization
	 */
	public final int[] getDividerLocations() {
		final int[] locations = new int[4];

		locations[0] = verticalSplitPane.getDividerLocation();
		locations[1] = horizontalSplitPane.getDividerLocation();
		return locations;
	}

	public void saveColumnOrder() {
		lineupPlayersTable.saveColumnOrder();
	}

	/**
	 * Refresh the players and tactics of each Lineup panels
	 */
	public final void update() {
		lineupPositionsPanel.refresh();
		lineupRatingAssistantPanel.refresh();
		lineupPlayersTable.refresh();
		lineupPlayersTableNameColumn.refresh();

		// Refresh the table and details of the player overview
		core.gui.HOMainFrame.instance().getSpielerUebersichtPanel().refresh();

		fireUpdate();
	}

	public void addUpdateable(Updatable updatable) {
		this.updatables.add(updatable);
	}


	private void fireUpdate() {
		for (int i = this.updatables.size() - 1; i >= 0; i--) {
			this.updatables.get(i).update();
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		var lineupRatingAssistantPanel = getLineupRatingAssistantPanel();

		verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		var lineupPositionsPanel = getLineupPositionsPanel();
		verticalSplitPane.setTopComponent(new JScrollPane(lineupPositionsPanel));
		verticalSplitPane.setBottomComponent(initSpielerTabelle());

		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		horizontalSplitPane.setLeftComponent(verticalSplitPane);
		var lineupPositionJScrollPane = new JScrollPane();
		lineupPositionJScrollPane.getViewport().add(lineupRatingAssistantPanel);
		horizontalSplitPane.setRightComponent(lineupPositionJScrollPane);

		UserParameter param = UserParameter.instance();
		verticalSplitPane.setDividerLocation(param.lineupPanel_verticalSplitLocation);
		horizontalSplitPane.setDividerLocation(param.lineupPanel_horizontalSplitLocation);

		add(horizontalSplitPane, BorderLayout.CENTER);
	}

	private LineupRatingAssistantPanel getLineupRatingAssistantPanel() {
		if ( this.lineupRatingAssistantPanel == null){
			this.lineupRatingAssistantPanel = new LineupRatingAssistantPanel(this);
		}
		return this.lineupRatingAssistantPanel;
	}

	private Component initSpielerTabelle() {
		lineupPlayersTable = new LineupPlayersTable();
		lineupPlayersTableNameColumn = new LineupPlayersTableNameColumn(lineupPlayersTable.getSorter());

		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		JScrollPane scrollpane = new JScrollPane(lineupPlayersTableNameColumn);
		scrollpane.setPreferredSize(new Dimension(170, 100));
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JScrollPane scrollpane2 = new JScrollPane(lineupPlayersTable);
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
				if (e.getSource() == lineupPlayersTable.getSelectionModel()) {
					synchronizeSelection(lineupPlayersTable);
				} else if (e.getSource() == lineupPlayersTableNameColumn.getSelectionModel()) {
					synchronizeSelection(lineupPlayersTableNameColumn);
				}
			}

			private void synchronizeSelection(JTable sourceTable) {
				JTable targetTable;
				if (sourceTable == lineupPlayersTable) {
					targetTable = lineupPlayersTableNameColumn;
				} else {
					targetTable = lineupPlayersTable;
				}

				int row = sourceTable.getSelectedRow();
				if (row == -1) {
					targetTable.clearSelection();
				} else {					
					if (targetTable.getSelectedRow() != row) {
						targetTable.setRowSelectionInterval(row, row);
					}
					Player player = ((PlayerTable) sourceTable).getPlayer(row);
					if (player != null) {
						HOMainFrame.instance().setActualSpieler(player);
					}
				}
			}
		};

		this.lineupPlayersTable.getSelectionModel().addListSelectionListener(lsl);
		this.lineupPlayersTableNameColumn.getSelectionModel().addListSelectionListener(lsl);
	}

	public Weather getWeather() {
		return getLineupSettingsPanel().getWeather();
	}

	public boolean isAssistantGroupFilter() {
		return this.getLineupAssistantPanel().isGroupFilter();
	}

	public String getAssistantGroup() {
		return this.getLineupAssistantPanel().getGroup();
	}

	public boolean isAssistantSelectedGroupExcluded() {
		return this.getLineupAssistantPanel().isSelectedGroupExcluded();
	}

	public boolean isAssistantExcludeLastMatch() {
		return this.getLineupAssistantPanel().isExcludeLastMatch();
	}

	public void addToAssistant(PlayerPositionPanel positionPanel) {
		this.getLineupAssistantPanel().addToAssistant(positionPanel);
	}

	public void refreshLineupRatingPanel() {
		this.getLineupRatingPanel().refresh();
	}

	public void refreshLineupSettingsPanel() {
		this.getLineupSettingsPanel().refresh();
	}

	public boolean isTeamAttitudeInitialized() {
		return this.getLineupPositionsPanel().is_jcbTeamAttitudeInitialized();
	}

	public void setEnabledTeamAttitudeCB(boolean b) {
		this.getLineupPositionsPanel().setEnabledTeamAttitudeCB(b);
	}

	public void resetSettings() {
		this.getLineupSettingsPanel().resetSettings();
	}

	public void updateLineupPositions() {
		this.getLineupPositionsPanel().update();
	}

	public void setPreviousRatings(Ratings oRatingsBefore) {
		this.getLineupRatingPanel().setPreviousRatings(oRatingsBefore);
	}

	public void calculateRatings() {
		this.getLineupRatingPanel().calculateRatings();
	}

	public void setAssistantGroupFilter(boolean b) {
		this.getLineupAssistantPanel().setGroupFilter(b);
	}

	public int updateStyleOfPlayComboBox(int oldStyleOfPlay) {
		return this.getLineupPositionsPanel().updateStyleOfPlayComboBox(oldStyleOfPlay);
	}

	public void refreshLineupPositionsPanel() {
		this.getLineupPositionsPanel().refresh();
	}

	public void addPositionComponent(Component component, Object constraints, int i) {
		this.getLineupPositionsPanel().getCenterPanel().add(component, constraints, i);
	}

	public void revalidatePositionComponents() {
		this.getLineupPositionsPanel().getCenterPanel().revalidate();
	}

	public void removePositionComponent(Component component) {
		this.getLineupPositionsPanel().getCenterPanel().remove(component);
	}

	public int getAssistantOrder() {
		return this.getLineupAssistantPanel().getOrder();
	}

	public boolean isAssistantBestPositionFirst() {
		return this.getLineupAssistantPanel().isIdealPositionZuerst();
	}

	public boolean isAssistantConsiderForm() {
		return this.getLineupAssistantPanel().isConsiderForm();
	}

	public boolean isAssistantIgnoreInjured() {
		return this.getLineupAssistantPanel().isIgnoreInjured();
	}

	public boolean isAssistantIgnoreSuspended() {
		return this.getLineupAssistantPanel().isIgnoreSuspended();
	}

	public Map<Integer, Boolean> getAssistantPositionsStatus() {
		return this.getLineupAssistantPanel().getPositionsStatus();
	}

	public void backupRealGameSettings() {
		this.getLineupSettingsPanel().backupRealGameSettings();
	}

	public void setLineupSettingsLabels() {
		this.getLineupSettingsPanel().setLabels();
	}

	public ArrayList<PlayerPositionPanel> getAllPositions() {
		return this.getLineupPositionsPanel().getAllPositions();
	}

	public boolean isSelectedMatchCompetitive() {
		return this.getLineupRatingAssistantPanel().isSelectedMatchCompetitive();
	}
}
