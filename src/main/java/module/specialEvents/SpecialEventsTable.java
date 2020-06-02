package module.specialEvents;

import static module.specialEvents.SpecialEventsTableModel.AWAYTACTICCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.EVENTTYPCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.HOMETACTICCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.MATCH_DATE_TYPE_COLUMN;
import static module.specialEvents.SpecialEventsTableModel.PLAYER_NAME_COLUMN;
import static module.specialEvents.SpecialEventsTableModel.EVENTTEXTCOLUMN;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import org.jetbrains.annotations.Nullable;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class SpecialEventsTable extends JTable {

	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(columnModel) {};
	}

	@Nullable
	@Override
	public String getToolTipText(MouseEvent e) {
		Point p = e.getPoint();
		int rowIndex = rowAtPoint(p);
		int colIndex = columnAtPoint(p);
		int modelColumnIndex = convertColumnIndexToModel(colIndex);
		MatchRow row = ((SpecialEventsTableModel) getModel()).getMatchRow(convertRowIndexToModel(rowIndex));
		return switch (modelColumnIndex) {
			case PLAYER_NAME_COLUMN -> HOVerwaltung.instance().getLanguageString("TipName");
			case EVENTTYPCOLUMN, EVENTTEXTCOLUMN -> getEventText(row);
			case AWAYTACTICCOLUMN -> getTacticToolTipText(row, false);
			case HOMETACTICCOLUMN -> getTacticToolTipText(row, true);
			case MATCH_DATE_TYPE_COLUMN -> row.getMatch().getMatchType().getName();
			default -> null;
		};
	}

	@Override
	public boolean isCellEditable(int rowIndex, int mColIndex) {
		return false;
	}

	private String getTacticToolTipText(MatchRow row, boolean homeTeam) {
		int tactic = (homeTeam) ? row.getMatch().getHostingTeamTactic() : row.getMatch().getVisitingTeamTactic();
		return switch (tactic) {
			case IMatchDetails.TAKTIK_NORMAL -> getLangStr("ls.team.tactic.normal");
			case IMatchDetails.TAKTIK_PRESSING -> getLangStr("ls.team.tactic.pressing");
			case IMatchDetails.TAKTIK_KONTER -> getLangStr("ls.team.tactic.counter-attacks");
			case IMatchDetails.TAKTIK_MIDDLE -> getLangStr("ls.team.tactic.attackinthemiddle");
			case IMatchDetails.TAKTIK_WINGS -> getLangStr("ls.team.tactic.attackonwings");
			case IMatchDetails.TAKTIK_CREATIVE -> getLangStr("ls.team.tactic.playcreatively");
			case IMatchDetails.TAKTIK_LONGSHOTS -> getLangStr("ls.team.tactic.longshots");
			default -> "";
		};
	}
	
	private String getEventText(MatchRow row) {
		if (row.getMatchHighlight() != null) {
			String highlightText = "<table width='300'><tr><td>"
					+ row.getMatchHighlight().getEventText() + "</td></tr></table>";
			return "<html>" + highlightText + "</html>";
		}
		return "";
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
}
