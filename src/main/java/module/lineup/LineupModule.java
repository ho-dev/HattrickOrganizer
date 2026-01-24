package module.lineup;

import core.model.TranslationFacility;
import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

public final class LineupModule extends DefaultModule {

	private LineupMasterView lineupMasterView;

	public LineupModule() {
		super(true);
	}

	@Override
	public int getModuleId() {
		return LINEUP;
	}

	@Override
	public String getDescription() {
		return TranslationFacility.tr("Aufstellung");
	}

	@Override
	public JPanel createTabPanel() {
		this.lineupMasterView = new LineupMasterView();
		return this.lineupMasterView;
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
	}

	@Override
	public void storeUserSettings() {
		if (this.lineupMasterView != null) {
            this.lineupMasterView.storeUserSettings();
        }
	}
}
