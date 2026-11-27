package module.playeranalysis;

import core.module.DefaultModule;
import core.module.config.ModuleConfig;

import javax.swing.*;
import java.awt.event.KeyEvent;

public final class PlayerAnalysisModule extends DefaultModule {
	static final String SHOW_GAMEANALYSIS = "PA_PlayerAnalysis";
	private static final String SHOW_PLAYERCOMPARE = "PA_PlayerCompare";

	public PlayerAnalysisModule(){
		super(true);
	}

	public static boolean isShowPlayerCompare() {
        if (ModuleConfig.instance().containsKey(SHOW_PLAYERCOMPARE)) {
            return ModuleConfig.instance().getBoolean(SHOW_PLAYERCOMPARE);
        }
        return false;
    }

    public static void setShowPlayerCompare(boolean showPlayerCompare) {
        ModuleConfig.instance().setBoolean(SHOW_PLAYERCOMPARE, showPlayerCompare);
    }

	@Override
	public int getModuleId() {
		return PLAYERANALYSIS;
	}

	@Override
	public String getTranslationKey() {
		return "SpielerAnalyse";
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
