package module.misc;

import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;


public final class MiscModule extends DefaultModule {

	public MiscModule(){
		super(true);
	}

	@Override
	public int getModuleId() {
		return MISC;
	}

	@Override
	public String getKey() {
		return "Verschiedenes";
	}

	@Override
	public JPanel createTabPanel() {
		return new InformationsPanel();
	}

	public void addMenus() {
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0);
	}

}
