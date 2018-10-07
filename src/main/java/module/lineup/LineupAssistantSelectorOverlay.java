package module.lineup;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

/**
 * A class that is used to overlay panels with green or red color to make
 * selection of positions for the lineup assistant to ignore.
 */
public class LineupAssistantSelectorOverlay extends JPanel {

	private static final long serialVersionUID = 1L;
	private boolean isSelected;

	public LineupAssistantSelectorOverlay() {
		super();
		init();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (isSelected) {
			g.setColor(ThemeManager.getColor(HOColorName.SEL_OVERLAY_SELECTION_BG));
		} else {
			g.setColor(ThemeManager.getColor(HOColorName.SEL_OVERLAY_BG));
		}
		g.fillRect(0, 0, 500, 500);
	}

	public void setSelected(boolean b) {
		isSelected = b;
		repaint();
	}

	public boolean isSelected() {
		return isSelected;
	}

	private void init() {
		setOpaque(false);
		setLayout(null);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// Flip color (and selected)
				isSelected = !isSelected;
				repaint();
			}
		});
	}
}
