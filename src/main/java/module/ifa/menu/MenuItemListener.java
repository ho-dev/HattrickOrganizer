package module.ifa.menu;

import core.gui.HOMainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuItemListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent arg0) {
		RebuildDialog rebuild = new RebuildDialog(HOMainFrame.INSTANCE);
		rebuild.setVisible(true);
	}	
}
