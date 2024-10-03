package module.transfer;

import core.model.TranslationFacility;
import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

public final class TransfersModule extends DefaultModule {
	private TransfersPanel panel;

	public TransfersModule(){
		super(true);
	}
	
	@Override
	public int getModuleId() {
		return TRANSFERS;
	}

	@Override
	public String getDescription() {
		return TranslationFacility.tr("Transfers");
	}

	@Override
	public JPanel createTabPanel() {
		panel = new TransfersPanel();
		return panel;
	}

	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0);
	}

	public void storeUserSettings()
	{
		if ( panel != null) panel.storeUserSettings();
	}

}
