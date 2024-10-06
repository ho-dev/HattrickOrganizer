package module.playerOverview;

import core.model.TranslationFacility;
import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

public final class PlayerOverviewModule extends DefaultModule {

	private PlayerOverviewPanel overviewPanel;
	public PlayerOverviewModule(){
		super(true);
	}
	
	@Override
	public int getModuleId() {
		return PLAYEROVERVIEW;
	}

	@Override
	public String getDescription() {
		return TranslationFacility.tr("Spieleruebersicht");
	}

	@Override
	public JPanel createTabPanel() {
		overviewPanel = new PlayerOverviewPanel();
		return overviewPanel;
	}

	@Override
	public void storeUserSettings()
	{
		if (overviewPanel != null) overviewPanel.storeUserSettings();
	}

	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
	}

}
