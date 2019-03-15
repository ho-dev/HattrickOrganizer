// %2336472602:hoplugins.teamAnalyzer.ui%
package module.teamAnalyzer.ui;

import core.gui.model.MatchesColumnModel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.MatchType;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;



/**
 * Renderer for the manual match selection.
 *
 * @author Draghetto
 */
public class ManualFilterTableRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -685008864266149099L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		MatchType type = MatchType.NONE;

		// (blaghaid fixes selection colors)
		setForeground(Color.black);
		
		try {
			type = MatchType.getById(Integer.parseInt((String) table.getValueAt(row, 7)));
		} catch (NumberFormatException e) {
		}

		setBackground(MatchesColumnModel.getColor4Matchtyp(type));
		String available = (String) table.getValueAt(row, 6);

		if (isSelected) {
			setBackground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG));
		}

		if (!available.equalsIgnoreCase("true")) {
			setEnabled(false);
		} else {
			setEnabled(true);
		}
		
		if (value instanceof ImageIcon) {
			setIcon(ThemeManager.getIcon(HOIconName.MATCHICONS[type.getIconArrayIndex()]));
			setText(null);
		} else {
			setIcon(null);
		}

		
		return this;
		
	}
}
