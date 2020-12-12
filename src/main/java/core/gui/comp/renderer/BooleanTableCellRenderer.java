package core.gui.comp.renderer;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;


public class BooleanTableCellRenderer extends JCheckBox implements TableCellRenderer {

	public BooleanTableCellRenderer() {
		this.setOpaque(true);
		this.setBackground(Color.RED);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		boolean boolValue = (Boolean) value;
		setSelected(boolValue);

		System.out.println(row + " , " + column + ":  " +  value.getClass().toString() + " " + value + "" + this.getBackground());
		System.out.println("-----------------------------------");

		setBackground(Color.RED);
		setHorizontalAlignment(SwingConstants.CENTER);
		return this;
	}

}
