package module.matchesanalyzer.ui.table.cell;

import module.matchesanalyzer.ui.table.cell.content.MatchesAnalyzerCellContent;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class MatchesAnalyzerCellRenderer implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if(value != null && !(value instanceof MatchesAnalyzerCellContent)) { return null; }

		MatchesAnalyzerCell label = new MatchesAnalyzerCell((MatchesAnalyzerCellContent)value, table.getSelectedRow() == row);
		return label;
	}

}