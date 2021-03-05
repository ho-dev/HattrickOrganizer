// %1614414392:de.hattrickorganizer.gui.templates%
package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;

import java.awt.*;
import javax.swing.JPanel;


/**
 * Panel with two horizontal elements, left and right.
 */
class DoubleLabel extends JPanel {
	
	private static final long serialVersionUID = 4801107348466403035L;
	private LayoutManager layout = new GridLayout(1, 2);
	
    public DoubleLabel() {
        setLayout(layout);
        setOpaque(true);
        setBackground(HODefaultTableCellRenderer.SELECTION_BG);
    }

    public void setLayoutManager(LayoutManager manager) {
        if (manager != null) {
            this.layout = manager;
            setLayout(this.layout);
            repaint();
        }
    }

}
