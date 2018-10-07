package module.matchesanalyzer.ui.table.cell;

import java.awt.Color;


public class MatchesAnalyzerCellStyle {
	private final int width;
	private final Color foreground;
	private final Color background;
	private final Color[] bar;
	private final boolean tooltips;
	private final boolean bold;
	private final boolean italic;
	private final int horizontalAlignment;

	public MatchesAnalyzerCellStyle(int width, Color foreground, Color background, Color[] bar, boolean tooltips, boolean bold, boolean italic, int horizontalAlignment) {
		this.width = width;
		this.foreground = foreground;
		this.background = background;
		this.bar = bar;
		this.tooltips = tooltips;
		this.bold = bold;
		this.italic = italic;
		this.horizontalAlignment = horizontalAlignment;
	}

	public int getWidth() {
		return width;
	}

	public Color getForeground() {
		return foreground;
	}

	public Color getBackground() {
		return background;
	}

	public boolean hasBar() {
		return(bar != null);
	}

	public boolean hasTooltips() {
		return tooltips;
	}

	public Color[] getBar() {
		return bar;
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public int getHorizontalAlignment() {
		return horizontalAlignment;
	}

}
