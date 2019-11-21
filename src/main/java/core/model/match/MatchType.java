package core.model.match;


public enum MatchType {

	NONE((int) 0),
	LEAGUE((int) 1),  // League match
	QUALIFICATION((int) 2), //Qualification match
	CUP((int) 3),  //Cup match (standard league match)
	FRIENDLYNORMAL((int) 4), //	Friendly (normal rules)
	FRIENDLYCUPRULES((int) 5), //Friendly (cup rules)
	INTSPIEL((int) 6), // Not currently in use, but reserved for international competition matches with normal rules (may or may not be implemented at some future point).
	MASTERS((int) 7), // Hattrick Masters
	INTFRIENDLYNORMAL((int) 8), //	International friendly (normal rules)
	INTFRIENDLYCUPRULES((int) 9), // International friendly (cup rules)
	NATIONALCOMPNORMAL((int) 10), // National teams competition match (normal rules)
	NATIONALCOMPCUPRULES((int) 11), // 	National teams competition match (cup rules)
	NATIONALFRIENDLY((int) 12), // 	National teams friendly
	TOURNAMENTGROUP((int) 50), // Tournament League match
	TOURNAMENTPLAYOFF((int) 51), // Tournament Playoff match
	SINGLE((int) 61), // Single match
	LADDER((int) 62), // Ladder match
	PREPARATION((int) 80), // Preparation match
	EMERALDCUP(1001), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	RUBYCUP(1002), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	SAPPHIRECUP(1003), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	CONSOLANTECUP(1004); // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure



	private final int id;

	MatchType(int id) {
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

	public static MatchType getById(int id, int CupLevel, int CupLevelIndex) {
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
		return matchType;
	}

	public String getSourceString() {
		switch (this) {
			case SINGLE:
			case LADDER:
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
			case LADDER:
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

			 case SINGLE:
				 return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.single");

			 case LADDER:
					 return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.ladder");

			 case EMERALDCUP :
				 return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.emerald_cup");

			 case RUBYCUP :
				 return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.ruby_cup");

			 case SAPPHIRECUP :
				 return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.sapphire_cup");

			 case CONSOLANTECUP :
				 return core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.consolante_cup");

	         //Error?
	         default:
	             return "unknown";

		 }
	}

	public int getIconArrayIndex() {
		switch (this) {
			case LEAGUE:
				return 0;
			case QUALIFICATION:
				return 1;
			case FRIENDLYNORMAL:
			case FRIENDLYCUPRULES:
			case INTFRIENDLYNORMAL:
			case INTFRIENDLYCUPRULES:
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
			case TOURNAMENTGROUP:
			case TOURNAMENTPLAYOFF:
				return 8;
			case SINGLE:
				return 9;
			case MASTERS:
				return 10;
			case CONSOLANTECUP:
				return 12;
			default :
				return 11;
		}
	}

}
