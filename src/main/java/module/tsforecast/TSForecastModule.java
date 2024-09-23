package module.tsforecast;

import core.db.DBManager;
import core.model.TranslationFacility;
import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

public final class TSForecastModule extends DefaultModule {

	@Override
	public int getModuleId() {
		return TSFORECAST;
	}

	@Override
	public String getDescription() {
		return TranslationFacility.tr("Tab_TSForecast");
	}

	@Override
	public JPanel createTabPanel() {
		return new TSForecast(DBManager.instance());
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_MASK);
	}
}
