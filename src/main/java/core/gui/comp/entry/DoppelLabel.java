// %1614414392:de.hattrickorganizer.gui.templates%
package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;

import java.awt.GridLayout;

import javax.swing.JPanel;



class DoppelLabel extends JPanel {
	
	private static final long serialVersionUID = 4801107348466403035L;
	
    public DoppelLabel() {
        setLayout(new GridLayout(1, 2));
        setOpaque(true);
        setBackground(HODefaultTableCellRenderer.SELECTION_BG);
    }


 
}
