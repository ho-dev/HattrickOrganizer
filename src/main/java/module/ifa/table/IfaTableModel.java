package module.ifa.table;

import core.model.HOVerwaltung;
import module.ifa.PluginIfaUtils;
import module.ifa.model.IfaModel;
import module.ifa.model.IfaStatistic;
import module.ifa.model.ModelChangeListener;
import module.ifa.model.Summary;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

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
		if (list.size() > 0) {
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
				return new Date(stat.getLastMatchDate());
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
				return new Date(this.summary.getLastMatch());
			case COL_COOLNESS:
				return this.summary.getCoolnessTotal();
			}
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case COL_PLAYED:
			return Integer.class;
		case COL_WON:
			return Integer.class;
		case COL_DRAW:
			return Integer.class;
		case COL_LOST:
			return Integer.class;
		case COL_LASTMATCH:
			return Date.class;
		case COL_COOLNESS:
			return Double.class;
		}
		return super.getColumnClass(columnIndex);
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case COL_COUNTRY:
			return HOVerwaltung.instance().getLanguageString("ifa.statisticsTable.col.country");
		case COL_PLAYED:
			return HOVerwaltung.instance().getLanguageString("ifa.statisticsTable.col.played");
		case COL_WON:
			return HOVerwaltung.instance().getLanguageString("Gewonnen");
		case COL_DRAW:
			return HOVerwaltung.instance().getLanguageString("Unendschieden");
		case COL_LOST:
			return HOVerwaltung.instance().getLanguageString("Verloren");
		case COL_LASTMATCH:
			return HOVerwaltung.instance().getLanguageString("ifa.statisticsTable.col.lastMatch");
		case COL_COOLNESS:
			return HOVerwaltung.instance().getLanguageString("ifa.statisticsTable.col.coolness");
		}

		return null;
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
