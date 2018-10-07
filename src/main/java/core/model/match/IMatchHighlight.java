// %3292955215:plugins%
/*
 * IMatchHighlight.java
 *
 * Created on 6. September 2004, 09:04
 */
package core.model.match;

/**
 * Const
 *
 * @author volker.fischer
 */
public interface IMatchHighlight {
    //~ Static fields/initializers -----------------------------------------------------------------

	/*************************************************************************
	 * Highlight main types 
	 *************************************************************************/

    /** Information */
    public static final int HIGHLIGHT_INFORMATION = 0;

    /** Success -> Goal */
    public static final int HIGHLIGHT_ERFOLGREICH = 1;

    /** Failure -> No Goal */
    public static final int HIGHLIGHT_FEHLGESCHLAGEN = 2;

    /** Special */
    public static final int HIGHLIGHT_SPEZIAL = 3;

    /** Cards */
    public static final int HIGHLIGHT_KARTEN = 5;

    
	/*************************************************************************
	 * Highlight sub types 
	 *************************************************************************/

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * Informal events (HIGHLIGHT_INFORMATION only) 
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /** formation, e.g. 3-4-3 */
    public static final int HIGHLIGHT_SUB_FORMATION = 20;

    /** List of players */
    public static final int HIGHLIGHT_SUB_SPIELER_AUFLISTUNG = 21;

    /** Its a Derby (not in friendlies) */
    public static final int HIGHLIGHT_SUB_DERBY = 25;

    /** Neutral Ground (not in friendlies) */
    public static final int HIGHLIGHT_SUB_NEUTRAL_GROUND= 26;

    /** Rain */
    public static final int HIGHLIGHT_SUB_REGEN = 30;

    /** Clouded */
    public static final int HIGHLIGHT_SUB_BEWOELKT = 31;

    /** Fine */
    public static final int HIGHLIGHT_SUB_SCHOEN = 32;

    /** Sunny */
    public static final int HIGHLIGHT_SUB_SONNIG = 33;

    /** Ball possession */
    public static final int HIGHLIGHT_SUB_BALLBESITZ = 40;

    /** Best Player */
    public static final int HIGHLIGHT_SUB_BESTER_SPIELER = 41;

    /** Worst Player */
    public static final int HIGHLIGHT_SUB_SCHLECHTESTER_SPIELER = 42;

    /** Halftime ;) */
    public static final int HIGHLIGHT_SUB_HALBZEIT = 45;

    /** Hattrick */
    public static final int HIGHLIGHT_SUB_HATTRICK = 46;

    /** Equal ball possession */
    public static final int HIGHLIGHT_SUB_BALLBESITZ_AUSGEGLICHEN = 47;

    /** 	Penalty contest: Goal by Technical (no nerves) */
    public static final int HIGHLIGHT_SUB_PENALTY_TOR_TECH_NO_NERVES = 55;

    /** Penalty Goal */
    public static final int HIGHLIGHT_SUB_PENALTY_TOR = 56;

    /** Penalty nervous Goal */
    public static final int HIGHLIGHT_SUB_PENALTY_NERVOES_TOR = 57;

    /** Penalty nervouse no Goal */
    public static final int HIGHLIGHT_SUB_PENALTY_NERVOES_KEIN_TOR = 58;

    /** Penalty Keeper get it */
    public static final int HIGHLIGHT_SUB_PENALTY_TORHUETER_HAELT = 59;

    /** Underestimated */
    public static final int HIGHLIGHT_SUB_UNTERSCHAETZT = 60;

    /** tactical problems */
    public static final int HIGHLIGHT_SUB_TAKTISCHE_PROBLEME = 61;

    /** defend lead / pull back */
    public static final int HIGHLIGHT_SUB_FUEHRUNG_HALTEN = 62;

    /** more concentrated */
    public static final int HIGHLIGHT_SUB_KONZENTRIERTER = 63;

    /** better organized */
    public static final int HIGHLIGHT_SUB_BESSER_ORGANISIERT = 64;

