package module.teamoftheweek;

import core.model.TranslationFacility;
import core.module.DefaultModule;
import module.teamoftheweek.gui.TeamOfTheWeekPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;

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
		return TranslationFacility.tr("Tab_TeamOfTheWeek");
	}

	@Override
	public JPanel createTabPanel() {
		return new TeamOfTheWeekPanel();
	}

}
