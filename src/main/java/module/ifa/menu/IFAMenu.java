package module.ifa.menu;

import core.model.TranslationFacility;

import javax.swing.*;


public class IFAMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	public IFAMenu() {
		super(TranslationFacility.tr("Tab_IFA"));
		initialize();
	}

	private void initialize() {
		add(getRebuildItem());
		
	}
	
	private JMenuItem getRebuildItem(){
		JMenuItem ifaItem = new JMenuItem(TranslationFacility.tr("ls.menu.modules.ifa.reloadallmatches"));
		ifaItem.addActionListener(new MenuItemListener());
		return ifaItem;
	}
}
