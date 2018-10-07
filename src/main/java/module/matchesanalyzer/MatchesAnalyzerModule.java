package module.matchesanalyzer;

import core.model.HOVerwaltung;
import core.module.DefaultModule;
import module.matchesanalyzer.data.MatchesAnalyzerTeam;
import module.matchesanalyzer.ui.MatchesAnalyzerPanel;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.KeyStroke;


public final class MatchesAnalyzerModule extends DefaultModule {

	private static final String DESCRIPTION = HOVerwaltung.instance().getLanguageString("matchesanalyzer.name");

	public MatchesAnalyzerModule() {
		super(true);
		setStartup(true);
	}

	@Override
	public int getModuleId() {
		return MATCHESANALYZER;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public JPanel createTabPanel() {
		MatchesAnalyzerTeam team = new MatchesAnalyzerTeam();
		return new MatchesAnalyzerPanel(team);
	}

	@Override
	public boolean hasConfigPanel() {
		return false;
	}

	@Override
	public JPanel createConfigPanel() {
		return null;
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.CTRL_MASK);
	}

	@Override
	public boolean hasMenu() {
		return false;
	}

	@Override
	public JMenu getMenu() {
		return null;
	}

}
