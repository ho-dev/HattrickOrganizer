package module.specialEvents.table;

import core.util.StringUtils;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PlayerNameTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -70362628083065436L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		JLabel label = (JLabel) super.getTableCellRendererComponent(table, "", isSelected,
				hasFocus, row, column);

		String playerName = (String) value;

		if (!StringUtils.isEmpty(playerName)) {
			String rName = playerName.substring(0, playerName.length() - 2);
			String rType = playerName.substring(playerName.length() - 1);
			label.setText(rName);
			if (rType.equals("-")) {
				label.setForeground(Color.RED);
			} else if (rType.equals("#")) {
				label.setForeground(Color.LIGHT_GRAY);
			} else {
				label.setForeground(Color.BLACK);
			}
		} else {
			label.setForeground(Color.BLACK);
			label.setText("");
		}
		RowColorDecorator.decorate(table, row, label, isSelected);
		return label;
	}

}
