package module.matchesanalyzer.ui.table.cell.content;

import module.matchesanalyzer.data.MatchesAnalyzerLineup;
import module.matchesanalyzer.data.MatchesAnalyzerPlayer;
import module.matchesanalyzer.ui.table.MatchesAnalyzerTable;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellStyle;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellType;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellValue;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;


public class MatchesAnalyzerCellLineup extends MatchesAnalyzerCellContent {

	private static final int X_MARGIN = MatchesAnalyzerCellType.LINEUP.getStyle().getWidth() / 10;
	private static final int Y_MARGIN = MatchesAnalyzerTable.DEFAULT_ROWS_HEIGHT / 30;
	private static final int X_ZONES = MatchesAnalyzerPlayer.Position.FORWARD.getxOffset() - 1;
	private static final int Y_ZONES = (MatchesAnalyzerPlayer.Position.WINGER.getyFactor() + 1) * 2;

	public MatchesAnalyzerCellLineup(MatchesAnalyzerLineup lineup, MatchesAnalyzerCellType type) {
		super(new MatchesAnalyzerCellValue<MatchesAnalyzerLineup>(lineup, lineup.getDefenders() + "-" + lineup.getMidfields() + "-" + lineup.getForwards()), type);
	}

	@Override
	public void paint(Graphics g, Dimension size, MatchesAnalyzerCellStyle style, boolean selected) {
		MatchesAnalyzerLineup lineup = (MatchesAnalyzerLineup)getCellValue().getValue();

		int xFactor = (size.width - X_MARGIN) / X_ZONES;
		int xExtra = (size.width - X_MARGIN) % X_ZONES + X_MARGIN;

		int yFactor = (size.height - Y_MARGIN) / Y_ZONES;
		int yExtra = (size.height - Y_MARGIN) % Y_ZONES + Y_MARGIN;

		Iterator<MatchesAnalyzerPlayer> it = lineup.iterator();
		while(it.hasNext()) {
			Point point = it.next().getPoint();
			if(point == null || point.x == 0) continue;
			point.x = ((point.x - 2) * xFactor + xExtra / 2) - 1;
			point.y = (point.y * yFactor + yExtra / 2) - 1;
			g.setColor(style.getForeground());
			g.drawRect(point.x, point.y, 1, 1);
		}
	}

}
