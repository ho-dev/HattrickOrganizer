package module.evilcard.gui;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

class PlayersTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -8182314658725941668L;
	private NumberFormat percentFormat;
	public static final Color WARNINGS_COLOR = ThemeManager.getColor("lightYellow");
	public static final Color WARNINGS_TYPE2_COLOR = new Color(255, 227, 200);
	public static final Color DIRECT_RED_COLOR = ThemeManager
			.getColor(HOColorName.LEAGUE_DEMOTED_BG);
	public static final Color NUMBER_COLOR = ThemeManager.getColor("lightGreen");
	static final String PERCENT = " %";
	static final String NO_AVERAGE = "-";

	public PlayersTableCellRenderer() {
		this.percentFormat = NumberFormat.getNumberInstance();
		this.percentFormat.setMinimumFractionDigits(1);
		this.percentFormat.setMaximumFractionDigits(1);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		Object val = value;
		if (column ==  PlayersTableModel.COL_WEIGHTED_AVERAGE || column == PlayersTableModel.COL_RAW_AVERAGE) {
			if (value == null) {
				val = NO_AVERAGE;
			} else {			
				val = this.percentFormat.format((Double)value) + PERCENT;
			}
		}

		super.getTableCellRendererComponent(table, val, isSelected, hasFocus, row, column);

		switch (column) {
		case PlayersTableModel.COL_CARDS:
		case PlayersTableModel.COL_MATCHES:
		case PlayersTableModel.COL_RAW_AVERAGE:
		case PlayersTableModel.COL_WEIGHTED_AVERAGE:
			this.setBackground(NUMBER_COLOR);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			break;

		case PlayersTableModel.COL_WARNINGS:
		case PlayersTableModel.COL_WARNINGS_TYPE1:
		case PlayersTableModel.COL_WARNINGS_TYPE3:
			this.setBackground(WARNINGS_COLOR);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			break;

		case PlayersTableModel.COL_WARNINGS_TYPE2:
		case PlayersTableModel.COL_WARNINGS_TYPE4:
			this.setBackground(WARNINGS_TYPE2_COLOR);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			break;

		case PlayersTableModel.COL_DIRECT_RED_CARDS:
			this.setBackground(DIRECT_RED_COLOR);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			break;

		default:
			this.setBackground(table.getBackground());
			this.setHorizontalAlignment(SwingConstants.LEFT);
			break;
		}

		if (isSelected) {
			this.setBackground(table.getSelectionBackground());
		}

		return this;
	}
}
