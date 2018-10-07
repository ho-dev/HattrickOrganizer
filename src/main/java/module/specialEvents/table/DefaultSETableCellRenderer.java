package module.specialEvents.table;


import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DefaultSETableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 6826209776608817194L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		JLabel component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);
		RowColorDecorator.decorate(table, row, component, isSelected);
		return component;
	}

}
