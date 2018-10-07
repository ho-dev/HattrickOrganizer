package module.playeranalysis;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.KeyStroke;


import core.model.HOVerwaltung;
import core.module.DefaultModule;
import core.module.config.ModuleConfig;

public final class PlayerAnalysisModule extends DefaultModule {
	static final String SHOW_GAMEANALYSIS = "PA_PlayerAnalysis";
	public static final String SHOW_PLAYERCOMPARE = "PA_PlayerCompare";
	static final String SHOW_EXPERIENCE = "PA_Experience";
	
	public PlayerAnalysisModule(){
		super(true);
		initialize();
	}
	
	private void initialize() {
		if(! ModuleConfig.instance().containsKey(SHOW_PLAYERCOMPARE))
			ModuleConfig.instance().setBoolean(SHOW_PLAYERCOMPARE, false);
		
		if(! ModuleConfig.instance().containsKey(SHOW_EXPERIENCE))
			ModuleConfig.instance().setBoolean(SHOW_EXPERIENCE, false);
		
	}

	@Override
	public int getModuleId() {
		return PLAYERANALYSIS;
	}

	@Override
	public String getDescription() {
		return HOVerwaltung.instance().getLanguageString("SpielerAnalyse");
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
