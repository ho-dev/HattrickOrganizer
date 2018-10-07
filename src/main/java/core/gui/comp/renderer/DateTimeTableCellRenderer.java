package core.gui.comp.renderer;

import java.awt.Component;
import java.util.Date;
import java.text.DateFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateTimeTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -5869341433817862361L;
	private DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		String dateString = "";
		if (value != null) {
			dateString = this.format.format((Date) value);
		}
		return super.getTableCellRendererComponent(table, dateString, isSelected, hasFocus, row,
				column);
	}
}