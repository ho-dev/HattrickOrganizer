package module.lineup;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.renderer.BooleanTableCellRenderer;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;
import core.gui.model.UserColumnFactory;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.player.Player;
import core.net.HattrickLink;
import module.playerOverview.PlayerTable;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Table displaying the players' details in Lineup tab.
 * which is the same table class used in the Squad tab
 */
public final class LineupPlayersTable extends FixedColumnsTable implements core.gui.Refreshable, PlayerTable {

	private final LineupTableModel tableModel;

	LineupPlayersTable() {
		super(UserColumnController.instance().getLineupModel(), 1);
		tableModel = (LineupTableModel) this.getScrollTable().getModel();
		tableModel.setValues(HOVerwaltung.instance().getModel().getCurrentPlayers());
		tableModel.initTable(this);
		setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
		setDefaultRenderer(Boolean.class, new BooleanTableCellRenderer());
		RefreshManager.instance().registerRefreshable(this);
		initListeners();
	}

	@Override
	public void setPlayer(int iPlayerID) {
		var rowIndex = tableModel.getRowIndexOfPlayer(iPlayerID);
		if (rowIndex >= 0) {
			this.setRowSelectionInterval(rowIndex, rowIndex);
		}
	}

	@Override
	public @Nullable Player getPlayer(int row) {
		return tableModel.getPlayerAtRow(row);
	}

	@Override
	public void reInit() {
		resetPlayers();
		repaint();
	}

	@Override
	public void refresh() {
		resetPlayers();
		repaint();
	}

	public LineupTableModel getTableModel() {
		return this.tableModel;
	}

	public void saveColumnOrder() {
		tableModel.storeUserSettings();
	}

	private void resetPlayers() {
		tableModel.setValues(HOVerwaltung.instance().getModel().getCurrentPlayers());
	}

	private void initListeners() {

//		this.tableSorter.addTableModelListener(e -> {
//			var r = e.getFirstRow();
//			var c = e.getColumn();
//			var player = tableSorter.getPlayerAtRow(r);
//			if (player != null) {
//				if (c == tableModel.getPositionInArray(UserColumnFactory.AUTO_LINEUP)) {
//					var autoLineup = tableSorter.getValueAt(r,c);
//					if (autoLineup != null){
//						player.setCanBeSelectedByAssistant((boolean) autoLineup);
//						if( player.getCanBeSelectedByAssistant()){
//							// this player has been made selectable from the Lineup tab, for consistency we set its position to undefined
//							player.setUserPosFlag(IMatchRoleID.UNKNOWN);
//						}
//						else {
//							player.setUserPosFlag(IMatchRoleID.UNSELECTABLE);
//						}
//						HOMainFrame.instance().getSpielerUebersichtPanel().update();
//					}
//				}
//			}
//        });

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int rowindex = getSelectedRow();
				if (rowindex >= 0) {
					// Last match column
//					int viewColumn = columnAtPoint(e.getPoint());
//					int column = columnModel.getColumn(viewColumn).getModelIndex();
					Player selectedPlayer = tableModel.getPlayerAtRow(rowindex);
					if (selectedPlayer != null) {
						var scrollTable = getScrollTable();
						var viewColumn = scrollTable.columnAtPoint(e.getPoint());
						if (viewColumn > -1) {
							var column = scrollTable.getColumn(viewColumn).getModelIndex();
							if (column + getFixedColumnsCount() == tableModel.getPositionInArray(UserColumnFactory.LAST_MATCH_RATING)) {
								if (e.isShiftDown()) {
									int matchId = selectedPlayer.getLastMatchId();
									// TODO get the match type of last match from player. For the moment we hope, that going with no type will work
									MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(matchId, null);
									HattrickLink.showMatch(matchId + "", info.getMatchType().isOfficial());
								} else if (e.getClickCount() == 2) {
									HOMainFrame.instance().showMatch(selectedPlayer.getLastMatchId());
								}
							}
						}
					}
				}
			}
		});
	}
}