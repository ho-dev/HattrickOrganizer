package module.nthrf;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import core.module.DefaultModule;

public class NthrfModule extends DefaultModule {

	public KeyStroke getKeyStroke() {
		return null;
	}

	@Override
	public int getModuleId() {
		return NTHRF;
	}

	@Override
	public String getDescription() {
		return "Nthrf";
	}

	@Override
	public boolean hasMainTab() {
		return false;
	}
	
	@Override
	public JPanel createTabPanel() {
		return null;
	}
	
	@Override
	public boolean hasMenu(){
		return true;
	}
	@Override
	public JMenu getMenu(){
		return new NthrfMenu();
	}

}
