package module.matchesanalyzer.data;

import javax.swing.Icon;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

public enum MatchesAnalyzerVenue {
	HOME(HOVerwaltung.instance().getLanguageString("Heim"), ThemeManager.getIcon(HOIconName.HOME)),
	AWAY(HOVerwaltung.instance().getLanguageString("Gast"), ThemeManager.getIcon(HOIconName.AWAY));

	private String name;
	private Icon icon;
	
	private MatchesAnalyzerVenue(String name, Icon icon) {
		this.name = name;
		this.icon = icon;
	}
	
	public String getName() {
		return name;
	}
	
	public Icon getIcon() {
		return icon;
	}
	
}
