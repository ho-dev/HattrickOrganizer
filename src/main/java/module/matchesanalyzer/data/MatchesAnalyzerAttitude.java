package module.matchesanalyzer.data;

import javax.swing.Icon;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;


public enum MatchesAnalyzerAttitude {
	PIC(ThemeManager.getIcon(HOIconName.PIC), HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.playitcool")),
	NORMAL(null, HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.normal")),
	MOTS(ThemeManager.getIcon(HOIconName.MOTS), HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.matchoftheseason")),
	UNKNOWN(null, null);

	private final Icon icon;
	private final String name;

	private MatchesAnalyzerAttitude(Icon icon, String name) {
		this.icon = icon;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Icon getIcon() {
		return icon;
	}

	public static MatchesAnalyzerAttitude toEnum(int id) {
		id = id + 1;
		if(id < 0 || id > 2) id = 3;
		return values()[id];
	}

}
