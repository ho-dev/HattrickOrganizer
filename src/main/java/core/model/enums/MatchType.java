package core.model.enums;

import core.model.TranslationFacility;
import core.model.match.IMatchType;
import core.model.match.SourceSystem;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public enum MatchType implements IMatchType {

	NONE(0),
	LEAGUE(1),  // League match
	QUALIFICATION(2), //Qualification match
	CUP(3),  //Cup match (standard cup match)
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
	YOUTHINTERNATIONALFRIENDLYCUPRULES(106); // Youth international friendly match (Cup rules)


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

	public static MatchType getById(Integer id) {
		if ( id != null) {
			for (MatchType matchType : MatchType.values()) {
				if (matchType.getId() == id) {
					return matchType;
				}
			}
		}
		return null;
	}

	public String getSourceString() {
		if (isOfficial()) return "hattrick";
		if (isYouth()) return "youth";
		return "htointegrated";
	}

	private boolean isYouth() {
		return switch (this) {
			case YOUTHFRIENDLY, YOUTHFRIENDLYCUPRULES, YOUTHINTERNATIONALFRIENDLY, YOUTHINTERNATIONALFRIENDLYCUPRULES, YOUTHLEAGUE -> true;
			default -> false;
		};
	}

	public boolean isCupRules() {
		switch (this) {
			case CUP, FRIENDLYCUPRULES, INTFRIENDLYCUPRULES, LADDER, NATIONALCOMPCUPRULES, TOURNAMENTPLAYOFF -> {
				return true;
			}
			default -> {
				return false;
			}
		}
	}

	public static List<MatchType> getFriendlyMatchTypes() {
		return List.of(FRIENDLYNORMAL, FRIENDLYCUPRULES, INTFRIENDLYNORMAL, INTFRIENDLYCUPRULES);
	}

	public boolean isFriendly() {
		return MatchType.getFriendlyMatchTypes().contains(this);
	}

	public static List<MatchType> getCupMatchTypes() {
		return List.of(CUP);
	}

	/** Returns true for all normal matches.
	 *  Cup, League, friendlies, qualification, masters
	 *
	 * @return true if the match is official
	 */
	public boolean isOfficial() {
		return switch (this) {
			case LEAGUE, QUALIFICATION, CUP, FRIENDLYNORMAL, FRIENDLYCUPRULES, INTFRIENDLYNORMAL, INTFRIENDLYCUPRULES, MASTERS, NATIONALCOMPNORMAL, NATIONALCOMPCUPRULES, NATIONALFRIENDLY -> true;
			default -> false;
		};
	}

	public static List<MatchType> getOfficialMatchTypes() {
		if (cl_officialMatchType == null){
			cl_officialMatchType = MatchType.stream().filter(MatchType::isOfficial).collect(toList());
		}
		return cl_officialMatchType;
	}

	public static List<MatchType> getYouthMatchType() {
		if (cl_YouthMatchType == null){
			cl_YouthMatchType = MatchType.stream().filter(MatchType::isYouth).collect(toList());
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
        return switch (this) {
            case NATIONALCOMPNORMAL, NATIONALCOMPCUPRULES, NATIONALFRIENDLY -> true;
            default -> false;
        };
	}

	public static List<MatchType> getNTMatchType() {
		if (cl_NTMatchType == null){
			cl_NTMatchType = MatchType.stream().filter(MatchType::isNationalMatch).collect(toList());
		}
		return cl_NTMatchType;
	}

	public static List<MatchType> getTournamentMatchTypes() {
		return List.of(LADDER, TOURNAMENTGROUP, TOURNAMENTPLAYOFF);
	}

	public boolean isTournament() {
		return switch (this) {
			case LADDER, TOURNAMENTGROUP, TOURNAMENTPLAYOFF -> true;
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

    public SourceSystem getSourceSystem() {
		if (isOfficial()) return SourceSystem.HATTRICK;
		if (isYouth()) return SourceSystem.YOUTH;
		return SourceSystem.HTOINTEGRATED;
	}

	public static List<MatchType> fromSourceSystem(SourceSystem sourceSystem){
		switch (sourceSystem) {
			case HATTRICK -> {return getOfficialMatchTypes();}
			case YOUTH -> {return getYouthMatchType();}
			default -> { return getHTOintegratedMatchType(); }
		}
	}

	@Override
	public int getMatchTypeId() {
		return id;
	}

	@Override
	public int getIconArrayIndex() {
		return switch (this) {
			case LEAGUE -> 0;
			case QUALIFICATION -> 1;
			case FRIENDLYNORMAL, FRIENDLYCUPRULES, INTFRIENDLYNORMAL, INTFRIENDLYCUPRULES -> 2;
			case CUP -> 3;
			case LADDER -> 7;
			case TOURNAMENTGROUP, TOURNAMENTPLAYOFF -> 8;
			case SINGLE -> 9;
			case MASTERS -> 10;
			default -> 11;
		};
	}

	@Override
	public String getName() {
		return switch (this) {
			case LEAGUE -> TranslationFacility.tr("ls.match.matchtype.league");
			case CUP -> TranslationFacility.tr("ls.match.matchtype.cup");
			case QUALIFICATION -> TranslationFacility.tr("ls.match.matchtype.qualification");
			case NATIONALCOMPCUPRULES -> TranslationFacility.tr("ls.match.matchtype.nationalteamscompetition_cup");
			case MASTERS -> TranslationFacility.tr("ls.match.matchtype.hattrickmasters");
			case NATIONALCOMPNORMAL -> TranslationFacility.tr("ls.match.matchtype.nationalteamscompetition_normal");
			case INTSPIEL -> TranslationFacility.tr("ls.match.matchtype.internationalcompetition_normal");
			case INTFRIENDLYCUPRULES -> TranslationFacility.tr("ls.match.matchtype.internationalfriendly_cup");
			case INTFRIENDLYNORMAL -> TranslationFacility.tr("ls.match.matchtype.internationalfriendly_normal");
			case NATIONALFRIENDLY -> TranslationFacility.tr("ls.match.matchtype.nationalteamsfriendly");
			case FRIENDLYCUPRULES -> TranslationFacility.tr("ls.match.matchtype.friendly_cup");
			case FRIENDLYNORMAL -> TranslationFacility.tr("ls.match.matchtype.friendly_normal");
			case TOURNAMENTGROUP -> TranslationFacility.tr("ls.match.matchtype.tournament_group");
			case TOURNAMENTPLAYOFF -> TranslationFacility.tr("ls.match.matchtype.tournament_playoff");
			case SINGLE -> TranslationFacility.tr("ls.match.matchtype.single");
			case LADDER -> TranslationFacility.tr("ls.match.matchtype.ladder");
			default -> "unknown";
		};
	}
}
