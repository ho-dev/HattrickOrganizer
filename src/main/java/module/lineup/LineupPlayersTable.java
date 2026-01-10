package module.lineup;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.renderer.BooleanTableCellRenderer;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.PlayerOverviewTableModel;
import core.gui.model.UserColumnController;
import core.gui.model.UserColumnFactory;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.net.HattrickLink;
import module.playeroverview.PlayerTable;
import org.jetbrains.annotations.Nullable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Table displaying the players' details in Lineup tab.
 * which is the same table class used in the Squad tab
 */
public final class LineupPlayersTable extends FixedColumnsTable implements core.gui.Refreshable, PlayerTable {

	private final PlayerOverviewTableModel tableModel;

	LineupPlayersTable() {
		super(UserColumnController.instance().getLineupModel());
		tableModel = (PlayerOverviewTableModel) this.getModel();
		tableModel.setValues(HOVerwaltung.instance().getModel().getCurrentPlayers());
		setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
		setDefaultRenderer(Boolean.class, new BooleanTableCellRenderer());
		RefreshManager.instance().registerRefreshable(this);
		initListeners();
	}

	@Override
	public void setPlayer(int iPlayerID) {
		tableModel.selectPlayer(iPlayerID);
	}

	@Override
	public @Nullable Player getPlayer(int row) {
		return tableModel.getPlayerAtRow(row);
	}

	@Override
	public void reInit() {
		var selectedPlayer = tableModel.getSelectedPlayer();
		resetPlayers();
		repaint();
		if (selectedPlayer != null) {
			tableModel.selectPlayer(selectedPlayer.getPlayerId());
		}
	}

	@Override
	public void refresh() {
		reInit();
	}

	public PlayerOverviewTableModel getTableModel() {
		return this.tableModel;
	}

	public void saveColumnOrder() {
		tableModel.storeUserSettings();
	}

	private void resetPlayers() {
		tableModel.setValues(HOVerwaltung.instance().getModel().getCurrentPlayers());
	}

	private void initListeners() {
		this.getTableModel().addTableModelListener(e -> {
			var r = e.getFirstRow();
			var c = e.getColumn();
			var player = tableModel.getPlayerAtRow(r);
			if (player != null) {
				var userColumn = this.getUserColumn(e);
				if (userColumn != null && userColumn.getId() == UserColumnFactory.AUTO_LINEUP) {
					var autoLineup = tableModel.getValueAt(convertRowIndexToModel(r),convertColumnIndexToModel(c));
					if (autoLineup != null){
						player.setCanBeSelectedByAssistant((boolean) autoLineup);
						if( player.getCanBeSelectedByAssistant()){
							// this player has been made selectable from the Lineup tab, for consistency we set its position to undefined
							player.setUserPosFlag(IMatchRoleID.UNKNOWN);
						}
						else {
							player.setUserPosFlag(IMatchRoleID.UNSELECTABLE);
						}
						HOMainFrame.instance().getSpielerUebersichtPanel().update();
					}
				}
			}
        });

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int rowindex = getSelectedRow();
				if (rowindex >= 0) {
					// Last match column
					Player selectedPlayer = tableModel.getPlayerAtRow(rowindex);
					if (selectedPlayer != null) {
						var viewColumn = columnAtPoint(e.getPoint());
						if (viewColumn > -1) {
							var column = getColumnModel().getColumn(viewColumn);
							if ((Integer)column.getIdentifier() == UserColumnFactory.LAST_MATCH_RATING) {
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