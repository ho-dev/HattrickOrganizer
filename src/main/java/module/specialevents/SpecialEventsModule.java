package module.specialevents;

import core.model.TranslationFacility;
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
	public String getDescription() {
		return TranslationFacility.tr("Tab_SpecialEvents");
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
