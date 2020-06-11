package module.lineup;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.SpielerLabelEntry;
import core.gui.comp.renderer.BooleanTableCellRenderer;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.comp.table.ToolTipHeader;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.gui.model.UserColumnFactory;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.util.Helper;
import module.playerOverview.PlayerTable;

import java.awt.event.MouseAdapter;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

/**
 * Table displaying the players' details in Lineup tab.
 *
 * <p>The name of the players is displayed in {@link module.playerOverview.SpielerUebersichtNamenTable},
 * which is the same table class used in the Squad tab.</p>
 */
public final class AustellungSpielerTable extends JTable implements core.gui.Refreshable, PlayerTable {

	private static final long serialVersionUID = -8295456454328467793L;

	private LineupTableModel tableModel;
	private TableSorter tableSorter;

	protected AustellungSpielerTable() {
		super();

		initModel();
		addListeners();
		setDefaultRenderer(Boolean.class, new BooleanTableCellRenderer());
		setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
		setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);
		setBackground(ColorLabelEntry.BG_STANDARD);
		RefreshManager.instance().registerRefreshable(this);
	}

	@Override
	public void setSpieler(int spielerid) {
		int index = tableSorter.getRow4Spieler(spielerid);
		if (index >= 0) {
			this.setRowSelectionInterval(index, index);
		}
	}

	@Override
	public Player getSpieler(int row) {
		return this.tableSorter.getSpieler(row);
	}

	@Override
	public void reInit() {
		initModel();
		repaint();
	}

	public void reInitModel() {
		((LineupTableModel) (this.getSorter()).getModel()).reInitData();
	}

	@Override
	public void refresh() {
		reInitModel();
		repaint();
	}

	/**
	 * Return width of BestPos column
	 */
	protected int getBestPosWidth() {
		return getColumnModel().getColumn(getColumnModel().getColumnIndex(3))
				.getWidth();
	}

	/**
	 *Returns the column for sorting
	 */
	protected int getSortSpalte() {
		switch (core.model.UserParameter.instance().standardsortierung) {
		case UserParameter.SORT_NAME:
			return tableModel.getPositionInArray(UserColumnFactory.NAME);

		case UserParameter.SORT_AUFGESTELLT:
			return tableModel.getPositionInArray(UserColumnFactory.LINUP);

		case UserParameter.SORT_GRUPPE:
			return tableModel.getPositionInArray(UserColumnFactory.GROUP);

		case UserParameter.SORT_BEWERTUNG:
			return tableModel.getPositionInArray(UserColumnFactory.RATING);

		default:
			return tableModel.getPositionInArray(UserColumnFactory.BEST_POSITION);

		}
	}

	protected TableSorter getSorter() {
		return tableSorter;
	}


	public final void saveColumnOrder() {
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
			tableSorter = new TableSorter(tableModel,
					tableModel.getPositionInArray(UserColumnFactory.ID),
					getSortSpalte(),
					tableModel.getPositionInArray(UserColumnFactory.NAME));

			ToolTipHeader header = new ToolTipHeader(getColumnModel());
			header.setToolTipStrings(tableModel.getTooltips());
			header.setToolTipText("");
			setTableHeader(header);
			setModel(tableSorter);

			final TableColumnModel columnModel = getColumnModel();

			for (int i = 0; i < tableModel.getColumnCount(); i++) {
				columnModel.getColumn(i).setIdentifier(i);
			}

			int[][] targetColumn = tableModel.getColumnOrder();
			targetColumn = Helper.sortintArray(targetColumn, 1);

			if (targetColumn != null) {
				for (int i = 0; i < targetColumn.length; i++) {
					this.moveColumn(
							getColumnModel().getColumnIndex(Integer.valueOf(targetColumn[i][0])),
							targetColumn[i][1]);
				}
			}

			tableSorter.addMouseListenerToHeaderInTable(this);
			tableModel.setColumnsSize(getColumnModel());
		} else {
			// Reset values
			tableModel.setValues(HOVerwaltung.instance().getModel().getCurrentPlayers());
			tableSorter.reallocateIndexes();
		}

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(0);
		setRowSelectionAllowed(true);
		tableSorter.initsort();
	}

	private void addListeners() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
				int selectedRow = getSelectedRow();
				boolean bSelected;
				if (selectedRow > -1) {
					Player selectedPlayer = ((SpielerLabelEntry) tableSorter.getValueAt(selectedRow,tableModel.getColumnIndexOfDisplayedColumn(UserColumnFactory.NAME))).getSpieler();
					bSelected = (boolean)tableSorter.getValueAt(selectedRow, tableModel.getColumnIndexOfDisplayedColumn(UserColumnFactory.AUTO_LINEUP));
					selectedPlayer.setCanBeSelectedByAssistant(bSelected);
					if(bSelected)
					{
						// this player has been made selectable from the Lineup tab, for consistency we set its position to undefined
						selectedPlayer.setUserPosFlag(IMatchRoleID.UNKNOWN);
					}
					else {
						selectedPlayer.setUserPosFlag(IMatchRoleID.UNSELECTABLE);
					}
					HOMainFrame.instance().getSpielerUebersichtPanel().update();
				}
			}
		});
	}

}
