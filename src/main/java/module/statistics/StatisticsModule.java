package module.statistics;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.KeyStroke;


import core.model.HOVerwaltung;
import core.module.DefaultModule;

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
		return HOVerwaltung.instance().getLanguageString("Statistik");
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
