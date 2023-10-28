package module.training;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.KeyStroke;


import core.model.HOVerwaltung;
import core.module.DefaultModule;

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
		return HOVerwaltung.instance().getLanguageString("Training");
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
