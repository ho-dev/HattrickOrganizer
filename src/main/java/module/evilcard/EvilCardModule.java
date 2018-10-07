package module.evilcard;

import core.model.HOVerwaltung;
import core.module.DefaultModule;
import module.evilcard.gui.EvilCardPanel;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class EvilCardModule extends DefaultModule {

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_4, KeyEvent.CTRL_MASK);
	}

	@Override
	public int getModuleId() {
		return EVIL_CARD;
	}

	@Override
	public String getDescription() {
		return HOVerwaltung.instance().getLanguageString("Tab_Evilcard");
	}

	@Override
	public JPanel createTabPanel() {
		return new EvilCardPanel();
	}

}
