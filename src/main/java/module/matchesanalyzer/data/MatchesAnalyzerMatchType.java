package module.matchesanalyzer.data;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

import javax.swing.Icon;


public enum MatchesAnalyzerMatchType {
	NONE					(0,		null),
	LEAGUE					(1,		HOVerwaltung.instance().getLanguageString("ls.match.matchtype.league")),
	QUALIFICATION			(2,		HOVerwaltung.instance().getLanguageString("ls.match.matchtype.qualification")),
	CUP						(3,		HOVerwaltung.instance().getLanguageString("ls.match.matchtype.cup")),
	FRIENDLY_NORMAL			(4,		HOVerwaltung.instance().getLanguageString("ls.match.matchtype.friendly_normal")),
	FRIENDLY_CUPRULES		(5,		HOVerwaltung.instance().getLanguageString("ls.match.matchtype.friendly_cup")),
	INT_COMP_NORMAL			(6,		HOVerwaltung.instance().getLanguageString("ls.match.matchtype.internationalcompetition_normal")),
	HT_MASTERS				(7,		HOVerwaltung.instance().getLanguageString("ls.match.matchtype.hattrickmasters")),
	INT_FRIENDLY_NORMAL		(8,		HOVerwaltung.instance().getLanguageString("ls.match.matchtype.internationalfriendly_normal")),
	INT_FRIENDLY_CUPRULES	(9,		HOVerwaltung.instance().getLanguageString("ls.match.matchtype.internationalfriendly_cup")),
	NAT_COMP_NORMAL			(10,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.nationalteamscompetition_normal")),
	NAT_COMP_CUPRULES		(11,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.nationalteamscompetition_cup")),
	NAT_FRIENDLY			(12,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.nationalteamsfriendly")),
	TOURNAMENT_GROUP		(50,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.tournament_group")),
	TOURNAMENT_PLAYOFF		(51,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.tournament_playoff"));

	private final int id;
	private final String name;

	private MatchesAnalyzerMatchType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Icon getIcon() {
		return ThemeManager.getIcon(HOIconName.MATCHTYPES[ordinal()]);
	}

	public boolean isLeague() {
		if(this == LEAGUE) return true;
		if(this == QUALIFICATION) return true;
		return false;
	}

	public boolean isCup() {
		if(this == CUP) return true;
		if(this == HT_MASTERS) return true;
		return false;
	}

	public boolean isFriendly() {
		if(this == FRIENDLY_NORMAL) return true;
		if(this == FRIENDLY_CUPRULES) return true;
		if(this == INT_FRIENDLY_NORMAL) return true;
		if(this == INT_FRIENDLY_CUPRULES) return true;

		return false;
	}

	public boolean isUnofficial() {
		if(this == TOURNAMENT_GROUP) return true;
		if(this == TOURNAMENT_PLAYOFF) return true;
		if(this == INT_COMP_NORMAL) return true;
		return false;
	}

	public boolean isNationaTeam() {
		if(this == NAT_COMP_NORMAL) return true;
		if(this == NAT_COMP_CUPRULES) return true;
		if(this == NAT_FRIENDLY) return true;
		return false;
	}
}
