package module.matchesanalyzer.ui.table.cell.content;

import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellType;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellValue;

import javax.swing.Icon;


public class MatchesAnalyzerCellIcon extends MatchesAnalyzerCellContent {

	public MatchesAnalyzerCellIcon(Icon icon, String tooltip, MatchesAnalyzerCellType type) {
		super(new MatchesAnalyzerCellValue<Icon>(icon, tooltip), type);
	}

}
