package module.matchesanalyzer.data;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

import javax.swing.Icon;


public enum MatchesAnalyzerWeather {
	RAIN				(HOVerwaltung.instance().getLanguageString("matchesanalyzer.weather.rain")),
	OVERCAST			(HOVerwaltung.instance().getLanguageString("matchesanalyzer.weather.overcast")),
	PARTIALLY_CLOUDY	(HOVerwaltung.instance().getLanguageString("matchesanalyzer.weather.partially_cloudy")),
	SUNNY				(HOVerwaltung.instance().getLanguageString("matchesanalyzer.weather.sunny"));

	private final String name;

	private MatchesAnalyzerWeather(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Icon getIcon() {
		return ThemeManager.getIcon(HOIconName.WEATHER[ordinal()]);
	}
}
