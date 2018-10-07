package module.specialEvents;

import core.model.HOVerwaltung;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class SpecialEventsTableModel extends AbstractTableModel {

	static final int MATCHDATECOLUMN = 0;
	static final int MATCHIDCOLUMN = 1;
	static final int MATCHTYPECOLUMN = 2;
	static final int HOMETACTICCOLUMN = 3;
	static final int HOMEEVENTCOLUMN = 4;
	static final int HOMETEAMCOLUMN = 5;
	static final int RESULTCOLUMN = 6;
	static final int AWAYTEAMCOLUMN = 7;
	static final int AWAYEVENTCOLUMN = 8;
	static final int AWAYTACTICCOLUMN = 9;
	static final int MINUTECOLUMN = 10;
	static final int CHANCECOLUMN = 11;
	static final int EVENTTYPCOLUMN = 12;
	static final int SETEXTCOLUMN = 13;
	static final int NAMECOLUMN = 14;
	private static final long serialVersionUID = 8499826497766216534L;
	private List<MatchRow> data;

	public void setData(List<MatchRow> data) {
		this.data = data;
		fireTableDataChanged();
	}

	@Override
	public Object getValueAt(int row, int column) {
		MatchRow matchRow = this.data.get(row);
		switch (column) {
		case MATCHDATECOLUMN:
			if (matchRow.isMatchHeaderLine()) {
				return matchRow.getMatch().getMatchDate();
			}
			break;
		case MATCHIDCOLUMN:
			if (matchRow.isMatchHeaderLine()) {
				return matchRow.getMatch().getMatchId();
			}
			break;
		case MATCHTYPECOLUMN:
			if (matchRow.isMatchHeaderLine()) {
				return matchRow.getMatch().getMatchType();
			}
			break;
		case HOMETACTICCOLUMN:
			if (matchRow.isMatchHeaderLine()) {
				return matchRow.getMatch().getHostingTeamTactic();
			}
			break;
		case HOMEEVENTCOLUMN:
			return matchRow;
		case HOMETEAMCOLUMN:
			if (matchRow.isMatchHeaderLine()) {
				return matchRow.getMatch().getHostingTeam();
			}
			break;
		case RESULTCOLUMN:
			if (matchRow.isMatchHeaderLine()) {
				return matchRow.getMatch().getMatchResult();
			}
			break;
		case AWAYTEAMCOLUMN:
			if (matchRow.isMatchHeaderLine()) {
				return matchRow.getMatch().getVisitingTeam();
			}
			break;
		case AWAYEVENTCOLUMN:
			return matchRow;
		case AWAYTACTICCOLUMN:
			if (matchRow.isMatchHeaderLine()) {
				return matchRow.getMatch().getVisitingTeamTactic();
			}
			break;
		case MINUTECOLUMN:
			if (matchRow.getMatchHighlight() != null) {
				return matchRow.getMatchHighlight().getMinute();
			}
			break;
		case CHANCECOLUMN:
			return matchRow;
		case EVENTTYPCOLUMN:
			return matchRow.getMatchHighlight();
		case SETEXTCOLUMN:
			if (matchRow.getMatchHighlight() != null) {
				return SpecialEventsDM.getSEText(matchRow.getMatchHighlight());
			}
			break;
		case NAMECOLUMN:
			if (matchRow.getMatchHighlight() != null) {
				return SpecialEventsDM.getSpielerName(matchRow.getMatchHighlight());
			}
			break;
		}

		return null;
	}

	@Override
	public int getRowCount() {
		if (this.data == null) {
			return 0;
		}
		return this.data.size();
	}

	@Override
	public int getColumnCount() {
		return 15;
	}

	@Override
	public String getColumnName(int columnIndex) {

		switch (columnIndex) {
		case MATCHDATECOLUMN:
			return HOVerwaltung.instance().getLanguageString("Datum");
		case MATCHIDCOLUMN:
			return HOVerwaltung.instance().getLanguageString("ls.match.id");
		case HOMETACTICCOLUMN:
			return HOVerwaltung.instance().getLanguageString("ls.team.tactic");
		case HOMETEAMCOLUMN:
			return HOVerwaltung.instance().getLanguageString("Heim");
		case AWAYTEAMCOLUMN:
			return HOVerwaltung.instance().getLanguageString("Gast");
		case AWAYTACTICCOLUMN:
			return HOVerwaltung.instance().getLanguageString("ls.team.tactic");
		case MINUTECOLUMN:
			return HOVerwaltung.instance().getLanguageString("Min");
		case SETEXTCOLUMN:
			return HOVerwaltung.instance().getLanguageString("Event");
		case NAMECOLUMN:
			return HOVerwaltung.instance().getLanguageString("Spieler");
		case CHANCECOLUMN:
		case AWAYEVENTCOLUMN:
		case RESULTCOLUMN:
			return HOVerwaltung.instance().getLanguageString("ls.match.result");
		case HOMEEVENTCOLUMN:
		case EVENTTYPCOLUMN:
		case MATCHTYPECOLUMN:
			return " ";
		default:
			return super.getColumnName(columnIndex);
		}
	}

	public MatchRow getMatchRow(int index) {
		return this.data.get(index);
	}
}
