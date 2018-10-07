package module.series;

import java.awt.event.KeyEvent;

import core.model.HOVerwaltung;
import core.module.DefaultModule;

import javax.swing.JPanel;
import javax.swing.KeyStroke;


public final class SeriesModule extends DefaultModule {

	public SeriesModule(){
		super(true);
	}
	
	@Override
	public int getModuleId() {
		return SERIES;
	}

	@Override
	public String getDescription() {
		return HOVerwaltung.instance().getLanguageString("Ligatabelle");
	}

	@Override
	public JPanel createTabPanel() {
		return new SeriesPanel();
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0);
	}
	
}
