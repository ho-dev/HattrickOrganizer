package module.ifa;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import core.model.HOVerwaltung;
import core.module.DefaultModule;
import module.ifa.menu.IFAMenu;

public class IfaModule extends DefaultModule {

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_5, KeyEvent.CTRL_MASK);
	}

	@Override
	public int getModuleId() {
		return IFA;
	}

	@Override
	public String getDescription() {
		return HOVerwaltung.instance().getLanguageString("Tab_IFA");
	}

	@Override
	public JPanel createTabPanel() {
		return new PluginIfaPanel();
	}
	
	@Override
	public boolean hasMenu(){
		return true;
	}
	@Override
	public JMenu getMenu(){
		return new IFAMenu();
	}

}
