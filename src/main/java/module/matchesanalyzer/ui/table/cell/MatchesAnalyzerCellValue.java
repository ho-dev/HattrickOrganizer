package module.matchesanalyzer.ui.table.cell;

public class MatchesAnalyzerCellValue<T extends Object> {

	public static final int NONE = -1;
	public static final int VERYLOW = 0;
	public static final int LOW = 1;
	public static final int HIGH = 2;
	public static final int VERYHIGH = 3;

	private final T value;
	private final double barValue;
	private final String tooltips;

	public MatchesAnalyzerCellValue() {
		this.value = null;
		this.barValue = NONE;
		this.tooltips = null;
	}

	public MatchesAnalyzerCellValue(T value) {
		this.value = value;
		this.barValue = NONE;
		this.tooltips = null;
	}

	public MatchesAnalyzerCellValue(T value, String tooltips) {
		this.value = value;
		this.barValue = NONE;
		this.tooltips = tooltips;
	}

	public MatchesAnalyzerCellValue(T value, double barValue) {
		this.value = value;
		this.barValue = barValue;
		this.tooltips = null;
	}

	public MatchesAnalyzerCellValue(T value, double barValue, String tooltips) {
		this.value = value;
		this.barValue = barValue;
		this.tooltips = tooltips;
	}

	public T getValue() {
		return value;
	}

	public double getBarValue() {
		return barValue;
	}

	public String getTooltips() {
		return tooltips;
	}

}
