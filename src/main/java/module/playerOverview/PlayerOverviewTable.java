package module.playerOverview;

import core.db.DBManager;
import core.gui.RefreshManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.comp.table.ToolTipHeader;
import core.gui.comp.table.UserColumn;
import core.gui.model.PlayerOverviewModel;
import core.gui.model.UserColumnController;
import core.gui.model.UserColumnFactory;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.Helper;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;


/**
 * The main player table.
 * 
 * @author Thorsten Dietz
 */
public class PlayerOverviewTable extends JTable implements core.gui.Refreshable {

	private static final long serialVersionUID = -6074136156090331418L;
	private PlayerOverviewModel tableModel;
	private TableSorter tableSorter;

	public PlayerOverviewTable() {
		super();
		initModel();
		setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
		setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);
		RefreshManager.instance().registerRefreshable(this);
	}

	/**
	 * Breite der BestPosSpalte zurückgeben
	 */
	public final int getBestPosWidth() {
		return getColumnModel().getColumn(
				getColumnModel().getColumnIndex(Integer.valueOf(tableModel.getPositionInArray(UserColumnFactory.BEST_POSITION))))
				.getWidth();
	}

	public final TableSorter getSorter() {
		return tableSorter;
	}

	/**
	 * @return int[spaltenanzahl][2] mit 0=ModelIndex und 1=ViewIndex
	 */
	public final int[][] getSpaltenreihenfolge() {
		final int[][] reihenfolge = new int[tableModel.getColumnCount()][2];

		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			// Modelindex
			reihenfolge[i][0] = i;
			// ViewIndex
			reihenfolge[i][1] = convertColumnIndexToView(i);
		}
		return reihenfolge;
	}

	public final void saveColumnOrder() {
		UserColumn[] columns = tableModel.getDisplayedColumns();
		TableColumnModel tableColumnModel = getColumnModel();
		for (int i = 0; i < columns.length; i++) {
			columns[i].setIndex(convertColumnIndexToView(i));
			columns[i].setPreferredWidth(tableColumnModel.getColumn(convertColumnIndexToView(i)).getWidth());
		}
		tableModel.setCurrentValueToColumns(columns);
		DBManager.instance().saveHOColumnModel(tableModel);
	}

	public final void setSpieler(int spielerid) {
		final int index = tableSorter.getRow4Spieler(spielerid);

		if (index >= 0) {
			this.setRowSelectionInterval(index, index);
		}
	}

	@Override
	public final void reInit() {
		initModel();
		repaint();
	}

	public final void reInitModel() {
		((PlayerOverviewModel) getSorter().getModel()).reInitData();
	}

	public final void reInitModelHRFVergleich() {
		((PlayerOverviewModel) getSorter().getModel()).reInitDataHRFVergleich();
	}

	@Override
	public final void refresh() {
		reInitModel();
		repaint();
	}

	public final void refreshHRFVergleich() {
		reInitModelHRFVergleich();
		repaint();
	}

	/**
	 * Gibt die Spalte für die Sortierung zurück
	 */
	private int getSortSpalte() {
		switch (UserParameter.instance().standardsortierung) {
		case UserParameter.SORT_NAME:
			return tableModel.getPositionInArray(UserColumnFactory.NAME);

		case UserParameter.SORT_BESTPOS:
			return tableModel.getPositionInArray(UserColumnFactory.BEST_POSITION);

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

	/**
	 * Initialisiert das Model
	 */
	private void initModel() {
		setOpaque(false);

		if (tableModel == null) {
			tableModel = UserColumnController.instance().getPlayerOverviewModel();
			tableModel.setValues(HOVerwaltung.instance().getModel().getAllSpieler());
			tableSorter = new TableSorter(tableModel, tableModel.getDisplayedColumns().length - 1, getSortSpalte());

			ToolTipHeader header = new ToolTipHeader(getColumnModel());
			header.setToolTipStrings(tableModel.getTooltips());
			header.setToolTipText("");
			setTableHeader(header);

			setModel(tableSorter);

			TableColumnModel tableColumnModel = getColumnModel();
			for (int i = 0; i < tableModel.getColumnCount(); i++) {
				tableColumnModel.getColumn(i).setIdentifier(new Integer(i));
			}

			int[][] targetColumn = tableModel.getColumnOrder();

			// Reihenfolge -> nach [][1] sortieren
			targetColumn = Helper.sortintArray(targetColumn, 1);

			if (targetColumn != null) {
				for (int i = 0; i < targetColumn.length; i++) {
					this.moveColumn(getColumnModel().getColumnIndex(Integer.valueOf(targetColumn[i][0])), targetColumn[i][1]);
				}
			}

			tableSorter.addMouseListenerToHeaderInTable(this);
			tableModel.setColumnsSize(getColumnModel());
		} else {
			// Werte neu setzen
			tableModel.setValues(HOVerwaltung.instance().getModel().getAllSpieler());
			tableSorter.reallocateIndexes();
		}

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(0);
		setRowSelectionAllowed(true);
		tableSorter.initsort();
	}
}
