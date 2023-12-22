package module.specialEvents;

//import static module.specialEvents.SpecialEventsTableModel.AWAYTACTICCOLUMN;
//import static module.specialEvents.SpecialEventsTableModel.EVENTCOLUMN;
//import static module.specialEvents.SpecialEventsTableModel.HOMETACTICCOLUMN;
//import static module.specialEvents.SpecialEventsTableModel.MATCH_DATE_TYPE_COLUMN;

import core.gui.HOMainFrame;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JTable;

public class SpecialEventsTable extends JTable {

	public SpecialEventsTable() {
		super(UserColumnController.instance().getSpecialEventsTableModel());
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				JTable table = (JTable) me.getSource();
				Point p = me.getPoint();
				int col = table.columnAtPoint(p);
				var tableModel = (HOTableModel) table.getModel();
				var matchDateColumn = Arrays.stream(tableModel.getColumns()).findFirst(); // Match date column is first column in table model
				if (matchDateColumn.isPresent()) {
					if (col == matchDateColumn.get().getIndex()) {
						try {
							SpecialEventsTableModel model = UserColumnController.instance().getSpecialEventsTableModel();
							Match oMatch = model.getMatch(table.rowAtPoint(p));
							if (me.isShiftDown()) {
								Desktop.getDesktop().browse(oMatch.getHTURL());
							} else {
								HOMainFrame.instance().showMatch(oMatch.getMatchId());
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		setOpaque(false);
		setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
		SpecialEventsTableModel tblModel = UserColumnController.instance().getSpecialEventsTableModel();
		setModel(tblModel);
		tblModel.restoreUserSettings(this);
	}

	public void storeUserSettings() {
		var tableModel = (HOTableModel)getModel();
		tableModel.storeUserSettings(this);
	}


//	@Override
//	protected JTableHeader createDefaultTableHeader() {
//		return new JTableHeader(columnModel) {};
//	}

//	@Nullable
//	@Override
//	public String getToolTipText(MouseEvent e) {
//		Point p = e.getPoint();
//		int rowIndex = rowAtPoint(p);
//		int colIndex = columnAtPoint(p);
//		int modelColumnIndex = convertColumnIndexToModel(colIndex);
//		MatchRow row = ((SpecialEventsTableModel) getModel()).getMatchRow(convertRowIndexToModel(rowIndex));
//		return switch (modelColumnIndex) {
//			case EVENTCOLUMN -> getEventText(row);
//			case AWAYTACTICCOLUMN -> getTacticToolTipText(row, false);
//			case HOMETACTICCOLUMN -> getTacticToolTipText(row, true);
//			case MATCH_DATE_TYPE_COLUMN -> row.getMatch().getMatchType().getName();
//			default -> null;
//		};
//	}

	@Override
	public boolean isCellEditable(int rowIndex, int mColIndex) {
		return false;
	}

//	private String getTacticToolTipText(MatchRow row, boolean homeTeam) {
//		int tactic = (homeTeam) ? row.getMatch().getHostingTeamTactic() : row.getMatch().getVisitingTeamTactic();
//		return switch (tactic) {
//			case IMatchDetails.TAKTIK_NORMAL -> getLangStr("ls.team.tactic.normal");
//			case IMatchDetails.TAKTIK_PRESSING -> getLangStr("ls.team.tactic.pressing");
//			case IMatchDetails.TAKTIK_KONTER -> getLangStr("ls.team.tactic.counter-attacks");
//			case IMatchDetails.TAKTIK_MIDDLE -> getLangStr("ls.team.tactic.attackinthemiddle");
//			case IMatchDetails.TAKTIK_WINGS -> getLangStr("ls.team.tactic.attackonwings");
//			case IMatchDetails.TAKTIK_CREATIVE -> getLangStr("ls.team.tactic.playcreatively");
//			case IMatchDetails.TAKTIK_LONGSHOTS -> getLangStr("ls.team.tactic.longshots");
//			default -> "";
//		};
//	}
	
//	private String getEventText(MatchRow row) {
//		if (row.getMatchHighlight() != null) {
//			String highlightText = "<table width='300'><tr><td>"
//					+ row.getMatchHighlight().getEventText() + "</td></tr></table>";
//			return "<html>" + highlightText + "</html>";
//		}
//		return "";
//	}
//
//	private String getLangStr(String key) {
//		return HOVerwaltung.instance().getLanguageString(key);
//	}
}
