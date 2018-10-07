package module.matchesanalyzer.ui.table.cell.content;

import core.model.HOVerwaltung;
import module.matchesanalyzer.ui.table.MatchesAnalyzerTable;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCell;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellStyle;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellType;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellValue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;


public class MatchesAnalyzerCellStat extends MatchesAnalyzerCellContent {

	private static final List<String> SKILL_LEVELS = new ArrayList<String>();
	static {
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.non-existent"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.disastrous"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.wretched"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.poor"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.weak"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.inadequate"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.passable"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.solid"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.excellent"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.formidable"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.outstanding"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.brilliant"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.magnificent"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.worldclass"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.supernatural"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.titanic"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.extra-terrestrial"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.mythical"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.magical"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.utopian"));
		SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.divine"));
		for (int i = 1; i <= 10; i++){
			SKILL_LEVELS.add(HOVerwaltung.instance().getLanguageString("ls.player.skill.value.divine") + "(+" + i + ")");
		}
	}

	private static final List<String> SKILL_SUBLEVELS = new ArrayList<String>();
	static {
		SKILL_SUBLEVELS.add(HOVerwaltung.instance().getLanguageString("verylow")); 
		SKILL_SUBLEVELS.add(HOVerwaltung.instance().getLanguageString("low")); 
		SKILL_SUBLEVELS.add(HOVerwaltung.instance().getLanguageString("high")); 
		SKILL_SUBLEVELS.add(HOVerwaltung.instance().getLanguageString("veryhigh"));
	}

	private static final int[] AFTER_COMMA = new int[] {1, 4, 6, 9};
	private static final int BIG_STAT_HEIGHT_DIFFERENCE = 10;
	private static final int SMALL_STAT_HEIGHT_DIFFERENCE = 2;
	private static final double BAR_WIDTH_RATIO = 0.10d;

	private final int barColor;

	public MatchesAnalyzerCellStat(Integer value, double barValue, MatchesAnalyzerCellType type) {
		super(type);

		barColor = (value < 0 ? 0 : 1);

		if(type.getStyle().hasBar()) {
			try {
				int v = Math.abs(value);
				int major = (v + 3) / 4;
				int minor = (v + 3) % 4;
				setCellValue(new MatchesAnalyzerCellValue<Integer>(v, barValue, SKILL_LEVELS.get(major) + " (" + SKILL_SUBLEVELS.get(minor) + ")"));
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			try {
				setCellValue(new MatchesAnalyzerCellValue<Integer>(value, SKILL_LEVELS.get(value)));
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void configure(MatchesAnalyzerCell cell, boolean selected) {
		super.configure(cell, selected);
		MatchesAnalyzerCellStyle style = getCellType().getStyle();

		Color color = null;
		String text = null;
		
		if(style.hasBar()) {
			color = style.getBar()[barColor].brighter().brighter().brighter().brighter();
			int v = (Integer)getCellValue().getValue();
			int level = (v + 3) / 4;
			int sublevel = AFTER_COMMA[(v + 3) % 4];
			int size = MatchesAnalyzerTable.DEFAULT_ROWS_HEIGHT - BIG_STAT_HEIGHT_DIFFERENCE;
			String family = cell.getFont().getFamily();
			text = "<html><font style='font-family:\"" + family + "\"; font-size: " + size + "pt;'>" + level + "</font><font style='font-family:\"" + family + "\"; font-size: " + (size - SMALL_STAT_HEIGHT_DIFFERENCE) + "pt;'>." + sublevel + "</font></html>";
		} else {
			color = style.getBackground();
			text = String.valueOf((Integer)getCellValue().getValue());
		}
		
		cell.setBackground(color);
		cell.setText(text);
	}

	@Override
	public void paint(Graphics g, Dimension size, MatchesAnalyzerCellStyle style, boolean selected) {
		Graphics2D g2d = (Graphics2D)g.create();
		double bar = getCellValue().getBarValue();
		if(bar != MatchesAnalyzerCellValue.NONE) {
			int x = (int)(size.width * (1.0d - BAR_WIDTH_RATIO));
			int y = (int)(size.height * (50.0d - Math.abs(bar - 50.0d)) / 50.0d);
			Color color = style.getBar()[barColor];
			if(selected) color = color.darker();
			g2d.setColor(color);
			g2d.fillRect(x, y, size.width - x, size.height - y);
		}
	}

}
