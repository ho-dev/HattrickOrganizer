// %3267207196:de.hattrickorganizer.gui.matchprediction%
package core.prediction;

import core.gui.comp.table.TableSorter;
import core.prediction.engine.MatchResult;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;


class MatchScoreDiffTable extends JTable {
	
	private static final long serialVersionUID = -6343540652634219571L;

	private MatchScoreDiffTableModel m_clTableModel;
	private TableSorter m_clTableSorter;

	MatchScoreDiffTable(MatchResult mr,boolean isHome) {
		super();
		initModel(mr,isHome);
		setDefaultRenderer(java.lang.Object.class, new core.gui.comp.renderer.HODefaultTableCellRenderer());
		setSelectionBackground(core.gui.comp.renderer.HODefaultTableCellRenderer.SELECTION_BG);
		setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
	}

	public final void refresh(MatchResult mr,boolean isHome) {
		initModel(mr,isHome);

		repaint();
	}

	private void initModel(MatchResult mr,boolean isHome) {
		setOpaque(false);

		if (m_clTableModel == null) {
			m_clTableModel = new MatchScoreDiffTableModel(mr,isHome);
			m_clTableSorter = new TableSorter(m_clTableModel, 1, -1);

			final core.gui.comp.table.ToolTipHeader header = new core.gui.comp.table.ToolTipHeader(getColumnModel());
			header.setToolTipStrings(m_clTableModel.getColumnNames());
			header.setToolTipText("");
			setTableHeader(header);

			setModel(m_clTableSorter);

			final TableColumnModel tableColumnModel = getColumnModel();

			for (int i = 0; i < 2; i++) {
				tableColumnModel.getColumn(i).setIdentifier(new Integer(i));
			}

			m_clTableSorter.addMouseListenerToHeaderInTable(this);
		} else {
			//Werte neu setzen
			m_clTableModel.setValues(mr);
			m_clTableSorter.reallocateIndexes();
		}

		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		final TableColumnModel tableColumnModel = getColumnModel();
		tableColumnModel.getColumn(tableColumnModel.getColumnIndex(Integer.valueOf(0))).setMaxWidth(core.util.Helper.calcCellWidth(100));
		tableColumnModel.getColumn(tableColumnModel.getColumnIndex(Integer.valueOf(0))).setPreferredWidth(core.util.Helper.calcCellWidth(100));
		tableColumnModel.getColumn(tableColumnModel.getColumnIndex(Integer.valueOf(1))).setMaxWidth(core.util.Helper.calcCellWidth(400));
		tableColumnModel.getColumn(tableColumnModel.getColumnIndex(Integer.valueOf(1))).setPreferredWidth(core.util.Helper.calcCellWidth(400));

		setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		setRowSelectionAllowed(true);

		m_clTableSorter.initsort();
	}
}
