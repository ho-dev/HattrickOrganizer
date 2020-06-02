package module.specialEvents;

import core.model.HOVerwaltung;
import org.apache.commons.text.WordUtils;
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
	static final int EVENTTYPCOLUMN = 7;
	static final int EVENTTEXTCOLUMN = 8;
	static final int PLAYER_NAME_COLUMN = 9;
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
			case MATCH_DATE_TYPE_COLUMN -> Pair.with(matchRow.getMatch().getMatchDate(), matchRow.getMatch().getMatchType());
			case HOMETACTICCOLUMN -> matchRow.getMatch().getHostingTeamTactic();
			case HOMETEAMCOLUMN -> matchRow.getMatch().getHostingTeam();
			case RESULTCOLUMN -> matchRow.getMatch().getMatchResult();
			case AWAYTEAMCOLUMN -> matchRow.getMatch().getVisitingTeam();
			case AWAYTACTICCOLUMN -> matchRow.getMatch().getVisitingTeamTactic();
			case MINUTECOLUMN -> matchRow.getMatchHighlight().getMinute();
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
		return 10;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return switch (columnIndex) {
			case MATCH_DATE_TYPE_COLUMN -> WordUtils.capitalizeFully(getLangStr("SpieleDetails"));
			case HOMETACTICCOLUMN, AWAYTACTICCOLUMN -> WordUtils.capitalizeFully(getLangStr("ls.team.tactic"));
			case HOMETEAMCOLUMN -> WordUtils.capitalizeFully(getLangStr("Heim"));
			case AWAYTEAMCOLUMN -> WordUtils.capitalizeFully(getLangStr("Gast"));
			case EVENTTEXTCOLUMN -> WordUtils.capitalizeFully(getLangStr("Event"));
			case PLAYER_NAME_COLUMN -> WordUtils.capitalizeFully(getLangStr("Spieler"));
			case RESULTCOLUMN -> WordUtils.capitalizeFully(getLangStr("ls.match.result"));
			default -> " ";
		};
	}

	public MatchRow getMatchRow(int index) {
		return this.data.get(index);
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}

}
