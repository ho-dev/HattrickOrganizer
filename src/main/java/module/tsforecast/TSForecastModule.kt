package module.tsforecast;

import java.awt.event.KeyEvent;

import core.model.HOVerwaltung;
import core.module.DefaultModule;

import javax.swing.JPanel;
import javax.swing.KeyStroke;

public final class TSForecastModule extends DefaultModule {

	@Override
	public int getModuleId() {
		return TSFORECAST;
	}

	@Override
	public String getDescription() {
		return HOVerwaltung.instance().getLanguageString("Tab_TSForecast");
	}

	@Override
	public JPanel createTabPanel() {
		return new TSForecast();
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_MASK);
	}
}
