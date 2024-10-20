package module.statistics;

import core.model.TranslationFacility;
import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

public final class StatisticsModule extends DefaultModule {

	public StatisticsModule(){
		super(true);
	}
	
	@Override
	public int getModuleId() {
		return STATISTICS;
	}

	@Override
	public String getDescription() {
		return TranslationFacility.tr("Statistik");
	}

	@Override
	public JPanel createTabPanel() {
		return new StatistikMainPanel();
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0);
	}

}
