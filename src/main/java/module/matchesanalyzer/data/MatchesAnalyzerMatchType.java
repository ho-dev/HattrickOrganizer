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
	TOURNAMENT_PLAYOFF		(51,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.tournament_playoff")),
	SINGLE					(61,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.single")),
	LADDER					(62,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.ladder")),
	PREPARATION				(80,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.preparation")),
	EMERALDCUP				(1001,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.emerald_cup")),
	RUBYCUP					(1002,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.ruby_cup")),
	SAPPHIRECUP				(1003,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.sapphire_cup")),
	CONSOLANTECUP			(1004,	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.consolante_cup")),
	DIVISION_BATTLE         (1101, 	HOVerwaltung.instance().getLanguageString("ls.match.matchtype.division_battle"));

	private final int id;
	private final String name;

	MatchesAnalyzerMatchType(int id, String name) {
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
		int iconIndex = this.getIconArrayIndex();
		return ThemeManager.getIcon(HOIconName.MATCHICONS[iconIndex]);
	}

	private int getIconArrayIndex() {
		switch (this) {
			case LEAGUE:
				return 0;
			case QUALIFICATION:
				return 1;
			case FRIENDLY_NORMAL:
			case FRIENDLY_CUPRULES:
			case INT_FRIENDLY_NORMAL:
			case INT_FRIENDLY_CUPRULES:
				return 2;
			case CUP:
				return 3;
			case EMERALDCUP:
				return 4;
			case RUBYCUP:
				return 5;
			case SAPPHIRECUP:
				return 6;
			case LADDER:
				return 7;
			case TOURNAMENT_GROUP:
			case TOURNAMENT_PLAYOFF:
				return 8;
			case SINGLE:
				return 9;
			case HT_MASTERS:
				return 10;
			case CONSOLANTECUP:
				return 12;
			case DIVISION_BATTLE:
				return 13;
			default :
				return 11;
		}
	}

	public boolean isLeague() {
		if(this == LEAGUE) return true;
		if(this == QUALIFICATION) return true;
		return false;
	}

	public boolean isCup() {
		if(this == CUP) return true;
		if(this == EMERALDCUP) return true;
		if(this == RUBYCUP) return true;
		if(this == SAPPHIRECUP) return true;
		if(this == CONSOLANTECUP) return true;
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
		if(this == DIVISION_BATTLE) return true;
		if(this == INT_COMP_NORMAL) return true;
		if(this == LADDER) return true;
		if(this == SINGLE) return true;
		return false;
	}

	public boolean isNationaTeam() {
		if(this == NAT_COMP_NORMAL) return true;
		if(this == NAT_COMP_CUPRULES) return true;
		if(this == NAT_FRIENDLY) return true;
		return false;
	}
}
