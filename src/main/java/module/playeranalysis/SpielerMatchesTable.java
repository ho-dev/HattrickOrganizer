// %515857825:de.hattrickorganizer.gui.playeranalysis%
package module.playeranalysis;

import core.db.DBManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.comp.table.ToolTipHeader;
import core.gui.comp.table.UserColumn;
import core.gui.model.PlayerAnalysisModel;
import core.gui.model.UserColumnController;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

final class SpielerMatchesTable extends JTable {

	private static final long serialVersionUID = 5959815846371146851L;
	private PlayerAnalysisModel m_clTableModel;
	private TableSorter m_clTableSorter;
	private int m_iSpielerId = -1;
	private int instance;

	/**
	 * Creates a new SpielerMatchesTable object.
	 */
	SpielerMatchesTable(int spielerid, int instance) {
		super();
		this.instance = instance;
		m_iSpielerId = spielerid;

		initModel();
		setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
		setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);
	}

	void saveColumnOrder() {
		final UserColumn[] columns = m_clTableModel.getDisplayedColumns();
		final TableColumnModel tableColumnModel = getColumnModel();
		for (int i = 0; i < columns.length; i++) {
			columns[i].setIndex(convertColumnIndexToView(i));
			columns[i].setPreferredWidth(tableColumnModel.getColumn(convertColumnIndexToView(i))
					.getWidth());
		}
		m_clTableModel.setCurrentValueToColumns(columns);
		DBManager.instance().saveHOColumnModel(m_clTableModel);
	}

	void refresh(int spielerid) {
		m_iSpielerId = spielerid;
		initModel();
		repaint();
	}

	/**
	 * Initialisiert das Model
	 */
	private void initModel() {
		setOpaque(false);

		if (m_clTableModel == null) {
			m_clTableModel = (instance == 1) ? UserColumnController.instance().getAnalysis1Model()
					: UserColumnController.instance().getAnalysis2Model();
			m_clTableModel.setValues(DBManager.instance().getPlayerMatchCBItems(m_iSpielerId));

			m_clTableSorter = new TableSorter(m_clTableModel, -1, -1);

			ToolTipHeader header = new ToolTipHeader(getColumnModel());
			header.setToolTipStrings(m_clTableModel.getTooltips());
			header.setToolTipText("");
			setTableHeader(header);

			setModel(m_clTableSorter);

			final TableColumnModel columModel = getColumnModel();

			for (int i = 0; i < m_clTableModel.getColumnCount(); i++) {
				columModel.getColumn(i).setIdentifier(i);
			}

			int[][] targetColumn = m_clTableModel.getColumnOrder();

			// Reihenfolge -> nach [][1] sortieren
			targetColumn = core.util.Helper.sortintArray(targetColumn, 1);

			if (targetColumn != null) {
				for (int i = 0; i < targetColumn.length; i++) {
					this.moveColumn(
							getColumnModel().getColumnIndex(Integer.valueOf(targetColumn[i][0])),
							targetColumn[i][1]);
				}
			}

			m_clTableSorter.addMouseListenerToHeaderInTable(this);
			m_clTableModel.setColumnsSize(getColumnModel());
		} else {
			// Werte neu setzen
			m_clTableModel.setValues(DBManager.instance().getPlayerMatchCBItems(m_iSpielerId));
			m_clTableSorter.reallocateIndexes();
		}

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(0);
		setRowSelectionAllowed(true);
		m_clTableSorter.initsort();
	}

}
