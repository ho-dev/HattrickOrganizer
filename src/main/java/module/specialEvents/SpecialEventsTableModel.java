package module.specialEvents;

import core.model.HOVerwaltung;
import org.javatuples.Triplet;
import org.jetbrains.annotations.Nullable;
import org.javatuples.Pair;

import java.util.List;
import javax.swing.table.AbstractTableModel;

public class SpecialEventsTableModel extends AbstractTableModel {

	static final int MATCH_DATE_TYPE_COLUMN = 0;
	static final int HOMETACTICCOLUMN = 1;
	static final int HOMETEAMCOLUMN = 2;
	static final int RESULTCOLUMN = 3;
	static final int AWAYTEAMCOLUMN = 4;
	static final int AWAYTACTICCOLUMN = 5;
	static final int MINUTECOLUMN = 6;
	static final int CHANCECOLUMN = 7;
	static final int EVENTTYPCOLUMN = 8;
	static final int EVENTTEXTCOLUMN = 9;
	static final int PLAYER_NAME_COLUMN = 10;
	static final List<Integer> HEADER_ROWS = List.of(MATCH_DATE_TYPE_COLUMN, HOMETACTICCOLUMN, HOMETEAMCOLUMN, RESULTCOLUMN, AWAYTEAMCOLUMN, AWAYTACTICCOLUMN);

	private List<MatchRow> data;

	public void setData(List<MatchRow> data) {
		this.data = data;
		fireTableDataChanged();
	}

	@Nullable
	@Override
	public Object getValueAt(int row, int column) {
		MatchRow matchRow = this.data.get(row);

		// if not a match event line and not a header line -> return null
		if ((!matchRow.isMatchHeaderLine()) & HEADER_ROWS.contains(column)) return null;

		return switch (column) {
			case MATCH_DATE_TYPE_COLUMN -> Triplet.with(matchRow.getMatch().getMatchDate(), matchRow.getMatch().getMatchType(), matchRow.getMatch().getMatchId());
			case HOMETACTICCOLUMN -> matchRow.getMatch().getHostingTeamTactic();
			case HOMETEAMCOLUMN -> matchRow.getMatch().getHostingTeam();
			case RESULTCOLUMN -> matchRow.getMatch().getMatchResult();
			case AWAYTEAMCOLUMN -> matchRow.getMatch().getVisitingTeam();
			case AWAYTACTICCOLUMN -> matchRow.getMatch().getVisitingTeamTactic();
			case MINUTECOLUMN -> matchRow.getMatchHighlight().getMinute();
			case CHANCECOLUMN -> matchRow;
			case EVENTTYPCOLUMN -> matchRow.getMatchHighlight();
			case EVENTTEXTCOLUMN -> SpecialEventsDM.getSEText(matchRow.getMatchHighlight());
			case PLAYER_NAME_COLUMN -> SpecialEventsDM.getSpielerName(matchRow.getMatchHighlight());
			default -> null; };
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
		return 11;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case MATCH_DATE_TYPE_COLUMN:
			return HOVerwaltung.instance().getLanguageString("ls.match");
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
		case EVENTTEXTCOLUMN:
			return HOVerwaltung.instance().getLanguageString("Event");
		case PLAYER_NAME_COLUMN:
			return HOVerwaltung.instance().getLanguageString("Spieler");
		case CHANCECOLUMN:
		case RESULTCOLUMN:
			return HOVerwaltung.instance().getLanguageString("ls.match.result");
		case EVENTTYPCOLUMN:
			return " ";
		default:
			return super.getColumnName(columnIndex);
		}
	}

	public MatchRow getMatchRow(int index) {
		return this.data.get(index);
	}
}
