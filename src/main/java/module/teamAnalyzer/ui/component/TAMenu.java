package module.teamAnalyzer.ui.component;

import core.model.HOVerwaltung;
import module.teamAnalyzer.ui.controller.DownloadItemListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;


public class TAMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	public TAMenu() {
		super(HOVerwaltung.instance().getLanguageString("TeamAnalyzer"));
		initialize();
	}

	private void initialize() {
		add(getDownloadItem());
		add(new FavouriteMenu());
		
	}
	
	private JMenuItem getDownloadItem(){
		JMenuItem downloadItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("Menu.DownloadMatch"));
		downloadItem.addActionListener(new DownloadItemListener());
		return downloadItem;
	}
}
