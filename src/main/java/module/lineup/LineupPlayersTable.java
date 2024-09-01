package module.lineup;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.renderer.BooleanTableCellRenderer;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.TableSorter;
import core.gui.comp.table.ToolTipHeader;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.gui.model.UserColumnFactory;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.MatchKurzInfo;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.net.HattrickLink;
import core.util.Helper;
import module.playerOverview.LineupPlayersTableNameColumn;
import module.playerOverview.PlayerTable;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 * Table displaying the players' details in Lineup tab.
 * The name of the players is displayed in {@link LineupPlayersTableNameColumn},
 * which is the same table class used in the Squad tab
 */
public final class LineupPlayersTable extends JTable implements core.gui.Refreshable, PlayerTable {

	private LineupTableModel tableModel;
//	private TableSorter tableSorter;

	LineupPlayersTable() {
		super();
		initModel();
		setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
		setDefaultRenderer(Boolean.class, new BooleanTableCellRenderer());
		setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);
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
		initModel();
		repaint();
	}

	public void reInitModel() {
		tableModel.reInitData();
	}

	@Override
	public void refresh() {
		reInitModel();
		repaint();
	}

	/**
	 *Returns the column for sorting
	 */
    private int getSortSpalte() {
		return switch (UserParameter.instance().standardsortierung) {
			case UserParameter.SORT_NAME -> tableModel.getPositionInArray(UserColumnFactory.NAME);
			case UserParameter.SORT_AUFGESTELLT -> tableModel.getPositionInArray(UserColumnFactory.LINEUP);
			case UserParameter.SORT_GRUPPE -> tableModel.getPositionInArray(UserColumnFactory.GROUP);
			case UserParameter.SORT_BEWERTUNG -> tableModel.getPositionInArray(UserColumnFactory.RATING);
			default -> tableModel.getPositionInArray(UserColumnFactory.BEST_POSITION);
		};
	}

//	TableSorter getSorter() {
//		return tableSorter;
//	}

	public LineupTableModel getTableModel() {return this.tableModel;}

	public void saveColumnOrder() {
		final UserColumn[] columns = tableModel.getDisplayedColumns();
		final TableColumnModel tableColumnModel = getColumnModel();
		for (int i = 0; i < columns.length; i++) {
			columns[i].setIndex(convertColumnIndexToView(i));
			columns[i].setPreferredWidth(tableColumnModel.getColumn(convertColumnIndexToView(i))
					.getWidth());
		}
		tableModel.setCurrentValueToColumns(columns);
		DBManager.instance().saveHOColumnModel(tableModel);
	}

	private void initModel() {
		setOpaque(false);

		if (tableModel == null) {
			tableModel = UserColumnController.instance().getLineupModel();

			tableModel.setValues(HOVerwaltung.instance().getModel().getCurrentPlayers());
//			tableSorter = new TableSorter(tableModel,
//					tableModel.getPositionInArray(UserColumnFactory.ID),
//					getSortSpalte(),
//					tableModel.getPositionInArray(UserColumnFactory.NAME));

			tableModel.initTable(this);
		} else {
			// Reset values
			tableModel.setValues(HOVerwaltung.instance().getModel().getCurrentPlayers());
//			tableSorter.reallocateIndexes();
		}

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(0);
		setRowSelectionAllowed(true);
//		tableSorter.initsort();
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
				if (rowindex >= 0){
					// Last match column
					int viewColumn = columnAtPoint(e.getPoint());
					int column = columnModel.getColumn(viewColumn).getModelIndex();
					Player selectedPlayer = tableModel.getPlayerAtRow(rowindex);
					if(selectedPlayer != null){
						if ( column == tableModel.getPositionInArray(UserColumnFactory.LAST_MATCH_RATING)){
							if(e.isShiftDown()){
								int matchId = selectedPlayer.getLastMatchId();
								// TODO get the match type of last match from player. For the moment we hope, that going with no type will work
								MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(matchId, null);
								HattrickLink.showMatch(matchId + "", info.getMatchType().isOfficial());
							}else if(e.getClickCount()==2) {
								HOMainFrame.instance().showMatch(selectedPlayer.getLastMatchId());
							}
						}
					}
				}
			}
		});
	}

	public TableRowSorter<HOTableModel> getTableSorter() {
		return tableModel.getRowSorter();
	}
}
