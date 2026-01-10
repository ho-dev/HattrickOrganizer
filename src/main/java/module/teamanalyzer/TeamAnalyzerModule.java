package module.teamanalyzer;

import java.awt.event.KeyEvent;

import core.model.TranslationFacility;
import core.module.DefaultModule;
import module.teamanalyzer.ui.TeamAnalyzerPanel;
import module.teamanalyzer.ui.component.SettingPanel;
import module.teamanalyzer.ui.component.TAMenu;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.KeyStroke;


public final class TeamAnalyzerModule extends DefaultModule {

	private TeamAnalyzerPanel teamAnalyzerPanel = null;

	public TeamAnalyzerModule(){
		super(true);
	}
	
	@Override
	public int getModuleId() {
		return TEAMANALYZER;
	}

	@Override
	public String getDescription() {
		return TranslationFacility.tr("TeamAnalyzer");
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
