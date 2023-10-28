package module.specialEvents.table;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.enums.MatchType;
import core.util.HODateTime;
import org.javatuples.Pair;
import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class MatchDateTypeTableCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setIcon(null);
		setText("");

		if (value != null) {
			Pair oMatchDateMatchType = (Pair) value;
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			HODateTime oMatchDate = (HODateTime) oMatchDateMatchType.getValue0();
			setText("  (" + oMatchDate.toLocaleDate() + ")");
			MatchType oMatchType = (MatchType) oMatchDateMatchType.getValue1();
			Icon oMatchTypeIcon = ThemeManager.getIcon(HOIconName.MATCHICONS[oMatchType.getIconArrayIndex()]);
			setIcon(oMatchTypeIcon);
		}
		RowColorDecorator.decorate(table, row, this, isSelected);
		return this;
	}
}
