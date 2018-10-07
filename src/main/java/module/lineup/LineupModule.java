package module.lineup;

import core.model.HOVerwaltung;
import core.module.DefaultModule;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.KeyStroke;

public final class LineupModule extends DefaultModule {

	public LineupModule() {
		super(true);
	}

	@Override
	public int getModuleId() {
		return LINEUP;
	}

	@Override
	public String getDescription() {
		return HOVerwaltung.instance().getLanguageString("Aufstellung");
	}

	@Override
	public JPanel createTabPanel() {
		return new LineupMasterView();
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
	}

}
