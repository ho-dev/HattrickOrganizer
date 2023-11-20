package core.gui.comp.renderer;
import core.util.HODateTime;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class HODateTimeTableCellRenderer extends DefaultTableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		String dateString = "";
		if (value != null) {
			dateString = ((HODateTime) value).toLocaleDateTime();
		}
		return super.getTableCellRendererComponent(table, dateString, isSelected, hasFocus, row,
				column);
	}
}