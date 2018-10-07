package module.transfer;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.KeyStroke;


import core.model.HOVerwaltung;
import core.module.DefaultModule;

public final class TransfersModule extends DefaultModule {

	public TransfersModule(){
		super(true);
	}
	
	@Override
	public int getModuleId() {
		return TRANSFERS;
	}

	@Override
	public String getDescription() {
		return HOVerwaltung.instance().getLanguageString("Transfers");
	}

	@Override
	public JPanel createTabPanel() {		
		return new TransfersPanel();
	}

	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0);
	}

}
