package module.matches;

import core.model.TranslationFacility;
import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

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
		return TranslationFacility.tr("Tab_Title_Matches");
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
