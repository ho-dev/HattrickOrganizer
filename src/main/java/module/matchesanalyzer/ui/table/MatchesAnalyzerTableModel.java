package module.matchesanalyzer.ui.table;

import module.matchesanalyzer.data.MatchesAnalyzerMatch;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellType;
import module.matchesanalyzer.ui.table.cell.content.MatchesAnalyzerCellContent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;


public class MatchesAnalyzerTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private List<MatchesAnalyzerMatch> rows = new ArrayList<MatchesAnalyzerMatch>();

	public List<MatchesAnalyzerMatch> getRows() {
		return rows;
	}

	public void updateTable(List<MatchesAnalyzerMatch> rows) {
		this.rows = rows;
	}

	public void clearTable() {
		this.rows = new ArrayList<MatchesAnalyzerMatch>();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return MatchesAnalyzerCellContent.class;
	}

	@Override
	public int getColumnCount() {
		return MatchesAnalyzerCellType.values().length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return MatchesAnalyzerCellType.values()[columnIndex].getHeader();
	}
	
	public String getColumnTitle(int columnIndex) {
		return MatchesAnalyzerCellType.values()[columnIndex].getTooltip();
	}

	@Override
	public int getRowCount() {
		if(rows == null) return 0;
		return rows.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(rows == null) return null;
		return MatchesAnalyzerCellType.valueOf(rows.get(rowIndex), columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

}