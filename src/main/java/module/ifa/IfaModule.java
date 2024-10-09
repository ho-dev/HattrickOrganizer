package module.ifa;

import core.model.TranslationFacility;
import core.module.DefaultModule;
import module.ifa.menu.IFAMenu;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class IfaModule extends DefaultModule {

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_4, KeyEvent.CTRL_MASK);
	}

	@Override
	public int getModuleId() {
		return IFA;
	}

	@Override
	public String getDescription() {
		return TranslationFacility.tr("Tab_IFA");
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
