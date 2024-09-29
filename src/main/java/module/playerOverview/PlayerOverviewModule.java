package module.playerOverview;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.KeyStroke;


import core.model.HOVerwaltung;
import core.module.DefaultModule;

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
		return HOVerwaltung.instance().getLanguageString("Spieleruebersicht");
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
