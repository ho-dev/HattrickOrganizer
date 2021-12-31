// %3338167864:hoplugins.teamAnalyzer.ui%
package module.teamAnalyzer.ui;

import core.gui.model.MatchesColumnModel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.enums.MatchType;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * Renderer for the selected matches in the TA.
 * 
 * @author Draghetto
 */
public class RecapTableRenderer extends DefaultTableCellRenderer {
	// ~ Methods
	// ------------------------------------------------------------------------------------

	/**
	 * 
	 */
	private static final long serialVersionUID = -1496877275674136140L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax
	 * .swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		// Once and for all (blaghaid)
		setForeground(Color.black);
		
		// Is this 'average' or 'no matches' row?
		if ((row == 0) && ((table.getRowCount() > 1) || (table.getValueAt(0, 1) == null))) {
			Font nFont = new Font(this.getFont().getFontName(), Font.BOLD, this.getFont().getSize());

			setFont(nFont);
			setBackground(ThemeManager.getColor(HOColorName.PLAYER_POS_BG)); 
			setIcon(null);
		} else {
			MatchType matchType;
			boolean isHomeMatch;
			Icon icon;
			try {
				icon = (Icon) table.getValueAt(row,1);
				matchType = MatchType.getById((Integer) table.getValueAt(row, 22));
				isHomeMatch = (Boolean) table.getValueAt(row, 23);
				setBackground(MatchesColumnModel.getColor4Matchtyp(matchType));
			} catch (Exception e) {
				// Make the exception visible.
				setBackground(Color.RED);
				setText("!!!"); //$NON-NLS-1$
				setToolTipText(e.toString());

				return this;
			}

			if (column == 1) {// Set icon for match type.
				//setIcon(ThemeManager.getIcon(HOIconName.MATCHICONS[matchType.getIconArrayIndex()]));
				setIcon(icon);
				setText(null);

				StringBuilder tipText = new StringBuilder(matchType.getName());

				tipText.append(" - "); //$NON-NLS-1$

				if (isHomeMatch) {
					tipText.append(HOVerwaltung.instance().getLanguageString("Heim")); //$NON-NLS-1$
				} else {
					tipText.append(HOVerwaltung.instance().getLanguageString("Gast")); //$NON-NLS-1$
				}

				setToolTipText(tipText.toString());
			} else {
				setToolTipText(null);
				setIcon(null);
			}
		}

		if (isSelected) {
			setBackground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG));
		}

		return this;
	}
}