    /** missing experience */
    public static final int HIGHLIGHT_SUB_FEHLENDE_ERFAHRUNG = 65;

    /** 	Remove underestimation at pause (goaldiff = 0)  */
    public static final int HIGHLIGHT_SUB_FEHLENDE_ERFAHRUNG_REMOVE_NODIFF = 66;
    
    /** 	Remove underestimation at pause (goaldiff = 1)  */
    public static final int HIGHLIGHT_SUB_FEHLENDE_ERFAHRUNG_REMOVE_ONEDIFF = 67;
    
    /** pressing successful */
    public static final int HIGHLIGHT_SUB_PRESSING_ERFOLGREICH = 68;
    
    /** 	Remove underestimation */
    public static final int HIGHLIGHT_SUB_FEHLENDE_ERFAHRUNG_REMOVE = 69;
    
    /** overtime */
    public static final int HIGHLIGHT_SUB_VERLAENGERUNG = 70;

    /** penalty shootout */
    public static final int HIGHLIGHT_SUB_ELFMETERSCHIESSEN = 71;

    /** won in overtime */
    public static final int HIGHLIGHT_SUB_SIEG_IN_VERLAENGERUNG = 72;
    
    /** won in overtime */
    public static final int HIGHLIGHT_SUB_KEIN_SIEG_NACH_ELFMETER_SCHIESSES_COIN = 73;

    /** 	New captain  */
    public static final int HIGHLIGHT_SUB_NEW_CAPTAIN = 80;

    /** bruised */
    public static final int HIGHLIGHT_SUB_PFLASTER = 90;

    /** light injured */
    public static final int HIGHLIGHT_SUB_VERLETZT_LEICHT = 91;

    /** heavy injured */
    public static final int HIGHLIGHT_SUB_VERLETZT_SCHWER = 92;

    /** injured, no replacement player */
    public static final int HIGHLIGHT_SUB_VERLETZT_KEIN_ERSATZ_EINS = 93;

    /** bruised */
    public static final int HIGHLIGHT_SUB_PFLASTER_BEHANDLUNG = 94;

    /** injured */
    public static final int HIGHLIGHT_SUB_VERLETZT = 95;

    /** injured, no replacement player */
    public static final int HIGHLIGHT_SUB_VERLETZT_KEIN_ERSATZ_ZWEI = 96;

    /** injured, no replacement player */
    public static final int HIGHLIGHT_SUB_VERLETZT_TORWART_FELDSPIELER = 97;
    
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * Basic events (HIGHLIGHT_ERFOLGREICH and HIGHLIGHT_FEHLGESCHLAGEN only)
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /** free kick */
    public static final int HIGHLIGHT_SUB_FREISTOSS = 0;

    /** middle */
    public static final int HIGHLIGHT_SUB_DURCH_MITTE = 1;

    /** left */
    public static final int HIGHLIGHT_SUB_UEBER_LINKS = 2;

    /** right */
    public static final int HIGHLIGHT_SUB_UEBER_RECHTS = 3;

    /** penalty */
    public static final int HIGHLIGHT_SUB_ELFMETER = 4;

    /** free kick */
    public static final int HIGHLIGHT_SUB_FREISTOSS_2 = 10;

    /** middle */
    public static final int HIGHLIGHT_SUB_DURCH_MITTE_2 = 11;

    /** left */
    public static final int HIGHLIGHT_SUB_UEBER_LINKS_2 = 12;

    /** right */
    public static final int HIGHLIGHT_SUB_UEBER_RECHTS_2 = 13;

    /** penalty */
    public static final int HIGHLIGHT_SUB_ELFMETER_2 = 14;

    /** free kick */
    public static final int HIGHLIGHT_SUB_FREISTOSS_3 = 20;

    /** middle */
    public static final int HIGHLIGHT_SUB_DURCH_MITTE_3 = 21;

    /** left */
    public static final int HIGHLIGHT_SUB_UEBER_LINKS_3 = 22;

