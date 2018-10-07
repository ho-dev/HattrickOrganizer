package module.matchesanalyzer.ui.table.cell.content;

import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCell;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellStyle;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellType;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellValue;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.Icon;


public abstract class MatchesAnalyzerCellContent {

	private MatchesAnalyzerCellValue<? extends Object> cellValue;
	private final MatchesAnalyzerCellType cellType;

	public MatchesAnalyzerCellContent(MatchesAnalyzerCellType type) {
		cellValue = null;
		cellType = type;
	}

	public MatchesAnalyzerCellContent(MatchesAnalyzerCellValue<? extends Object> value, MatchesAnalyzerCellType type) {
		cellValue = value;
		cellType = type;
	}

	public MatchesAnalyzerCellValue<? extends Object> getCellValue() {
		return cellValue;
	}

	protected void setCellValue(MatchesAnalyzerCellValue<? extends Object> cellValue) throws IllegalAccessException {
		if(this.cellValue != null) throw new IllegalAccessException("Can't change cell value.");
		this.cellValue = cellValue;
	}

	public MatchesAnalyzerCellType getCellType() {
		return cellType;
	}

	public void configure(MatchesAnalyzerCell cell, boolean selected) {
		MatchesAnalyzerCellStyle style = cellType.getStyle();

		cell.setHorizontalAlignment(style.getHorizontalAlignment());
		cell.setForeground(style.getForeground());
		cell.setBackground(style.getBackground());
		cell.setOpaque(true);

		Font newFont;
		Font font = cell.getFont();
		if(style.isBold()) {
			newFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		} else {
			newFont = font;
		}
		cell.setFont(newFont);

		Object value = cellValue.getValue();
		if(value instanceof Icon) {
			cell.setIcon((Icon)value);
		} else if(value != null) {
			cell.setText(value.toString());
		}

		if(style.hasTooltips()) {
			cell.setToolTipText(cellValue.getTooltips());
		}
	}

	public void paint(Graphics g, Dimension size, MatchesAnalyzerCellStyle style, boolean selected) {}

}
