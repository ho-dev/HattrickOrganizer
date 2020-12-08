package module.lineup;

import core.gui.HOMainFrame;
import core.gui.Updatable;
import core.model.UserParameter;
import core.model.player.Player;
import module.lineup.assistant.ILineupAssistantPanel;
import module.lineup.ratings.LineupRatingPanel;
import module.playerOverview.PlayerTable;
import module.playerOverview.SpielerUebersichtNamenTable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Master panel of the Lineup module
 */
public class LineupPanel extends core.gui.comp.panel.ImagePanel {

	private LineupPositionsPanel lineupPositionsPanel;
	private LineupPlayersTable lineupPlayersTable;
	private LineupRatingAssistantPanel lineupRatingAssistantPanel;
	private JSplitPane horizontalSplitPane;
	private JSplitPane verticalSplitPane;
	private SpielerUebersichtNamenTable aufstellungSpielerTableName;
	private List<Updatable> updatables = new ArrayList<>();

	public LineupPanel() {
		initComponents();
		addListeners();
	}

	public void setPlayer(int idPlayer) {
		aufstellungSpielerTableName.setSpieler(idPlayer);
		lineupPlayersTable.setSpieler(idPlayer);
	}

	public void refresh() {
		lineupPlayersTable.refresh();
	}

	public final ILineupAssistantPanel getAufstellungsAssistentPanel() {return lineupRatingAssistantPanel.getLineupAssistantPanel(); }

	public final LineupSettingsPanel getLineupSettingsPanel() {return lineupRatingAssistantPanel.getLineupSettingsPanel();}

	public final LineupRatingPanel getLineupRatingPanel(){ return lineupRatingAssistantPanel.getLineupRatingPanel();}

	public final LineupPositionsPanel getLineupPositionsPanel() {
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
		aufstellungSpielerTableName.refresh();

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

		lineupRatingAssistantPanel = new LineupRatingAssistantPanel(this);
		lineupPositionsPanel = new LineupPositionsPanel(this);

		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		horizontalSplitPane.setLeftComponent(new JScrollPane(lineupPositionsPanel));
		horizontalSplitPane.setRightComponent(lineupRatingAssistantPanel);

		verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		verticalSplitPane.setTopComponent(horizontalSplitPane);
		verticalSplitPane.setBottomComponent(initSpielerTabelle());


		UserParameter param = UserParameter.instance();
		verticalSplitPane.setDividerLocation(param.lineupPanel_verticalSplitLocation);
		horizontalSplitPane.setDividerLocation(param.lineupPanel_horizontalSplitLocation);


		add(verticalSplitPane, BorderLayout.CENTER);
	}

	private Component initSpielerTabelle() {
		lineupPlayersTable = new LineupPlayersTable();
		aufstellungSpielerTableName = new SpielerUebersichtNamenTable(lineupPlayersTable.getSorter());

		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		JScrollPane scrollpane = new JScrollPane(aufstellungSpielerTableName);
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
				} else if (e.getSource() == aufstellungSpielerTableName.getSelectionModel()) {
					synchronizeSelection(aufstellungSpielerTableName);
				}
			}

			private void synchronizeSelection(JTable sourceTable) {
				JTable targetTable;
				if (sourceTable == lineupPlayersTable) {
					targetTable = aufstellungSpielerTableName;
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
					Player player = ((PlayerTable) sourceTable).getSpieler(row);
					if (player != null) {
						HOMainFrame.instance().setActualSpieler(player);
					}
				}
			}
		};

		this.lineupPlayersTable.getSelectionModel().addListSelectionListener(lsl);
		this.aufstellungSpielerTableName.getSelectionModel().addListSelectionListener(lsl);
	}
}
