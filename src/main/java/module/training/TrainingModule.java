package module.training;

import core.model.TranslationFacility;
import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

public final class TrainingModule extends DefaultModule {

	public TrainingModule(){
		super(true);
	}
	
	@Override
	public int getModuleId() {
		return TRAINING;
	}

	@Override
	public String getDescription() {
		return TranslationFacility.tr("Training");
	}

	@Override
	public JPanel createTabPanel() {
		return new TrainingModulePanel();
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0);
	}

}
