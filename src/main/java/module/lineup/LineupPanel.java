package module.lineup;

import core.gui.Refreshable;
import core.gui.Updatable;
import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.UserParameter;
import core.model.match.Weather;
import module.lineup.assistant.LineupAssistantPanel;
import module.lineup.lineup.LineupPositionsPanel;
import module.lineup.lineup.MatchAndLineupSelectionPanel;
import module.lineup.lineup.PlayerPositionPanel;
import module.lineup.ratings.LineupRatingPanel;
import module.playerOverview.PlayerOverviewTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Master panel of the Lineup module
 */
public class LineupPanel extends core.gui.comp.panel.ImagePanel implements Refreshable {

	public static final Color TITLE_FG = ThemeManager.getColor(HOColorName.LINEUP_HIGHLIGHT_FG);
	private LineupPositionsPanel lineupPositionsPanel;
	private PlayersTable lineupPlayersTable;
	private LineupRatingAssistantPanel lineupRatingAssistantPanel;
	private JSplitPane horizontalSplitPane;
	private JSplitPane verticalSplitPane;
	private final List<Updatable> updatable = new ArrayList<>();
//	private boolean areSelecting = false;

	public LineupPanel() {
		initComponents();
		var playerOverviewTableModel = (PlayerOverviewTableModel)this.lineupPlayersTable.getModel();

//		for (var c : playerOverviewTableModel.getColumns()) {
//			if (c instanceof PlayerCheckBoxColumn playerCheckBoxColumn) {
//				if (playerCheckBoxColumn.isEditable()) {
//					var tableColumn = lineupPlayersTable.getColumn(c.getId());
//					if (tableColumn != null) {
//						tableColumn.setCellEditor(CheckBoxTableEntry.getEditor().getCellEditor());
//					}
//				}
//			}
//		}

		playerOverviewTableModel.reInitData();
	}

	public void storeUserSettings(){
		var playerOverviewTableModel = (PlayerOverviewTableModel)this.lineupPlayersTable.getModel();
		playerOverviewTableModel.storeUserSettings();
	}

//	public void setPlayer(int idPlayer) {
//		lineupPlayersTable.setPlayer(idPlayer);
//	}

	public void refresh() {
		lineupPlayersTable.refresh();
	}

	private LineupAssistantPanel getLineupAssistantPanel() {return lineupRatingAssistantPanel.getLineupAssistantPanel(); }

	private LineupSettingsPanel getLineupSettingsPanel() {return lineupRatingAssistantPanel.getLineupSettingsPanel();}

	private LineupRatingPanel getLineupRatingPanel(){ return lineupRatingAssistantPanel.getLineupRatingPanel();}

	private MatchAndLineupSelectionPanel getMatchAndLineupSelectionPanel() {
		return lineupRatingAssistantPanel.getMatchAndLineupSelectionPanel();
	}

	private LineupPositionsPanel getLineupPositionsPanel() {
		if ( lineupPositionsPanel == null){
			lineupPositionsPanel = new LineupPositionsPanel(
					this,
					getLineupRatingAssistantPanel().getLineupSettingsPanel().getWeather(), true,
					getLineupRatingAssistantPanel().getLineupRatingPanel().getSelectedMatchMinute());
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

//	public void saveColumnOrder() {
//		lineupPlayersTable.saveColumnOrder();
//	}

	/**
	 * Refresh the players and tactics of each Lineup panels
	 */
	public final void update() {
		lineupPositionsPanel.refresh();
		lineupRatingAssistantPanel.refresh();
		lineupPlayersTable.refresh();
		// Refresh the table and details of the player overview
		core.gui.HOMainFrame.instance().getSpielerUebersichtPanel().refresh();

		fireUpdate();
	}

	public void addUpdateable(Updatable updatable) {
		this.updatable.add(updatable);
	}

	private void fireUpdate() {
		for (int i = this.updatable.size() - 1; i >= 0; i--) {
			this.updatable.get(i).update();
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
		var model = UserColumnController.instance().getLineupModel();
		lineupPlayersTable = new PlayersTable(model);
//		for ( var modelColumn : model.getColumns()){
//			if ( modelColumn.isEditable()){
//				var tableColumn = lineupPlayersTable.getColumn(modelColumn.getId());
//				tableColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()));
//			}
//		}
//		lineupPlayersTable.getSelectionModel().addListSelectionListener(
//				e -> {
//					if (!areSelecting) {
//						areSelecting = true;
//						var player = lineupPlayersTable.getPlayer(e.getFirstIndex());
//						if (player == null) {
////							player = HOMainFrame.instance().getSelectedPlayer();
//							player = PlayersTable.Companion.getSelectedPlayer();
//							if (player != null) {
//								lineupPlayersTable.setPlayer(player.getPlayerId());
//							}
//						} else {
////							HOMainFrame.instance().selectPlayer(player);
//						}
//						areSelecting = false;
//					}
//				}
//		);
		return lineupPlayersTable.getContainerComponent();
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

	public void setAssistantGroupFilter(boolean b) {
		this.getLineupAssistantPanel().setGroupFilter(b);
	}

	public void updateStyleOfPlayComboBox() {
		this.getMatchAndLineupSelectionPanel().refresh();
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

	public boolean isUseAverageRating() {
		return  this.getLineupAssistantPanel().isUseAverageRating();
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

	public int getSelectedMatchMinute() {
		return this.lineupRatingAssistantPanel.getLineupRatingPanel().getSelectedMatchMinute();
	}

	@Override
	public void reInit() {
		refresh();
	}

}