    /** right */
    public static final int HIGHLIGHT_SUB_UEBER_RECHTS_3 = 23;

    /** penalty */
    public static final int HIGHLIGHT_SUB_ELFMETER_3 = 24;

    /** free kick */
    public static final int HIGHLIGHT_SUB_FREISTOSS_4 = 30;

    /** middle */
    public static final int HIGHLIGHT_SUB_DURCH_MITTE_4 = 31;

    /** left */
    public static final int HIGHLIGHT_SUB_UEBER_LINKS_4 = 32;

    /** right */
    public static final int HIGHLIGHT_SUB_UEBER_RECHTS_4 = 33;

    /** penalty */
    public static final int HIGHLIGHT_SUB_ELFMETER_4 = 34;

    /** free kick */
    public static final int HIGHLIGHT_SUB_FREISTOSS_5 = 50;

    /** middle */
    public static final int HIGHLIGHT_SUB_DURCH_MITTE_5 = 51;

    /** left */
    public static final int HIGHLIGHT_SUB_UEBER_LINKS_5 = 52;

    /** right */
    public static final int HIGHLIGHT_SUB_UEBER_RECHTS_5 = 53;

    /** penalty */
    public static final int HIGHLIGHT_SUB_ELFMETER_5 = 54;

    /** free kick */
    public static final int HIGHLIGHT_SUB_FREISTOSS_6 = 60;

    /** middle */
    public static final int HIGHLIGHT_SUB_DURCH_MITTE_6 = 61;

    /** left */
    public static final int HIGHLIGHT_SUB_UEBER_LINKS_6 = 62;

    /** right */
    public static final int HIGHLIGHT_SUB_UEBER_RECHTS_6 = 63;

    /** penalty */
    public static final int HIGHLIGHT_SUB_ELFMETER_6 = 64;

    /** free kick */
    public static final int HIGHLIGHT_SUB_FREISTOSS_7 = 70;

    /** middle */
    public static final int HIGHLIGHT_SUB_DURCH_MITTE_7 = 71;

    /** left */
    public static final int HIGHLIGHT_SUB_UEBER_LINKS_7 = 72;

    /** right */
    public static final int HIGHLIGHT_SUB_UEBER_RECHTS_7 = 73;

    /** penalty */
    public static final int HIGHLIGHT_SUB_ELFMETER_7 = 74;

    /** free kick */
    public static final int HIGHLIGHT_SUB_FREISTOSS_8 = 80;

    /** middle */
    public static final int HIGHLIGHT_SUB_DURCH_MITTE_8 = 81;

    /** left */
    public static final int HIGHLIGHT_SUB_UEBER_LINKS_8 = 82;

    /** right */
    public static final int HIGHLIGHT_SUB_UEBER_RECHTS_8 = 83;

    /** penalty */
    public static final int HIGHLIGHT_SUB_ELFMETER_8 = 84;


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * Special events (HIGHLIGHT_ERFOLGREICH and HIGHLIGHT_FEHLGESCHLAGEN only)
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /** (positive) unpredicatable SE */
    public static final int HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_VORLAGE_TOR = 5;

    /** (positive) unpredicatable SE */
    public static final int HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_ABGEFANGEN_TOR = 6;

    /** (positive) long shot SE */
    public static final int HIGHLIGHT_SUB_WEITSCHUSS_TOR = 7;

    /** (positive) unpredicatable SE */
    public static final int HIGHLIGHT_SUB_UNVORHERSEHBAR_BALL_ERKAEMPFT_TOR = 8;

    /** (negative) unpredicatable SE own goal*/
    public static final int HIGHLIGHT_SUB_UNVORHERSEHBAR_BALLVERLUST_TOR = 9;
    
    /** (positive) quick SE */
    public static final int HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_TOR = 15;

    /** (positive) quick SE */
    public static final int HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_PASS_TOR = 16;

    /** (negative) stamina SE */
    public static final int HIGHLIGHT_SUB_SCHLECHTE_KONDITION_BALLVERLUST_TOR = 17;

