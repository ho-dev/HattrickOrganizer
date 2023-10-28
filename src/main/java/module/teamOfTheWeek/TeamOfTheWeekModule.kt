package module.teamOfTheWeek;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.KeyStroke;


import core.model.HOVerwaltung;
import core.module.DefaultModule;
import module.teamOfTheWeek.gui.TeamOfTheWeekPanel;

public class TeamOfTheWeekModule extends DefaultModule {

	@Override
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.CTRL_MASK);
	}

	@Override
	public int getModuleId() {
		return TEAM_OF_THE_WEEK;
	}

	@Override
	public String getDescription() {
		return HOVerwaltung.instance().getLanguageString("Tab_TeamOfTheWeek");
	}

	@Override
	public JPanel createTabPanel() {
		return new TeamOfTheWeekPanel();
	}

}
