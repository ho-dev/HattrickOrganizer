package module.ifa.table;

import core.model.TranslationFacility;
import module.ifa.PluginIfaUtils;
import module.ifa.model.IfaModel;
import module.ifa.model.IfaStatistic;
import module.ifa.model.ModelChangeListener;
import module.ifa.model.Summary;

import javax.swing.table.AbstractTableModel;
import java.util.Date;
import java.util.List;

public class IfaTableModel extends AbstractTableModel implements ModelChangeListener {

	static final int COL_COUNTRY = 0;
	static final int COL_PLAYED = 1;
	static final int COL_WON = 2;
	static final int COL_DRAW = 3;
	static final int COL_LOST = 4;
	static final int COL_LASTMATCH = 5;
	static final int COL_COOLNESS = 6;
	private static final long serialVersionUID = -5838533232544239799L;
	private IfaModel model;
	private List<IfaStatistic> list;
	private Summary summary;
	private boolean visited;


	public void setData(IfaModel model, boolean visited) {
		if (this.model != null) {
			this.model.removeModelChangeListener(this);
		}
		this.model = model;
		this.visited = visited;

		this.model.addModelChangeListener(this);
		refresh();
	}

	@Override
	public int getRowCount() {
		if (!list.isEmpty()) {
			return this.list.size() + 1;
		}
		return 0;
	}

	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		if (rowIndex < this.list.size()) {
			IfaStatistic stat = this.list.get(rowIndex);
			switch (columnIndex) {
			case COL_COUNTRY:
				return stat.getCountry();
			case COL_PLAYED:
				return stat.getMatchesPlayed();
			case COL_WON:
				return stat.getMatchesWon();
			case COL_DRAW:
				return stat.getMatchesDraw();
			case COL_LOST:
				return stat.getMatchesLost();
			case COL_LASTMATCH:
				return stat.getLastMatchDate();
			case COL_COOLNESS:
				return PluginIfaUtils.getCoolness(stat.getCountry().getCountryId());
			}
		} else {
			switch (columnIndex) {
			case COL_COUNTRY:
				return this.summary.getCountriesTotal();
			case COL_PLAYED:
				return this.summary.getPlayedTotal();
			case COL_WON:
				return this.summary.getWonTotal();
			case COL_DRAW:
				return this.summary.getDrawTotal();
			case COL_LOST:
				return this.summary.getLostTotal();
			case COL_LASTMATCH:
				return this.summary.getLastMatchDate();
			case COL_COOLNESS:
				return this.summary.getCoolnessTotal();
			}
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return switch (columnIndex) {
			case COL_PLAYED -> Integer.class;
			case COL_WON -> Integer.class;
			case COL_DRAW -> Integer.class;
			case COL_LOST -> Integer.class;
			case COL_LASTMATCH -> Date.class;
			case COL_COOLNESS -> Double.class;
			default -> super.getColumnClass(columnIndex);
		};
	}

	@Override
	public String getColumnName(int columnIndex) {
		return switch (columnIndex) {
			case COL_COUNTRY -> TranslationFacility.tr("ifa.statisticsTable.col.country");
			case COL_PLAYED -> TranslationFacility.tr("ifa.statisticsTable.col.played");
			case COL_WON -> TranslationFacility.tr("Gewonnen");
			case COL_DRAW -> TranslationFacility.tr("Unendschieden");
			case COL_LOST -> TranslationFacility.tr("Verloren");
			case COL_LASTMATCH -> TranslationFacility.tr("ifa.statisticsTable.col.lastMatch");
			case COL_COOLNESS -> TranslationFacility.tr("ifa.statisticsTable.col.coolness");
			default -> null;
		};

	}

	@Override
	public void modelChanged() {
		refresh();
	}

	private void refresh() {
		if (this.visited) {
			this.list = this.model.getVisitedStatistic();
			this.summary = this.model.getVisitedSummary();
		} else {
			this.list = this.model.getHostedStatistic();
			this.summary = this.model.getHostedSummary();
		}
		fireTableDataChanged();
	}
}