    /** (positive) corner SE */
    public static final int HIGHLIGHT_SUB_ECKBALL_TOR = 18;

    /** (positive) corner-header SE */
    public static final int HIGHLIGHT_SUB_ECKBALL_KOPFTOR = 19;

    /** (positive) experience SE */
    public static final int HIGHLIGHT_SUB_ERFAHRENER_ANGREIFER_TOR = 35;

    /** (negative) experience SE */
    public static final int HIGHLIGHT_SUB_UNERFAHREN_TOR = 36;

    /** (positive) cross pass SE */
    public static final int HIGHLIGHT_SUB_QUERPASS_TOR = 37;

    /** (positive) cross pass SE */
    public static final int HIGHLIGHT_SUB_AUSSERGEWOEHNLICHER_PASS_TOR = 38;

    /** (positive) technical SE */
    public static final int HIGHLIGHT_SUB_TECHNIKER_ANGREIFER_TOR = 39;

    /** Counter */
    public static final int HIGHLIGHT_SUB_KONTERANGRIFF_EINS = 40;

    /** Counter */
    public static final int HIGHLIGHT_SUB_KONTERANGRIFF_ZWEI = 41;

    /** Counter */
    public static final int HIGHLIGHT_SUB_KONTERANGRIFF_DREI = 42;

    /** Counter */
    public static final int HIGHLIGHT_SUB_KONTERANGRIFF_VIER = 43;

    /** Counter */
    public static final int HIGHLIGHT_SUB_KONTERANGRIFF_FUENF = 44;

    /** indirect free kick, regular */
    public static final int HIGHLIGHT_SUB_INDIRECT_FREEKICK_1 = 85;
    
    /** @deprecated use HIGHLIGHT_SUB_INDIRECT_FREEKICK_1 */
    @Deprecated
	public static final int HIGHLIGHT_SUB_INDIRECT_FREEKICK_7 = 85;

    /** indirect free kick, counter */
    public static final int HIGHLIGHT_SUB_INDIRECT_FREEKICK_2 = 86;

    /** @deprecated use HIGHLIGHT_SUB_INDIRECT_FREEKICK_2 */
    @Deprecated
	public static final int HIGHLIGHT_SUB_INDIRECT_FREEKICK_8 = 86;

    /** long shot */
    public static final int HIGHLIGHT_SUB_LONGHSHOT_1 = 87;

    /** SE: Quick rushes, stopped by quick defender  */
    public static final int HIGHLIGHT_SUB_QUICK_RUSH_STOPPED_BY_DEF = 89;
    
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * Informal events (HIGHLIGHT_SPEZIAL only)
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Negative weather SE for technical players on rainy days
     */
    public static final int HIGHLIGHT_SUB_PLAYER_TECHNICAL_RAINY = 1;

    /**
     * Positive weather SE for powerful players on rainy days
     */
    public static final int HIGHLIGHT_SUB_PLAYER_POWERFUL_RAINY = 2;

    /**
     * Positve weather SE for technical players on sunny days
     */
    public static final int HIGHLIGHT_SUB_PLAYER_TECHNICAL_SUNNY = 3;

    /**
     * Negative weather SE for powerful players on sunny days
     */
    public static final int HIGHLIGHT_SUB_PLAYER_POWERFUL_SUNNY = 4;

    /**
     * Negative weather SE for quick players on rainy days
     */
    public static final int HIGHLIGHT_SUB_PLAYER_QUICK_RAINY = 5;

    /**
     * Negative weather SE for quick players on sunny days
     */
    public static final int HIGHLIGHT_SUB_PLAYER_QUICK_SUNNY = 6;

    /**
     * Negative weather SE for powerful players on sunny days
     * @deprecated	use HIGHLIGHT_SUB_PLAYER_POWERFUL_SUNNY instead
     */
    @Deprecated
	public static final int HIGHLIGHT_SUB_SPIELER_KANN_SICH_NICHT_DURCHSETZEN = 4;

