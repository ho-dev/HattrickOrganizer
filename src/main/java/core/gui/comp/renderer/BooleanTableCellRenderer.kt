package core.gui.comp.renderer;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;


public class BooleanTableCellRenderer extends JCheckBox implements TableCellRenderer {

	public BooleanTableCellRenderer() {
		setOpaque(true);
		setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		boolean boolValue = (Boolean) value;
		setSelected(boolValue);

		return this;
	}

}
