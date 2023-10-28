package module.teamAnalyzer;

import java.awt.event.KeyEvent;

import core.model.HOVerwaltung;
import core.module.DefaultModule;
import module.teamAnalyzer.ui.TeamAnalyzerPanel;
import module.teamAnalyzer.ui.component.SettingPanel;
import module.teamAnalyzer.ui.component.TAMenu;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.KeyStroke;


public final class TeamAnalyzerModule extends DefaultModule {

	private TeamAnalyzerPanel teamAnalyzerPanel=null;

	public TeamAnalyzerModule(){
		super(true);
	}
	
	@Override
	public int getModuleId() {
		return TEAMANALYZER;
	}

	@Override
	public String getDescription() {
		return HOVerwaltung.instance().getLanguageString("TeamAnalyzer");
	}

	@Override
	public JPanel createTabPanel() {
		teamAnalyzerPanel = new TeamAnalyzerPanel();
		return teamAnalyzerPanel;
	}

	@Override
	public boolean hasConfigPanel() {
		return true;
	}

	@Override
	public JPanel createConfigPanel() {
		return new SettingPanel();
	}

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0);
	}
	
	@Override
	public boolean hasMenu(){
		return true;
	}
	@Override
	public JMenu getMenu(){
		return new TAMenu();
	}

	@Override
	public void storeUserSettings(){
		if ( teamAnalyzerPanel != null){
			teamAnalyzerPanel.storeUserSettings();
		}
	}
}
