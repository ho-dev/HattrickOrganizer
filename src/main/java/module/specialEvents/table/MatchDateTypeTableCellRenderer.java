package module.specialEvents.table;

import core.gui.HOMainFrame;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.MatchType;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class MatchDateTypeTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -703626020830654L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);

		if (value == null) {
			component.setIcon(null);
		}
		else {
			Triplet oMatchDateMatchType = (Triplet) value;
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			Date oMatchDate = (Date) oMatchDateMatchType.getValue0();
			component.setText("  (" + dateFormat.format(oMatchDate) + ")");
			MatchType oMatchType = (MatchType) oMatchDateMatchType.getValue1();
			ImageIcon oMatchTypeIcon = ThemeManager.getIcon(HOIconName.MATCHICONS[oMatchType.getIconArrayIndex()]);
			component.setIcon(oMatchTypeIcon);
			Integer oMatchID = (Integer) oMatchDateMatchType.getValue2();
			component.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					HOMainFrame.instance().showMatch(oMatchID);
				}
			});
		}
		RowColorDecorator.decorate(table, row, component, isSelected);
		return component;
	}
}
