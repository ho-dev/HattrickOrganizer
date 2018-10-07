package core.gui.comp.renderer;

import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class DoubleTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -3966963303325802102L;
	private final NumberFormat format;

	public DoubleTableCellRenderer(int precision) {
		this.format = NumberFormat.getInstance();
		this.format.setMaximumFractionDigits(precision);
		this.format.setMinimumFractionDigits(precision);
		setHorizontalAlignment(SwingConstants.RIGHT);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		String numberString = this.format.format((Number) value);
		return super.getTableCellRendererComponent(table, numberString, isSelected, hasFocus, row,
				column);
	}
}