    /**
     * Negative weather SE for quick players on sunny days
     * @deprecated	use HIGHLIGHT_SUB_PLAYER_QUICK_SUNNY instead
     */
    @Deprecated
	public static final int HIGHLIGHT_SUB_SPIELER_MUEDE = 6;

    /** Tactic Pressing */
    public static final int HIGHLIGHT_SUB_SPIELT_PRESSING = 31;

    /** Tactic Counter Attack */
    public static final int HIGHLIGHT_SUB_SPIELT_KONTER = 32;

    /** Tactic Attack in the middle */
    public static final int HIGHLIGHT_SUB_SPIELT_DURCH_MITTE = 33;

    /** Tactic Attack over wings */
    public static final int HIGHLIGHT_SUB_SPIELT_UEBER_FLUEGEL = 34;

    /** Tactic Creative */
    public static final int HIGHLIGHT_SUB_TACTIC_CREATIVE = 35;

    /** Tactic Long shots */
    public static final int HIGHLIGHT_SUB_TACTIC_LONGSHOTS = 36;

    /** Tactic AIM */
    public static final int HIGHLIGHT_SUB_TACTIC_AIM = 43;

    /** Tactic AOW */
    public static final int HIGHLIGHT_SUB_TACTIC_AOW = 44;

    /* Substitutions */
    /** Substitution at deficit */
    public static final int HIGHLIGHT_SUB_SUBSTITUTION_DEFICIT = 50;

    /** Substitution at lead */
    public static final int HIGHLIGHT_SUB_SUBSTITUTION_LEAD = 51;

    /** Substitution, score is even */
    public static final int HIGHLIGHT_SUB_SUBSTITUTION_EVEN = 52;

    /* Tactic changes */
    /** Tactic change at deficit */
    public static final int HIGHLIGHT_SUB_TACTICCHANGE_DEFICIT = 60;

    /** Tactic change at lead */
    public static final int HIGHLIGHT_SUB_TACTICCHANGE_LEAD = 61;

    /** Tactic change, score is even */
    public static final int HIGHLIGHT_SUB_TACTICCHANGE_EVEN = 62;

    /** 	Player position swap: team is behind  */
    public static final int HIGHLIGHT_SUB_POSITION_SWAP_DEFICIT = 70;

    /** 	Player position swap: team is ahead  */
    public static final int HIGHLIGHT_SUB_POSITION_SWAP_LEAD = 71;

    /** 	Player position swap: minute */
    public static final int HIGHLIGHT_SUB_POSITION_SWAP_MINUTE = 72;
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * Informal events (HIGHLIGHT_KARTEN only)
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /** first booking */
    public static final int HIGHLIGHT_SUB_GELB_HARTER_EINSATZ = 10;

    /** first booking */
    public static final int HIGHLIGHT_SUB_GELB_UNFAIR = 11;

    /** second booking */
    public static final int HIGHLIGHT_SUB_GELB_ROT_HARTER_EINSATZ = 12;

    /** second booking */
    public static final int HIGHLIGHT_SUB_GELB_ROT_UNFAIR = 13;

    /** red card */
    public static final int HIGHLIGHT_SUB_ROT = 14;

    /** full time, end of game */
    public static final int HIGHLIGHT_SUB_SPIELENDE = 99;

    /** walkover - draw */
    public static final int HIGHLIGHT_SUB_WALKOVER_DRAW = 0;

    /** walkover - home team wins */
    public static final int HIGHLIGHT_SUB_WALKOVER_HOMETEAM_WINS = 1;

    /** walkover - home team wins */
    public static final int HIGHLIGHT_SUB_WALKOVER_AWAYTEAM_WINS = 2;
    
    /** indirect free kicks ratings (will be removed in the future)
     * SubjectTeamID   = Team ID
     * SubjectPlayerID = IFK_Attack
     * ObjectPlayerID  = IFK_Defense
     */
    public static final int HIGHLIGHT_SUB_IFK_RATINGS = 50;

}
