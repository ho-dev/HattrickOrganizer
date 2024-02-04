package tool.updater;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

/**
 * TableCellRenderer for the update plugins panel and user columns settings panel. 
 *
 * @author tdietz
 */
public final class UpdaterCellRenderer implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		double site = 0;
		double ho = 0;
		if (value == null) {
			return new JLabel();
		}

		if (value instanceof JCheckBox) {
			return (JCheckBox) value;
		}

		try {
			site = Double.parseDouble(((JLabel) table.getModel().getValueAt(row, 3)).getText());
		} catch (Exception ignored) {
		}
		try {
			ho = Double.parseDouble(((JLabel) table.getModel().getValueAt(row, 2)).getText());
		} catch (Exception ignored) {
		}

		boolean color = (ho > 0) && (ho < site);

        if (value instanceof JButton b) {
            b.setBorderPainted(false);
			if (color) {
				b.setForeground(ThemeManager.getColor(HOColorName.LABEL_ERROR_FG));
			}
			int labelHeight = 20; // fix table height problems with non-classic l&f
			int tableRowHeight = table.getRowHeight(row);
			if (labelHeight != tableRowHeight) {
				table.setRowHeight(row, labelHeight);
			}
			return b;
		}

		JLabel label;
		if (value instanceof JLabel) {
			label = (JLabel) value;
		} else if (value instanceof Color) {
			label = new JLabel();
			label.setBackground((Color) value);
		} else {
			label = new JLabel(value.toString());
		}

		if (color) {
			label.setForeground(ThemeManager.getColor(HOColorName.LABEL_ERROR_FG));
		}
		return label;
	}
}