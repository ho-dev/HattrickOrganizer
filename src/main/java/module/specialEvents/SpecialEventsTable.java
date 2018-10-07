package module.specialEvents;

import static module.specialEvents.SpecialEventsTableModel.AWAYEVENTCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.AWAYTACTICCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.EVENTTYPCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.HOMEEVENTCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.HOMETACTICCOLUMN;
import static module.specialEvents.SpecialEventsTableModel.MATCHTYPECOLUMN;
import static module.specialEvents.SpecialEventsTableModel.NAMECOLUMN;
import static module.specialEvents.SpecialEventsTableModel.SETEXTCOLUMN;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.util.HOLogger;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class SpecialEventsTable extends JTable {

	private static final long serialVersionUID = 8656004206333977669L;

	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(columnModel) {

			private static final long serialVersionUID = 203261496086729638L;

			@Override
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				Point p = e.getPoint();
				int index = columnModel.getColumnIndexAtX(p.x);
				int modelIndex = convertColumnIndexToModel(columnModel.getColumnIndexAtX(p.x));
				if (modelIndex == HOMEEVENTCOLUMN || modelIndex == AWAYEVENTCOLUMN) {
					tip = HOVerwaltung.instance().getLanguageString("Tip4");
				} else {
					tip = getModel().getColumnName(modelIndex);

				}
				return tip;
			}

		};
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		String tip = null;
		Point p = e.getPoint();
		int rowIndex = rowAtPoint(p);
		int colIndex = columnAtPoint(p);
		int modelColumnIndex = convertColumnIndexToModel(colIndex);
		int modelRowIndex = convertRowIndexToModel(rowIndex);
		MatchRow row = ((SpecialEventsTableModel) getModel())
				.getMatchRow(convertRowIndexToModel(rowIndex));
		switch (modelColumnIndex) {
		case HOMEEVENTCOLUMN:
		case AWAYEVENTCOLUMN:
			return HOVerwaltung.instance().getLanguageString("Tip4");
		case NAMECOLUMN:
			return HOVerwaltung.instance().getLanguageString("TipName");
		case EVENTTYPCOLUMN:
		case SETEXTCOLUMN:
			return getEventText(row);
		case AWAYTACTICCOLUMN:
			return getTacticToolTipText(row, false);
		case HOMETACTICCOLUMN:
			return getTacticToolTipText(row, true);
		case MATCHTYPECOLUMN:
			return row.getMatch().getMatchType().getName();
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int mColIndex) {
		return false;
	}

	private String getTacticToolTipText(MatchRow row, boolean homeTeam) {
		int tactic = (homeTeam) ? row.getMatch().getHostingTeamTactic() : row.getMatch()
				.getVisitingTeamTactic();
		switch (tactic) {
		case IMatchDetails.TAKTIK_NORMAL:
			return getLangStr("ls.team.tactic.normal");
		case IMatchDetails.TAKTIK_PRESSING:
			return getLangStr("ls.team.tactic.pressing");
		case IMatchDetails.TAKTIK_KONTER:
			return getLangStr("ls.team.tactic.counter-attacks");
		case IMatchDetails.TAKTIK_MIDDLE:
			return getLangStr("ls.team.tactic.attackinthemiddle");
		case IMatchDetails.TAKTIK_WINGS:
			return getLangStr("ls.team.tactic.attackonwings");
		case IMatchDetails.TAKTIK_CREATIVE:
			return getLangStr("ls.team.tactic.playcreatively");
		case IMatchDetails.TAKTIK_LONGSHOTS:
			return getLangStr("ls.team.tactic.longshots");
		default:
			return "unknown";
		}
	}
	
	private String getEventText(MatchRow row) {
		if (row.getMatchHighlight() != null) {
			String highlightText = "<table width='300'><tr><td>"
					+ row.getMatchHighlight().getEventText() + "</td></tr></table>";
			return "<html>" + highlightText + "</html>";
		}
		return "";
	}

	private void showDebug(Exception exr) {
		HOLogger.instance().error(this.getClass(), exr);
	}
	

	/**
	 * Convenience method.
	 * @param key
	 * @return
	 */
	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
}
