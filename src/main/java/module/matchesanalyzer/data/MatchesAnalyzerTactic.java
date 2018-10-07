package module.matchesanalyzer.data;

import core.model.HOVerwaltung;


public enum MatchesAnalyzerTactic {
    NORMAL					(HOVerwaltung.instance().getLanguageString("ls.team.tactic.normal")),
    PRESSING				(HOVerwaltung.instance().getLanguageString("ls.team.tactic.pressing")),
    COUNTER_ATTACKS			(HOVerwaltung.instance().getLanguageString("ls.team.tactic.counter-attacks")),
    ATTACK_IN_THE_MIDDLE	(HOVerwaltung.instance().getLanguageString("ls.team.tactic.attackinthemiddle")),
    ATTACK_ON_WINGS			(HOVerwaltung.instance().getLanguageString("ls.team.tactic.attackonwings")),
    NOT_IN_USE_5 			(null),
    NOT_IN_USE_6			(null),
    PLAY_CREATIVELY			(HOVerwaltung.instance().getLanguageString("ls.team.tactic.playcreatively")),
    LONG_SHOTS				(HOVerwaltung.instance().getLanguageString("ls.team.tactic.longshots"));

	private final String name;

	private MatchesAnalyzerTactic(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
