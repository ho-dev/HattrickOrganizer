package module.matchesanalyzer.ui.table.cell.content;

import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellType;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellValue;


public class MatchesAnalyzerCellText extends MatchesAnalyzerCellContent {

	public MatchesAnalyzerCellText(String value, MatchesAnalyzerCellType type) {
		super(new MatchesAnalyzerCellValue<String>(value, value), type);
	}

}
