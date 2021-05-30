package core.model.match;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public enum MatchType {

	NONE(0),
	LEAGUE(1),  // League match
	QUALIFICATION(2), //Qualification match
	CUP(3),  //Cup match (standard league match)
	FRIENDLYNORMAL(4), //	Friendly (normal rules)
	FRIENDLYCUPRULES(5), //Friendly (cup rules)
	INTSPIEL(6), // Not currently in use, but reserved for international competition matches with normal rules (may or may not be implemented at some future point).
	MASTERS(7), // Hattrick Masters
	INTFRIENDLYNORMAL(8), //	International friendly (normal rules)
	INTFRIENDLYCUPRULES(9), // International friendly (cup rules)
	NATIONALCOMPNORMAL(10), // National teams competition match (normal rules)
	NATIONALCOMPCUPRULES(11), // 	National teams competition match (cup rules)
	NATIONALFRIENDLY(12), // 	National teams friendly
	TOURNAMENTGROUP(50), // Tournament League match
	TOURNAMENTPLAYOFF(51), // Tournament Playoff match
	SINGLE(61), // Single match
	LADDER(62), // Ladder match
	PREPARATION(80), // Preparation match
	YOUTHLEAGUE(100), //	Youth league match
	YOUTHFRIENDLY(101), //	Youth friendly match
	YOUTHFRIENDLYCUPRULES(103), //	Youth friendly match (cup rules)
	YOUTHINTERNATIONALFRIENDLY(105), // Youth international friendly match
	YOUTHINTERNATIONALFRIENDLYCUPRULES(106), // Youth international friendly match (Cup rules)
	EMERALDCUP(1001), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	RUBYCUP(1002), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	SAPPHIRECUP(1003), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	CONSOLANTECUP(1004), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	DIVISIONBATTLE(1101), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	GROUP_OFFICIAL(9990); // Supposed to replace constants declared in SpielePanel



	private final int id;

	private static List<MatchType> cl_officialMatchType, cl_NTMatchType, cl_YouthMatchType, cl_HTOintegratedMatchType;

	MatchType(int id) {
		this.id = id;
	}


	public int getId() {
		return id;
	}


	public static Stream<MatchType> stream() {
		return Stream.of(MatchType.values());
	}

	public static MatchType getById(int id) {
		for (MatchType matchType : MatchType.values()) {
			if (matchType.getId() == id) {
				return matchType;
			}
		}
		return null;
	}

	public static MatchType getById(int id, int CupLevel, int CupLevelIndex, int iTournamentType) {
		MatchType matchType = getById(id);
		if (matchType == CUP) {
			if (CupLevel == 1) {
				return CUP;
			} else if (CupLevel == 3) {
				return CONSOLANTECUP;
			} else {
				switch (CupLevelIndex) {
					case 1: {
						return EMERALDCUP;
					}
					case 2: {
						return RUBYCUP;
					}
					case 3: {
						return SAPPHIRECUP;
					}
					default: {
						return NONE;
					}
				}
			}
		}
		else if (matchType == TOURNAMENTGROUP)
		{
			TournamentType tournamentType = TournamentType.getById(iTournamentType);
			if (tournamentType == TournamentType.DIVISIONBATTLE)
			{
				return DIVISIONBATTLE;
			}
		}
		return matchType;
	}

	public String getSourceString() {
		if (isOfficial()) return "hattrick";
		if (isYouth()) return "youth";
		return "htointegrated";
	}

	private boolean isYouth() {
		switch (this){
			case YOUTHFRIENDLY:
			case YOUTHFRIENDLYCUPRULES:
			case YOUTHINTERNATIONALFRIENDLY:
			case YOUTHINTERNATIONALFRIENDLYCUPRULES:
			case YOUTHLEAGUE:
				return true;
		}
		return false;
	}

	public boolean isCupRules() {
		switch (this) {
			case CUP :
			case FRIENDLYCUPRULES :
			case INTFRIENDLYCUPRULES :
			case LADDER:
			case NATIONALCOMPCUPRULES :
			case TOURNAMENTPLAYOFF : {
				return true;
			}
			default: {
				return false;
			}
		}
	}

	public boolean isFriendly() {
		switch (this) {
			case FRIENDLYNORMAL :
			case FRIENDLYCUPRULES :
			case INTFRIENDLYNORMAL :
			case INTFRIENDLYCUPRULES : {
				return true;
			}
			default : {
				return false;
			}
		}
	}

	/** Returns true for all normal matches.
	 *  Cup, League, friendlies, qualification, masters
	 *
	 * @return true if the match is official
	 */
	public boolean isOfficial() {
		switch (this) {
			case LEAGUE :
			case QUALIFICATION :
			case EMERALDCUP :
			case RUBYCUP :
			case SAPPHIRECUP :
			case CONSOLANTECUP :
			case CUP :
			case FRIENDLYNORMAL:
			case FRIENDLYCUPRULES :
			case INTFRIENDLYNORMAL :
			case INTFRIENDLYCUPRULES :
			case MASTERS :
			case NATIONALCOMPNORMAL:
			case NATIONALCOMPCUPRULES:
			case NATIONALFRIENDLY:
				return true;
			default:
				return false;
		}
	}


	public static List<MatchType> getOfficialMatchType() {
		if (cl_officialMatchType == null){
			cl_officialMatchType = MatchType.stream().filter(m -> m.isOfficial()).collect(toList());
		}
		return cl_officialMatchType;
	}

	public static List<MatchType> getYouthMatchType() {
		if (cl_YouthMatchType == null){
			cl_YouthMatchType = MatchType.stream().filter(m -> m.isYouth()).collect(toList());
		}
		return cl_YouthMatchType;
	}

	public static List<MatchType> getHTOintegratedMatchType() {
		if (cl_HTOintegratedMatchType == null){
			cl_HTOintegratedMatchType = MatchType.stream().filter(m -> !(m.isYouth() || m.isOfficial())).collect(toList());
		}
		return cl_HTOintegratedMatchType;
	}


	/**
	 * Returns true for all NT matches.
	 */
	public boolean isNationalMatch() {
		switch (this) {
			case NATIONALCOMPNORMAL :
			case NATIONALCOMPCUPRULES :
			case NATIONALFRIENDLY : {
				return true;
			}
			default:
				return false;
		}
	}


	public static List<MatchType> getNTMatchType() {
		if (cl_NTMatchType == null){
			cl_NTMatchType = MatchType.stream().filter(m -> m.isNationalMatch()).collect(toList());
		}
		return cl_NTMatchType;
	}


	public boolean isTournament() {
		return switch (this) {
			case LADDER, TOURNAMENTGROUP, TOURNAMENTPLAYOFF, DIVISIONBATTLE -> true;
			default -> false;
		};
	}

	//team attitude can be set only for competitive match
	public boolean isCompetitive() {
		return switch (this) {
			case LEAGUE, QUALIFICATION, CUP, MASTERS -> true;
			default -> false;
		};
	}

	public String getName() {
		return switch (this) {
			case LEAGUE -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.league");
			case CUP -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.cup");
			case QUALIFICATION -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.qualification");
			case NATIONALCOMPCUPRULES -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.nationalteamscompetition_cup");
			case MASTERS -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.hattrickmasters");
			case NATIONALCOMPNORMAL -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.nationalteamscompetition_normal");
			case INTSPIEL -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.internationalcompetition_normal");
			case INTFRIENDLYCUPRULES -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.internationalfriendly_cup");
			case INTFRIENDLYNORMAL -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.internationalfriendly_normal");
			case NATIONALFRIENDLY -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.nationalteamsfriendly");
			case FRIENDLYCUPRULES -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.friendly_cup");
			case FRIENDLYNORMAL -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.friendly_normal");
			case TOURNAMENTGROUP -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.tournament_group");
			case TOURNAMENTPLAYOFF -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.tournament_playoff");
			case SINGLE -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.single");
			case LADDER -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.ladder");
			case EMERALDCUP -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.emerald_cup");
			case RUBYCUP -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.ruby_cup");
			case SAPPHIRECUP -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.sapphire_cup");
			case CONSOLANTECUP -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.consolante_cup");
			case DIVISIONBATTLE -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.division_battle");
			default -> "unknown";
		};
	}

	public int getIconArrayIndex() {
		return switch (this) {
			case LEAGUE -> 0;
			case QUALIFICATION -> 1;
			case FRIENDLYNORMAL, FRIENDLYCUPRULES, INTFRIENDLYNORMAL, INTFRIENDLYCUPRULES -> 2;
			case CUP -> 3;
			case EMERALDCUP -> 4;
			case RUBYCUP -> 5;
			case SAPPHIRECUP -> 6;
			case LADDER -> 7;
			case TOURNAMENTGROUP, TOURNAMENTPLAYOFF -> 8;
			case SINGLE -> 9;
			case MASTERS -> 10;
			case CONSOLANTECUP -> 12;
			case DIVISIONBATTLE -> 13;
			default -> 11;
		};
	}

    public SourceSystem getSourceSystem() {
		if (isOfficial()) return SourceSystem.HATTRICK;
		if (isYouth()) return SourceSystem.YOUTH;
		return SourceSystem.HTOINTEGRATED;
	}

	public static List<MatchType> fromSourceSystem(SourceSystem sourceSystem){
		switch (sourceSystem) {
			case HATTRICK -> {return getOfficialMatchType();}
			case YOUTH -> {return getYouthMatchType();}
			default -> { return getHTOintegratedMatchType(); }
		}
	}
}
