package module.evilcard.gui;

import core.model.HOVerwaltung;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

class DetailsTable extends JTable {

	private static final long serialVersionUID = -3276477756368710414L;
	private String[] columnToolTips;
	private TableModelListener tableModelListener;

	DetailsTable() {
		super();

		columnToolTips = new String[DetailsTableModel.cols];
		columnToolTips[DetailsTableModel.COL_MATCH_ID] = HOVerwaltung.instance().getLanguageString(
				"ls.match.id");
		columnToolTips[DetailsTableModel.COL_MATCH_HOME] = HOVerwaltung.instance()
				.getLanguageString("Heim");
		columnToolTips[DetailsTableModel.COL_MATCH_GUEST] = HOVerwaltung.instance()
				.getLanguageString("Gast");
		columnToolTips[DetailsTableModel.COL_MATCH_RESULT] = HOVerwaltung.instance()
				.getLanguageString("ls.match.result");
		columnToolTips[DetailsTableModel.COL_EVENT] = HOVerwaltung.instance().getLanguageString(
				"tooltip.Event");
		columnToolTips[DetailsTableModel.COL_WARNINGS_TYPE1] = HOVerwaltung.instance()
				.getLanguageString("tooltip.WarningType1");
		columnToolTips[DetailsTableModel.COL_WARNINGS_TYPE2] = HOVerwaltung.instance()
				.getLanguageString("tooltip.WarningType2");
		columnToolTips[DetailsTableModel.COL_WARNINGS_TYPE3] = HOVerwaltung.instance()
				.getLanguageString("tooltip.WarningType3");
		columnToolTips[DetailsTableModel.COL_WARNINGS_TYPE4] = HOVerwaltung.instance()
				.getLanguageString("tooltip.WarningType4");
		columnToolTips[DetailsTableModel.COL_DIRECT_RED_CARDS] = HOVerwaltung.instance()
				.getLanguageString("RoteKarten");

		this.tableModelListener = new TableModelListener() {

        @Override
			public void tableChanged(TableModelEvent e) {
				updateRowHeights();
			}
		};
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		String tip = null;
		java.awt.Point p = e.getPoint();
		int rowIndex = rowAtPoint(p);
		int colIndex = columnAtPoint(p);
		int realColumnIndex = convertColumnIndexToModel(colIndex);

		if (realColumnIndex == DetailsTableModel.COL_EVENT) { // HIGHLIGHT

			// column
			tip = adattaLunghezza(getValueAt(rowIndex, colIndex).toString());
		} else { // another column
			tip = super.getToolTipText(e);
		}

		return tip;
	}

	@Override
	public void setModel(TableModel dataModel) {
		if (getModel() != null) {
			getModel().removeTableModelListener(this.tableModelListener);
		}
		dataModel.addTableModelListener(this.tableModelListener);
		super.setModel(dataModel);
	}

	private void updateRowHeights() {

		int maxHeight = 1;

		for (int row = this.getRowCount() - 1; row >= 0; row--) {
			TableCellRenderer renderer = this.getCellRenderer(row, DetailsTableModel.COL_EVENT);
			JEditorPane comp = (JEditorPane) this.prepareRenderer(renderer, row,
					DetailsTableModel.COL_EVENT);
			Dimension dim = comp.getPreferredSize();
			int rowCount = ((int) dim.getWidth() / this.getColumnModel()
					.getColumn(DetailsTableModel.COL_EVENT).getWidth()) + 1;
			int rowHeight = (rowCount * (int) dim.getHeight()) + (this.getRowMargin() * 2);
			maxHeight = Math.max(maxHeight, rowHeight);
		}

		this.setRowHeight(maxHeight);

	}

	// Implement table header tool tips.
	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(columnModel) {

			private static final long serialVersionUID = -5010053157479571641L;

			@Override
			public String getToolTipText(MouseEvent e) {
				// String tip = null;
				java.awt.Point p = e.getPoint();
				int index = columnModel.getColumnIndexAtX(p.x);
				int realIndex = columnModel.getColumn(index).getModelIndex();
				return columnToolTips[realIndex];
			}
		};
	}

	private String adattaLunghezza(String testo) {
		StringBuilder risultato = new StringBuilder(50);

		for (int i = 0, troncamento = 0; i < (testo.length() - 1); i++, troncamento++) {
			risultato.append(testo.charAt(i));

			if (troncamento >= 40) {
				// andare a capo se c'è uno spazio dopo
				if (testo.charAt(i + 1) == ' ') {
					risultato.append("<br>");
					troncamento = 0;
				}
			}
		}

		return risultato.toString();
	}
}
