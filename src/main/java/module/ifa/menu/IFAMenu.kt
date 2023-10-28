package module.ifa.menu;

import core.model.HOVerwaltung;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


public class IFAMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	public IFAMenu() {
		super(HOVerwaltung.instance().getLanguageString("Tab_IFA"));
		initialize();
	}

	private void initialize() {
		add(getRebuildItem());
		
	}
	
	private JMenuItem getRebuildItem(){
		JMenuItem ifaItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.modules.ifa.reloadallmatches"));
		ifaItem.addActionListener(new MenuItemListener());
		return ifaItem;
	}
}
