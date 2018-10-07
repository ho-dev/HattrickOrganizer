package module.specialEvents.table;

import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TacticsTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -7036269102083065436L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		String tactic = null;
		if (value != null) {
			int tacticId = ((Integer) value).intValue();
			switch (tacticId) {
			case IMatchDetails.TAKTIK_NORMAL:
				tactic = "";
				break;
			case IMatchDetails.TAKTIK_PRESSING:
				tactic = HOVerwaltung.instance().getLanguageString("ls.team.tactic_short.pressing");
				break;
			case IMatchDetails.TAKTIK_KONTER:
				tactic = HOVerwaltung.instance().getLanguageString("ls.team.tactic_short.counter-attacks");
				break;
			case IMatchDetails.TAKTIK_MIDDLE:
				tactic = HOVerwaltung.instance().getLanguageString("ls.team.tactic_short.attackinthemiddle");
				break;
			case IMatchDetails.TAKTIK_WINGS:
				tactic = HOVerwaltung.instance().getLanguageString("ls.team.tactic_short.attackonwings");
				break;
			case IMatchDetails.TAKTIK_CREATIVE:
				tactic = HOVerwaltung.instance().getLanguageString("ls.team.tactic_short.playcreatively");
				break;
			case IMatchDetails.TAKTIK_LONGSHOTS:
				tactic = HOVerwaltung.instance().getLanguageString("ls.team.tactic_short.longshots");
				break;
			default:
				tactic = " ? " + tacticId;
			}
		}

		Component component = super.getTableCellRendererComponent(table, tactic, isSelected,
				hasFocus, row, column);
		RowColorDecorator.decorate(table, row, component, isSelected);
		return component;
	}

}
