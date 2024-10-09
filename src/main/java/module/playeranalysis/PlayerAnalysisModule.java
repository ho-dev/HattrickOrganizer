package module.playeranalysis;

import core.model.TranslationFacility;
import core.module.DefaultModule;
import core.module.config.ModuleConfig;

import javax.swing.*;
import java.awt.event.KeyEvent;

public final class PlayerAnalysisModule extends DefaultModule {
	static final String SHOW_GAMEANALYSIS = "PA_PlayerAnalysis";
	public static final String SHOW_PLAYERCOMPARE = "PA_PlayerCompare";

	public PlayerAnalysisModule(){
		super(true);
		initialize();
	}
	
	private void initialize() {
		if(! ModuleConfig.instance().containsKey(SHOW_PLAYERCOMPARE))
			ModuleConfig.instance().setBoolean(SHOW_PLAYERCOMPARE, false);
		
	}

	@Override
	public int getModuleId() {
		return PLAYERANALYSIS;
	}

	@Override
	public String getDescription() {
		return TranslationFacility.tr("SpielerAnalyse");
	}

	@Override
	public JPanel createTabPanel() {
		return new PlayerAnalysisModulePanel();
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
	}
	
	@Override
	public boolean hasConfigPanel() {
		return true;
	}

	@Override
	public JPanel createConfigPanel() {
		return new SettingPanel();
	}

}
