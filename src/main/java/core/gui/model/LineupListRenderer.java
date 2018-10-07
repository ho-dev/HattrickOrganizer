// %2449251406:de.hattrickorganizer.gui.model%
package core.gui.model;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


/**
 * ListCellRenderer that colorizes the lineup, that is currently active.
 */
public class LineupListRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 1L;
	private static Color fgColor = null;

	public LineupListRenderer(final JList list) {
		if (fgColor == null && list != null) {
			fgColor = list.getSelectionBackground().brighter();
			if (fgColor.getRed() > 250 && fgColor.getGreen() > 250 && fgColor.getBlue() > 250) {
				// fallback, if the selBGcolor is (almost) white
				fgColor = list.getSelectionBackground().darker();
			}
		}
	}

	public final Component getListCellRendererComponent(final JList list, final Object value, final int row, final boolean isSelected,
			final boolean hasFocus) {
		setOpaque(true);
		setText(value.toString());
		if (value instanceof AufstellungCBItem && ((AufstellungCBItem) value).isAngezeigt()) {
			setForeground(fgColor);
		} else {
			setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
		}
		setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());

		return this;
	}
}
