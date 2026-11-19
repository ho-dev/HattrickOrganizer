package module.specialevents;

import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;


public class SpecialEventsModule extends DefaultModule {
	SpecialEventsPanel specialEventsPanel;

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.CTRL_MASK);
	}

	@Override
	public int getModuleId() {
		return SPECIALEVENTS;
	}

	@Override
	public String getKey() {
		return "Tab_SpecialEvents";
	}

	@Override
	public JPanel createTabPanel() {
		specialEventsPanel = new SpecialEventsPanel();
		return specialEventsPanel;
	}

	@Override
	public void storeUserSettings()
	{
		if ( specialEventsPanel != null) specialEventsPanel.storeUserSettings();
	}

}
