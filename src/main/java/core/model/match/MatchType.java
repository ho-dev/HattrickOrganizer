package core.model.match;


public enum MatchType {

	NONE((int) 0),
	LEAGUE((int) 1),
	QUALIFICATION((int) 2),
	CUP((int) 3),
	FRIENDLYNORMAL((int) 4),
	FRIENDLYCUPRULES((int) 5),
	INTSPIEL((int) 6),
	MASTERS((int) 7),
	INTFRIENDLYNORMAL((int) 8),
	INTFRIENDLYCUPRULES((int) 9),
	NATIONALCOMPNORMAL((int) 10),
	NATIONALCOMPCUPRULES((int) 11),
	NATIONALFRIENDLY((int) 12),
	TOURNAMENTGROUP((int) 50),
	TOURNAMENTPLAYOFF((int) 51);

	private final int id;

	private MatchType(int id) {
		this.id = id;
	}


	public int getId() {
		return id;
	}


	public static MatchType getById(int id) {
		for (MatchType matchType : MatchType.values()) {
			if (matchType.getId() == id) {
				return matchType;
			}
		}
		return null;
	}

	public String getSourceString() {
		switch (this) {
			case TOURNAMENTGROUP :
			case TOURNAMENTPLAYOFF : {
				return "htointegrated";
			}
			default: {
				return "hattrick";
			}
		}
	}

	public boolean isCupRules() {
		switch (this) {
			case CUP :
			case FRIENDLYCUPRULES :
			case INTFRIENDLYCUPRULES :
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
			case CUP :
			case FRIENDLYNORMAL :
			case FRIENDLYCUPRULES :
			case INTFRIENDLYNORMAL :
			case INTFRIENDLYCUPRULES :
			case MASTERS : {
				return true;
			}
			default:
				return false;
		}
	}

	public boolean isTournament() {
		switch (this) {
			case TOURNAMENTGROUP:
			case TOURNAMENTPLAYOFF:
				return true;
			default: return false;
		}
	}


	public String getName() {
		 switch (this) {
	         case LEAGUE:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.league");

	         case CUP:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.cup");

	         case QUALIFICATION:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.qualification");

	         case NATIONALCOMPCUPRULES:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.nationalteamscompetition_cup");

	         case MASTERS:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.hattrickmasters");

	         case NATIONALCOMPNORMAL:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.nationalteamscompetition_normal");

	         case INTSPIEL:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.internationalcompetition_normal");

	         case INTFRIENDLYCUPRULES:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.internationalfriendly_cup");

	         case INTFRIENDLYNORMAL:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.internationalfriendly_normal");

	         case NATIONALFRIENDLY:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.nationalteamsfriendly");

	         case FRIENDLYCUPRULES:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.friendly_cup");

	         case FRIENDLYNORMAL:
	             return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.friendly_normal");

	         case TOURNAMENTGROUP:
	         	 return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.tournament_group");

	         case TOURNAMENTPLAYOFF :
	         	 return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.tournament_playoff");

	         //Error?
	         default:
	             return "unknown";

		 }
	}

	public int getIconArrayIndex() {
		switch (this) {
			case TOURNAMENTGROUP :
				return 13;
			case TOURNAMENTPLAYOFF :
				return 14;
			default :
				return id;
		}
	}
}
