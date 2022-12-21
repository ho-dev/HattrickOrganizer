// %3338167864:hoplugins.teamAnalyzer.ui%
package module.teamAnalyzer.ui;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import java.awt.Component;
import java.awt.Font;
import java.io.Serial;

import javax.swing.*;


/**
 * Renderer for the selected matches in the TA.
 * 
 * @author Draghetto
 */
public class RecapTableRenderer extends HODefaultTableCellRenderer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax
	 * .swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		var ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		// Once and for all (blaghaid)
//		setForeground(Color.black);
		
		// Is this 'average' or 'no matches' row?
		if ((row == 0) && ((table.getRowCount() > 1) || (table.getValueAt(0, 1) == null))) {
			Font nFont = new Font(ret.getFont().getFontName(), Font.BOLD, ret.getFont().getSize());

			ret.setFont(nFont);
			ret.setBackground(ThemeManager.getColor(HOColorName.PLAYER_POS_BG));
			//ret.setIcon(null);
		}

		if (isSelected) {
			ret.setBackground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG));
		}

		return ret;
	}
}
