package module.specialEvents.table;

import java.awt.Component;

import javax.swing.JTable;

import core.gui.comp.renderer.DateTimeTableCellRenderer;

public class DateTableCellRenderer extends DateTimeTableCellRenderer {

	private static final long serialVersionUID = -4942592433778998537L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		RowColorDecorator.decorate(table, row, component, isSelected);
		return component;
	}
	
}
