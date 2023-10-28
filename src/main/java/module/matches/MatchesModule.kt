package module.matches;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.KeyStroke;


import core.model.HOVerwaltung;
import core.module.DefaultModule;

public final class MatchesModule extends DefaultModule {

	public MatchesModule(){
		super(true);
	}

	@Override
	public int getModuleId() {
		return MATCHES;
	}

	@Override
	public String getDescription() {
		return HOVerwaltung.instance().getLanguageString("Tab_Title_Matches");
	}

	@Override
	public JPanel createTabPanel() {
		return new MatchesPanel();
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0);
	}

}
