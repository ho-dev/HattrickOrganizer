//package module.opponentspy;
//
//
//
//import core.model.HOVerwaltung;
//import core.module.DefaultModule;
//
//import java.awt.event.InputEvent;
//import java.awt.event.KeyEvent;
//
//import javax.swing.JPanel;
//import javax.swing.KeyStroke;
//
//public final class OpponentModule extends DefaultModule {
//
//
//	public OpponentModule() {
//		super(true);
//		setStartup(true);
//	}
//
//	@Override
//	public int getModuleId() {
//		return OPPONENTSPY;
//	}
//
//	@Override
//	public String getDescription() {
//		return HOVerwaltung.instance().getLanguageString("opponentspy");
//	}
//
//	@Override
//	public JPanel createTabPanel() {
//		return new OpponentPanel();
//
//	}
//
//	@Override
//	public KeyStroke getKeyStroke() {
//		return KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK);
//	}
//
//}