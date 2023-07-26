package core.rating;

import core.constants.player.PlayerSkill;
import core.constants.player.PlayerSpeciality;
import core.gui.HOMainFrame;
import core.model.Team;
import core.model.match.IMatchDetails;
import core.model.match.Matchdetails;
import core.model.match.Weather;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.util.HOLogger;
import module.lineup.Lineup;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static core.model.player.IMatchRoleID.*;
import static core.model.player.MatchRoleID.getPosition;
import static core.util.MathUtils.fuzzyEquals;

public class RatingPredictionManager {
	//~ Class constants ----------------------------------------------------------------------------
    private static final int THISSIDE = RatingPredictionParameter.THISSIDE;
    private static final int OTHERSIDE = RatingPredictionParameter.OTHERSIDE;
    private static final int ALLSIDES = RatingPredictionParameter.ALLSIDES;
    private static final int MIDDLE = RatingPredictionParameter.MIDDLE;
    private static final int LEFT = RatingPredictionParameter.LEFT;
    private static final int RIGHT = RatingPredictionParameter.RIGHT;
    public static final Date LAST_CHANGE = (new GregorianCalendar(2009, Calendar.MAY, 18)).getTime(); //18.05.2009
	private static final int SIDEDEFENSE = 0;
    private static final int CENTRALDEFENSE = 1; 
    private static final int MIDFIELD = 2; 
    private static final int SIDEATTACK = 3; 
    private static final int CENTRALATTACK = 4;
    private static final int GOALKEEPING = PlayerSkill.KEEPER; // 0
    private static final int DEFENDING = PlayerSkill.DEFENDING; // 1
    private static final int WINGER = PlayerSkill.WINGER; // 2
    private static final int PLAYMAKING = PlayerSkill.PLAYMAKING; // 3
    private static final int SCORING = PlayerSkill.SCORING; // 4
    private static final int PASSING = PlayerSkill.PASSING; // 5
    private static final int SETPIECES = PlayerSkill.SET_PIECES; // 8
    public static final int SPEC_NONE = PlayerSpeciality.NO_SPECIALITY; // 0
    public static final int SPEC_TECHNICAL = PlayerSpeciality.TECHNICAL; // 1
    public static final int SPEC_QUICK = PlayerSpeciality.QUICK; // 2
    public static final int SPEC_POWERFUL = PlayerSpeciality.POWERFUL; // 3
    public static final int SPEC_UNPREDICTABLE = PlayerSpeciality.UNPREDICTABLE; // 4
    public static final int SPEC_HEADER = PlayerSpeciality.HEAD; // 5
    public static final int SPEC_REGAINER = PlayerSpeciality.REGAINER; // 6
	public static final int SPEC_NOTUSED = 7;
	public static final int SPEC_SUPPORT = PlayerSpeciality.SUPPORT; // 8
    public static final int SPEC_ALL = SPEC_SUPPORT+1; // 9
    public static final int NUM_SPEC = SPEC_ALL+1; // 10
	public static final double EPSILON = 0.000001;
//	public static final float DEFAULT_WEATHER_BONUS = 0.05f;

    //~ Class fields -------------------------------------------------------------------------------
	private static final HashMap<String, LinkedHashMap<Double, Double>> allStaminaEffect = new HashMap<>();

    // Initialize with default config
    private static RatingPredictionConfig config = RatingPredictionConfig.getInstance();
	
    /** Cache for player strength (Hashtable<String, Float>) */
    private static final Hashtable<String, Double> playerStrengthCache = new Hashtable<>();
	private static Double loyaltyDelta = null;
	private static Double loyaltySkillMax= null;
	private static Double homegrownBonus= null;
	private static Double loyaltyMax=null;
	private final RatingPredictionModel model;
	private final Team team;


	//~ Instance fields ----------------------------------------------------------------------------
    private short heimspiel;
    private short attitude;
    private short selbstvertrauen;
    private short stimmung;
    private short substimmung;
    private short taktikType;
	private final Lineup startingLineup;
    private int pullBackMinute;

	/**  Evolution of lineup during the game
	 *   he keys will represent an array of events (in minutes)
	 *  e.g.   t = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 46, 50, 55, 60, 65, 71, …...., 90, 91, ….]
	 *  i.e at each minute between 0 and 120 by step of 5 minutes     => +24 entries
	 *  At 46 and 91 to show resting effect     =>  +2 entries
	 *   At each minute a substitution occur.    => + [0-3] entries
	 *   + pullback event if it occurs: => + [0-1] entry
	 *           The values will represent the evolution of lineup
	 *          e.g.    {0:'starting_lineup', 5:'starting_lineup', …....... 71:'lineup_after_sub1'}
	 */
//	private final Hashtable<Double, Lineup> LineupEvolution;
//	private double userRatingOffset;

	public RatingPredictionManager(RatingPredictionModel model){
		this.model = model;
	}

	private double calcAverageRating(Lineup lineup, RatingPredictionModel.RatingSector s, int minutes) {
		var staminaChanges = new TreeSet<Byte>();
		for (byte i = 0; i < minutes; i += 5) {
			staminaChanges.add(i);
		}
		staminaChanges.addAll(lineup.getLineupChangeMinutes());
		var iStart = 0;
		var lastRating = 0.;
		var sumRating = 0.;
		for (var m : staminaChanges) {
			var rating = getRating(lineup, s, m);
			sumRating += lastRating * (m-iStart);
			lastRating = rating;
			iStart = m;
		}
		sumRating += lastRating * (minutes-iStart);
		return sumRating/minutes;
	}

	/**
	 * Rating revision -> sector -> minute -> rating
	 */
	private final Map<Long, Map<RatingPredictionModel.RatingSector, Map<Integer, Double>>> ratingCache = new HashMap<>();
	public double getRating(Lineup lineup, RatingPredictionModel.RatingSector s, int minute) {

		var revision = ratingCache.get(lineup.getRatingRevision());
		if (revision == null) {
			ratingCache.clear(); // remove old revisions
			ratingCache.put(lineup.getRatingRevision(), new HashMap<>());
		}
		var sector = revision.get(s);
		if ( sector != null){
			var rating = sector.get(minute);
			if ( rating != null){
				return rating;
			}
		}
		else {
			sector = new HashMap<>();
		}

		var ret = this.model.calcRating(lineup, s, minute);
		sector.put(minute, ret);
		return ret;
	}

//	public RatingPredictionManager(Lineup _startingLineup, Team iteam)
//    {
//        this.startingLineup = _startingLineup;
//		if (RatingPredictionManager.config == null) RatingPredictionManager.config = RatingPredictionConfig.getInstance();
//        init(iteam);
//        this.LineupEvolution = this.setLineupEvolution();
//    }

//	private Hashtable<Double, Lineup> setLineupEvolution()
//	{
//		// Initialize _LineupEvolution and add starting lineup
//		Hashtable<Double, Lineup> _LineupEvolution = new Hashtable<>();
//
//		// reset start time
//		for (var mid: startingLineup.getFieldPositions()) {
//			Player player = startingLineup.getPlayerByPositionID(mid.getId());
//			if (player != null) {
//				player.setGameStartingTime(0);
//			}
//		}
//
//		_LineupEvolution.put(0d, startingLineup.duplicate());
//
//		// list at which time occurs all events others than game start
//		List<Double> events = new ArrayList<>();
//
//		for(Substitution sub :startingLineup.getSubstitutionList())
//		{
//			if ((sub.getMatchMinuteCriteria() != -1) &&
//			   (sub.getRedCardCriteria() == RedCardCriteria.IGNORE) &&
//				(sub.getStanding() == GoalDiffCriteria.ANY_STANDING)) {
//				events.add((double)(sub.getMatchMinuteCriteria()));
//			}
//
//	     }
//
//		Collections.sort(events);
//
//		// we calculate lineup for all event
//		double t = 0d;
//		double tNextEvent, tMatchOrder;
//		Lineup currentLineup;
//
//		// define time of next event
//		if (events.size() > 0) {
//			tNextEvent = events.get(0);
//		}
//		else
//		{
//			tNextEvent = 125d;
//		}
//
//		while(t<120d)
//		{
//			// use Lineup at last event as reference
//			currentLineup = _LineupEvolution.get(t).duplicate();
//
//			//if no match event between now and the next step of 5 minutes, we jump to the next step
//			if ((t+5-(t+5)%5)<tNextEvent + 3*EPSILON) t = t + 5 - (t + 5) % 5;
//
//			//else I treat next occurring match events
//			else
//			{
//				t = tNextEvent;
//
//				// In case the match order happen in whole 5 minutes, it is shifted by 2 Epsilon to be visible in the graphs
//				if (t%5<0.00001) t += 2*EPSILON;
//
//				for(Substitution sub :startingLineup.getSubstitutionList())
//				{
//					tMatchOrder = sub.getMatchMinuteCriteria();
//
//					if (tNextEvent == tMatchOrder)
//					{
//						// all matchOrders taking place now are recursively apply on the lineup object
//						//currentLineup.UpdateLineupWithMatchOrder(sub);
//						Lineup.applySubstitution(currentLineup.getFieldPositions(), sub);
//					}
//				}
//
//				// we remove all Match Events that have been already treated
//				var itr = events.iterator();
//				while (itr.hasNext())
//				{
//					Double x = itr.next();
//					if (Objects.equals(x, tNextEvent))
//						itr.remove();
//				}
//
//
//				// define time of next event
//				if (events.size() > 0) {
//					tNextEvent = events.get(0);
//				}
//				else
//				{
//					tNextEvent = 125d;
//				}
//			}
//			_LineupEvolution.put(t, currentLineup);
//		}
//
//		// we add time just after break in order to visualize respectively halftime and endgame rest effect
//		_LineupEvolution.put(45+EPSILON, _LineupEvolution.get(45d).duplicate());
//		_LineupEvolution.put(90+EPSILON, _LineupEvolution.get(90d).duplicate());
//
//
//		return _LineupEvolution;
//
//	}

//    private float calcRatings (Double t, Lineup lineup, int type, boolean useForm, Weather weather, boolean useWeatherImpact) {
//
//    	return calcRatings (t, lineup, type, ALLSIDES, useForm, weather, useWeatherImpact);
//    }
//
//    private float calcRatings (Double t, Lineup _lineup, int type, int side2calc, boolean useForm, Weather weather, boolean useWeatherImpact) {
//
//    	RatingPredictionParameter params;
//		switch (type) {
//			case SIDEDEFENSE -> params = config.getSideDefenseParameters();
//			case CENTRALDEFENSE -> params = config.getCentralDefenseParameters();
//			case MIDFIELD -> params = config.getMidfieldParameters();
//			case SIDEATTACK -> params = config.getSideAttackParameters();
//			case CENTRALATTACK -> params = config.getCentralAttackParameters();
//			default -> {
//				return 0;
//			}
//		}
//    	Hashtable<String, Properties> allSections = params.getAllSections();
//		Enumeration<String> allKeys = allSections.keys();
//    	double retVal = 0;
//    	while (allKeys.hasMoreElements()) {
//    		String sectionName = allKeys.nextElement();
//    		double curValue = calcPartialRating (t, _lineup, params, sectionName, side2calc, useForm, weather, useWeatherImpact);
//    		retVal += curValue;
//    	}
////		if ( t==0 ) HOLogger.instance().info(getClass(), "t=" + t + "; before applyCommonProps.GENERAL="+retVal);
//    	retVal = applyCommonProps (retVal, params, RatingPredictionParameter.GENERAL);
//    	return (float)retVal;
//    }
//
//    private double calcPartialRating (double t, Lineup _lineup, RatingPredictionParameter params, String sectionName, int side2calc, boolean useForm, Weather weather, boolean useWeatherImpact) {
//    	int skillType = sectionNameToSkillAndSide(sectionName)[0];
//    	int sideType = sectionNameToSkillAndSide(sectionName)[1];
//    	double retVal = 0;
//    	if (skillType == -1 || sideType == -1) {
//    		HOLogger.instance().debug(this.getClass(), "parseError: "+sectionName+" resolves to Skill "+skillType+", Side "+sideType);
//    		return 0;
//    	}
//		var useLeft = useSide(LEFT, side2calc, sideType);
//		var useMiddle =useSide(MIDDLE, side2calc, sideType);
//		var useRight = useSide(RIGHT, side2calc, sideType);
//    	double[][] allStk = getAllPlayerStrength(t, _lineup, useForm, weather, useWeatherImpact, skillType, useLeft, useMiddle, useRight);
//    	double[][] allWeights = getAllPlayerWeights(params, sectionName);
//		var maxSkillContribution = params.getParam(RatingPredictionParameter.GENERAL, "maxSkillContribution", 1);
//
//
//		for (int effPos=0; effPos < allStk.length; effPos++) {
//			double curAllSpecWeight = allWeights[effPos][SPEC_ALL];
//    		for (int spec=0; spec < SPEC_ALL; spec++) {
//    			if (spec == SPEC_NOTUSED)
//    				continue;
//    			double curStk = allStk[effPos][spec];
//				if (curStk > 0) {
//	    			double curWeight = allWeights[effPos][spec];
//					if ( curWeight<=0) curWeight = curAllSpecWeight;
//					//var inkr = adjustForCrowding(_lineup, curStk, effPos) * curWeight;
//					var inkr = curStk * curWeight;
//					retVal += inkr;
////					if ( t == 0 ) HOLogger.instance().info(getClass(), "section=" + sectionName + "; t=" + t + "; retArray["+getShortNameForPosition((byte)effPos)+"]["+getSpecialtyName(spec, false)+"]="+inkr);
//				}
//    		}
//    	}
//
//		// calc Schum's experience effect
//		var xpSectionName = "xp_" + getSectionName(sideType);
//		if ( params.hasSection(xpSectionName) ) {
////			if ( t==0 && sectionName.equals("playmaking_allsides")){
////				HOLogger.instance().debug(getClass(), "midfiled exp");
////			}
//			var inkr =  getAllPlayerXpEffect(_lineup, params, xpSectionName, useLeft, useMiddle, useRight);
//			retVal += inkr;
////			if ( t == 0 ) HOLogger.instance().info(getClass(), "section=" + xpSectionName + "; t=" + t + "; ret="+inkr);
//		}
//		retVal *= maxSkillContribution;
//		retVal = applyCommonProps (retVal, params, sectionName);
//    	return retVal;
//    }

//	private boolean useSide(int useSide, int side2Calc, int sideType) {
//		return switch (sideType) {
//			case THISSIDE -> useSide == side2Calc;
//			case OTHERSIDE -> useSide != side2Calc;
//			case MIDDLE -> useSide == MIDDLE;
//			default ->    // all sides
//					true;
//		};
//	}
//
//	private String getSectionName(int sideType) {
//		return switch (sideType) {
//			case THISSIDE -> "thisside";
//			case OTHERSIDE -> "otherside";
//			case MIDDLE -> "middle";
//			default -> "allsides";
//		};
//	}

	public double applyCommonProps (double inVal, RatingPredictionParameter params, String sectionName) {
    	double retVal = inVal;
        retVal += params.getParam(sectionName, "squareMod", 0) * Math.pow(inVal, 2); // Avoid if possible!
        retVal += params.getParam(sectionName, "cubeMod", 0) * Math.pow(inVal, 3); // Avoid even more!

    	if (taktikType == Matchdetails.TAKTIK_WINGS)
    		retVal *= params.getParam(sectionName, "tacticAOW", 1);
    	else if (taktikType == Matchdetails.TAKTIK_MIDDLE)
    		retVal *= params.getParam(sectionName, "tacticAIM", 1);
    	else if (taktikType == Matchdetails.TAKTIK_KONTER)
    		retVal *= params.getParam(sectionName, "tacticCounter", 1);
    	else if (taktikType == Matchdetails.TAKTIK_CREATIVE)
    		retVal *= params.getParam(sectionName, "tacticcreative", 1);
    	else if (taktikType == Matchdetails.TAKTIK_PRESSING)
    		retVal *= params.getParam(sectionName, "tacticpressing", 1);
    	else if (taktikType == Matchdetails.TAKTIK_LONGSHOTS)
    		retVal *= params.getParam(sectionName, "tacticlongshots", 1);

        double teamspirit = (double)stimmung + ((double)substimmung / 5);
        // Alternative 1: TS linear
        retVal *= (1 + params.getParam(sectionName, "teamspiritmulti", 0)
        			*(teamspirit - 5.5));
        // Alternative 2: TS exponentiell
       	retVal *= Math.pow((teamspirit * params.getParam(sectionName, "teamspiritpremulti", 1) + params.getParam(sectionName, "teamspiritoffset", 0)),
       				params.getParam(sectionName, "teamspiritpower", 0)) * params.getParam(sectionName,"teamspiritpostmulti", 1);
        
    	if (heimspiel == IMatchDetails.LOCATION_HOME)
    		retVal *= params.getParam(sectionName, "home", 1);
    	else if (heimspiel == IMatchDetails.LOCATION_AWAYDERBY)
    		retVal *= params.getParam(sectionName, "awayDerby", 1);
    	else
    		retVal *= params.getParam(sectionName, "away", 1);

    	if (attitude == Matchdetails.EINSTELLUNG_PIC)
    		retVal *= params.getParam(sectionName, "pic", 1);
    	else if (attitude == Matchdetails.EINSTELLUNG_MOTS)
    		retVal *= params.getParam(sectionName, "mots", 1);
    	else
    		retVal *= params.getParam(sectionName, "normal", 1);
    	
		retVal *= (1.0 + params.getParam(sectionName, "confidence", 0) * (float)(selbstvertrauen - 5));

        // off Trainer
        double offensive = params.getParam(sectionName, "trainerOff", 1);
        // def Trainer
   	    double defensive = params.getParam(sectionName, "trainerDef", 1);
        // neutral Trainer
   	    double neutral = params.getParam(sectionName, "trainerNeutral", 1);

        retVal *= getTrainerEffect(defensive, offensive, neutral);
        
        // PullBack event
//		boolean pullBackOverride = false;
		int actualPullBackMinute = pullBackMinute;
        if (actualPullBackMinute >= 0 && actualPullBackMinute <= 90) {
        	retVal *= 1.0 + (90 - actualPullBackMinute) / 90.0
					* params.getParam(sectionName, "pullback", 0);
        }
        
        retVal *= params.getParam(sectionName, "multiplier", 1);
		retVal = Math.pow(retVal, params.getParam(sectionName, "power", 1));
		retVal += params.getParam(sectionName, "delta", 0);
//    	System.out.println ("applyCommonProps: section "+sectionName+", before="+inVal+", after="+retVal);
    	return retVal;
    }
    
    private static String getSpecialtyName (int specialty, boolean withDot) {
    	String retVal = (withDot?".":"");
		switch (specialty) {
			case SPEC_NONE -> retVal += "none";
			case SPEC_TECHNICAL -> retVal += "technical";
			case SPEC_QUICK -> retVal += "quick";
			case SPEC_POWERFUL -> retVal += "powerful";
			case SPEC_UNPREDICTABLE -> retVal += "unpredictable";
			case SPEC_HEADER -> retVal += "header";
			case SPEC_REGAINER -> retVal += "regainer";
			case SPEC_NOTUSED -> retVal = "";
			case SPEC_SUPPORT -> retVal += "support";
			case SPEC_ALL ->
//			retVal += "all";
					retVal = "";
			default -> {
				return "";
			}
		}
    	return retVal;
    }

    private static String getSkillName (int skill) {
		return switch (skill) {
			case GOALKEEPING -> "goalkeeping";
			case DEFENDING -> "defending";
			case WINGER -> "winger";
			case PLAYMAKING -> "playmaking";
			case SCORING -> "scoring";
			case PASSING -> "passing";
			case SETPIECES -> "setpieces";
			default -> "";
		};
    }

    private static int getSkillByName (String skillName) {
    	skillName = skillName.toLowerCase(Locale.ENGLISH);
		return switch (skillName) {
			case "goalkeeping" -> GOALKEEPING;
			case "defending" -> DEFENDING;
			case "winger" -> WINGER;
			case "playmaking" -> PLAYMAKING;
			case "scoring" -> SCORING;
			case "passing" -> PASSING;
			case "setpieces" -> SETPIECES;
			default -> -1;
		};
    }

    private static int getSideByName (String sideName) {
    	sideName = sideName.toLowerCase(Locale.ENGLISH);
		return switch (sideName) {
			case "thisside" -> THISSIDE;
			case "otherside" -> OTHERSIDE;
			case "middle" -> MIDDLE;
			case "", "allsides" -> ALLSIDES;
			default -> -1;
		};
    }
    
    private static int[] sectionNameToSkillAndSide (String sectionName) {
    	// retArray[0] == skill
    	// retArray[1] == side
    	int[] retArray = new int[2];
    	String skillName = "";
    	String sideName = "";    	
    	if (sectionName.contains("_")) {
    		String[] tmp = sectionName.split("_");
    		if (tmp.length == 2) {
    			skillName = tmp[0];
    			sideName = tmp[1];
    		}
    	} else {
    		skillName = sectionName;
    	}
    	retArray[0] = getSkillByName (skillName);
    	retArray[1] = getSideByName (sideName);
    	return retArray;
    }

	public Hashtable<Double, Double> getCentralDefenseRatings(boolean useForm, boolean useWeatherImpact)
	{
		Weather weather = HOMainFrame.getWeather();
		double userRatingOffset = 0;
		Hashtable<Double, Double> CentralDefenseRatings = new Hashtable<>();
		for (Map.Entry<Double,Lineup> tLineup : LineupEvolution.entrySet()) {
			var rating = calcRatings(tLineup.getKey(), tLineup.getValue(), CENTRALDEFENSE, useForm, weather, useWeatherImpact);
			CentralDefenseRatings.put(tLineup.getKey(), userRatingOffset + rating);
//			if ( tLineup.getKey()==0 ) HOLogger.instance().debug(getClass(), "CentralDefenseRatings["+tLineup.getKey()+"]="+rating);
		}
		return CentralDefenseRatings;
	}

	public Hashtable<Double, Double> getCentralAttackRatings(boolean useForm, boolean useWeatherImpact)
	{
		Weather weather = HOMainFrame.getWeather();
		double userRatingOffset = 0;
		Hashtable<Double, Double> CentralAttackRatings = new Hashtable<>();
		for (Map.Entry<Double,Lineup> tLineup : LineupEvolution.entrySet()) {
			var rating = calcRatings(tLineup.getKey(), tLineup.getValue(), CENTRALATTACK, useForm, weather, useWeatherImpact);
			CentralAttackRatings.put(tLineup.getKey(), userRatingOffset + rating);
//			if ( tLineup.getKey()==0 ) HOLogger.instance().debug(getClass(), "CentralAttackRatings["+tLineup.getKey()+"]="+rating);
		}
		return CentralAttackRatings;
	}


	public Hashtable<Double, Double> getRightDefenseRatings(boolean useForm, boolean useWeatherImpact)
	{
		Weather weather = HOMainFrame.getWeather();
		double userRatingOffset = 0;//UserParameter.instance().rightDefenceOffset;
		Hashtable<Double, Double> RightDefenseRatings = new Hashtable<>();
		for (Map.Entry<Double,Lineup> tLineup : LineupEvolution.entrySet()) {
			var rating = calcRatings(tLineup.getKey(), tLineup.getValue(), SIDEDEFENSE, RIGHT, useForm, weather, useWeatherImpact);
			RightDefenseRatings.put(tLineup.getKey(), userRatingOffset + rating);
//			if ( tLineup.getKey()==0 ) HOLogger.instance().debug(getClass(), "RightDefenseRatings["+tLineup.getKey()+"]="+rating);
		}
		return RightDefenseRatings;
	}


	public Hashtable<Double, Double> getLeftDefenseRatings(boolean useForm, boolean useWeatherImpact)
	{
		Weather weather = HOMainFrame.getWeather();
		double userRatingOffset = 0;
		Hashtable<Double, Double> LeftDefenseRatings = new Hashtable<>();
		for (Map.Entry<Double,Lineup> tLineup : LineupEvolution.entrySet()) {
			var rating =  calcRatings(tLineup.getKey(), tLineup.getValue(), SIDEDEFENSE, LEFT, useForm, weather, useWeatherImpact);
			LeftDefenseRatings.put(tLineup.getKey(), userRatingOffset + rating);
//			if ( tLineup.getKey()==0 ) HOLogger.instance().debug(getClass(), "LeftDefenseRatings["+tLineup.getKey()+"]="+rating);
		}
		return LeftDefenseRatings;
	}

	public Hashtable<Double, Double> getLeftAttackRatings(boolean useForm, boolean useWeatherImpact)
	{
		Weather weather = HOMainFrame.getWeather();
		double userRatingOffset = 0;
		Hashtable<Double, Double> LeftAttackRatings = new Hashtable<>();
		for (Map.Entry<Double,Lineup> tLineup : LineupEvolution.entrySet()) {
			var rating = calcRatings(tLineup.getKey(), tLineup.getValue(), SIDEATTACK, LEFT, useForm, weather, useWeatherImpact);
			LeftAttackRatings.put(tLineup.getKey(), userRatingOffset + rating);
//			if ( tLineup.getKey()==0 ) HOLogger.instance().debug(getClass(), "LeftAttackRatings["+tLineup.getKey()+"]="+rating);
		}
		return LeftAttackRatings;
	}

	public Hashtable<Double, Double> getRightAttackRatings(boolean useForm, boolean useWeatherImpact)
	{
		Weather weather = HOMainFrame.getWeather();
		double userRatingOffset = 0;
		Hashtable<Double, Double> RightAttackRatings = new Hashtable<>();
		for (Map.Entry<Double,Lineup> tLineup : LineupEvolution.entrySet()) {
			var rating = calcRatings(tLineup.getKey(), tLineup.getValue(), SIDEATTACK, RIGHT, useForm, weather, useWeatherImpact);
			RightAttackRatings.put(tLineup.getKey(), userRatingOffset + rating);
//			if ( tLineup.getKey()==0 ) HOLogger.instance().debug(getClass(), "RightAttackRatings["+tLineup.getKey()+"]="+rating);
		}
		return RightAttackRatings;
	}

	public Hashtable<Double, Double> getMFRatings(boolean useForm, boolean useWeatherImpact)
	{
		Weather weather = HOMainFrame.getWeather();
		double userRatingOffset = 0;
		Hashtable<Double, Double> MidfieldRatings = new Hashtable<>();
		for (Map.Entry<Double,Lineup> tLineup : LineupEvolution.entrySet()) {
			var rating = calcRatings(tLineup.getKey(), tLineup.getValue(), MIDFIELD, useForm, weather, useWeatherImpact);
			MidfieldRatings.put(tLineup.getKey(), userRatingOffset + rating);
//			if ( tLineup.getKey()==0 ) HOLogger.instance().debug(getClass(), "MidfieldRatings["+tLineup.getKey()+"]="+rating);
		}
		return MidfieldRatings;
	}

    
    private double getCrowdingPenalty(Lineup _lineup, int pos) {
    	double penalty;
    	RatingPredictionParameter  para = config.getPlayerStrengthParameters();
    	
//    	HOLogger.instance().debug(getClass(), "Parameter file used: " + config.getPredictionName());

		penalty = switch (pos) {
			case CENTRALDEFENSE ->
					// Central Defender
					para.getParam(RatingPredictionParameter.GENERAL, getNumCDs(_lineup) + "CdMulti");
			case MIDFIELD ->
					// Midfielder
					para.getParam(RatingPredictionParameter.GENERAL, getNumIMs(_lineup) + "MfMulti");
			case CENTRALATTACK ->
					// Forward
					para.getParam(RatingPredictionParameter.GENERAL, getNumFWs(_lineup) + "FwMulti");
			default -> 1;
		};
    	return penalty;
    }
    

    public static double[][] getAllPlayerWeights (RatingPredictionParameter params, String sectionName) {
		double[][] weights = new double[NUM_POSITIONS][NUM_SPEC];
		double modCD = params.getParam(sectionName, "allCDs", 1);
		double modWB = params.getParam(sectionName, "allWBs", 1);
		double modIM = params.getParam(sectionName, "allIMs", 1);
		double modWI = params.getParam(sectionName, "allWIs", 1);
		double modFW = params.getParam(sectionName, "allFWs", 1);
		for (int specialty = 0; specialty < NUM_SPEC; specialty++) {
			if (specialty == SPEC_NOTUSED)
				continue;
			String specialtyName = getSpecialtyName(specialty, true);
			weights[KEEPER][specialty] = params.getParam(sectionName, "keeper" + specialtyName);
			weights[KEEPER][specialty] += params.getParam(sectionName, "gk" + specialtyName);    // alias for keeper
			weights[CENTRAL_DEFENDER][specialty] = params.getParam(sectionName, "cd_norm" + specialtyName) * modCD;
			weights[CENTRAL_DEFENDER][specialty] += params.getParam(sectionName, "cd" + specialtyName) * modCD;    // alias for cd_norm
			weights[CENTRAL_DEFENDER_OFF][specialty] = params.getParam(sectionName, "cd_off" + specialtyName) * modCD;
			weights[CENTRAL_DEFENDER_TOWING][specialty] = params.getParam(sectionName, "cd_tw" + specialtyName) * modCD;
			weights[BACK][specialty] = params.getParam(sectionName, "wb_norm" + specialtyName) * modWB;
			weights[BACK][specialty] += params.getParam(sectionName, "wb" + specialtyName) * modWB;    // alias for wb_norm
			weights[BACK_OFF][specialty] = params.getParam(sectionName, "wb_off" + specialtyName) * modWB;
			weights[BACK_DEF][specialty] = params.getParam(sectionName, "wb_def" + specialtyName) * modWB;
			weights[BACK_TOMID][specialty] = params.getParam(sectionName, "wb_tm" + specialtyName) * modWB;
			weights[MIDFIELDER][specialty] = params.getParam(sectionName, "im_norm" + specialtyName) * modIM;
			weights[MIDFIELDER][specialty] += params.getParam(sectionName, "im" + specialtyName) * modIM;    // alias for im_norm
			weights[MIDFIELDER_OFF][specialty] = params.getParam(sectionName, "im_off" + specialtyName) * modIM;
			weights[MIDFIELDER_DEF][specialty] = params.getParam(sectionName, "im_def" + specialtyName) * modIM;
			weights[MIDFIELDER_TOWING][specialty] = params.getParam(sectionName, "im_tw" + specialtyName) * modIM;
			weights[IMatchRoleID.WINGER][specialty] = params.getParam(sectionName, "wi_norm" + specialtyName) * modWI;
			weights[IMatchRoleID.WINGER][specialty] += params.getParam(sectionName, "wi" + specialtyName) * modWI;    // alias for wi_norm
			weights[WINGER_OFF][specialty] = params.getParam(sectionName, "wi_off" + specialtyName) * modWI;
			weights[WINGER_DEF][specialty] = params.getParam(sectionName, "wi_def" + specialtyName) * modWI;
			weights[WINGER_TOMID][specialty] = params.getParam(sectionName, "wi_tm" + specialtyName) * modWI;
			weights[FORWARD][specialty] = params.getParam(sectionName, "fw_norm" + specialtyName) * modFW;
			weights[FORWARD][specialty] += params.getParam(sectionName, "fw" + specialtyName) * modFW;    // alias for fw_norm
			weights[FORWARD_DEF][specialty] = params.getParam(sectionName, "fw_def" + specialtyName) * modFW;
			weights[FORWARD_TOWING][specialty] = params.getParam(sectionName, "fw_tw" + specialtyName) * modFW;
		}
		return weights;
	}

    public int getNumIMs (Lineup _lineup) {
    	int retVal = 0;
    	for(int pos : aFieldMatchRoleID) {
    		Player player = _lineup.getPlayerByPositionID(pos);
            if (player != null) {
            	if (pos == rightInnerMidfield || pos == leftInnerMidfield ||
            			pos == centralInnerMidfield)
            		retVal++;
            }
    	}
    	return retVal;
    }

    public int getNumFWs (Lineup _lineup) {
    	int retVal = 0;
    	for(int pos : aFieldMatchRoleID) {
    		Player player = _lineup.getPlayerByPositionID(pos);
            if (player != null) {
            	if (pos == rightForward || pos == leftForward ||
            			pos == centralForward)
            		retVal++;
            }
    	}
    	return retVal;
    }

    public int getNumCDs (Lineup _lineup) {
    	int retVal = 0;
    	for(int pos : aFieldMatchRoleID) {
    		Player player = _lineup.getPlayerByPositionID(pos);
            if (player != null) {
            	if (pos == rightCentralDefender || pos == leftCentralDefender ||
            			pos == middleCentralDefender)
            		retVal++;
            }
    	}
    	return retVal;
    }

    public static double getLoyaltyEffect(Player player) {
		var ret = getLoyaltyMax()*(player.getLoyalty()+getLoyaltyDelta())/getLoyaltySkillMax();
    	if (player.isHomeGrown()) {
			ret += getHomegrownBonus();
		}
		return ret;
    }
	public static double getLoyaltyMax() {
		if (loyaltyMax==null){
			loyaltyMax=config.getPlayerStrengthParameters().getParam(RatingPredictionParameter.GENERAL, "loyaltyMax", 1.);
		}
		return loyaltyMax;
	}

	private static double getLoyaltyDelta() {
		if (loyaltyDelta == null) {
			loyaltyDelta = config.getPlayerStrengthParameters().getParam(RatingPredictionParameter.GENERAL, "loyaltyDelta", -1.);
		}
		return loyaltyDelta;
	}
	private static double getLoyaltySkillMax() {
		if (loyaltySkillMax == null) {
			loyaltySkillMax = config.getPlayerStrengthParameters().getParam(RatingPredictionParameter.GENERAL, "loyaltySkillMAx", 19.);
		}
		return loyaltySkillMax;
	}
	private static double getHomegrownBonus() {
		if (homegrownBonus == null) {
			homegrownBonus = config.getPlayerStrengthParameters().getParam(RatingPredictionParameter.GENERAL, "homegrownBonus", 0.5);
		}
		return homegrownBonus;
	}

	private double getAllPlayerXpEffect(Lineup lineup, RatingPredictionParameter params, String xpSectionName, boolean useLeft, boolean useMiddle, boolean useRight) {
		double ret = 0;
		for (int pos : aFieldMatchRoleID) {
			Player player = lineup.getPlayerByPositionID(pos);
			if (player != null) {
				var posTactic = getRelevantPositionTactic(lineup.getTactic4PositionID(pos), pos, useLeft, useMiddle, useRight);
				if (posTactic != null) {
					var key = MatchRoleID.getPositionPropertyKey(posTactic);
					var v = params.getParam(xpSectionName, key, 0);
					if ( v > 0) {
						var maxXpDelta = params.getParam(RatingPredictionParameter.GENERAL, "maxXpDelta", 0);
						var xp = player.getExperience() + player.getSubExperience();
						ret += _calcPlayerExperienceEffect(config.getPlayerStrengthParameters(), v*maxXpDelta, xp);
					}
				}
			}
		}
		return ret;
	}
	private Byte getRelevantPositionTactic(byte taktik, int pos, boolean useLeft, boolean useMiddle, boolean useRight) {
		switch (pos) {
			case keeper -> {
				return getPosition(pos, taktik);
			}
			case rightCentralDefender, rightBack, rightWinger, rightInnerMidfield, rightForward -> {
				if (useRight) {
					return getPosition(pos, taktik);
				}
			}
			case leftCentralDefender, leftBack, leftWinger, leftInnerMidfield, leftForward -> {
				if (useLeft) {
					return getPosition(pos, taktik);
				}
			}
			case middleCentralDefender, centralInnerMidfield, centralForward -> {
				if (useMiddle) {
					return getPosition(pos, taktik);
				}
			}
		}
		return null;
	}

	public double[][] getAllPlayerStrength (double t, Lineup _lineup, boolean useForm, Weather weather, boolean useWeatherImpact, int skillType, boolean useLeft, boolean useMiddle, boolean useRight) {
    	double[][] retArray = new double[NUM_POSITIONS][SPEC_ALL];
        for(int pos : aFieldMatchRoleID) {
            Player player = _lineup.getPlayerByPositionID(pos);
            if(player != null) {
				byte taktik = _lineup.getTactic4PositionID(pos);
				var posTactic = getRelevantPositionTactic(taktik, pos, useLeft, useMiddle, useRight);
				if ( posTactic != null ){
					if ( this.startingLineup.getManMarkingPosition() != null) {
						var manMarkingOrder = this.startingLineup.getManMarkingOrder();
						if (manMarkingOrder != null &&
								player.getPlayerID() == manMarkingOrder.getSubjectPlayerID() &&
								player.getGameStartingTime() + 5 >= t	// man marking starts 5 minutes after player enters the match
						) {
							// create player clone with reduced skill values
							player = player.createManMarker(this.startingLineup.getManMarkingPosition());
						}
					}
					int specialty = player.getPlayerSpecialty();
					retArray[(int)posTactic][specialty] += calcPlayerStrength(
							t,
							player,
							skillType,
							useForm,
							_lineup.getTacticType() == IMatchDetails.TAKTIK_PRESSING,
							weather, useWeatherImpact,
							getCrowdingPenalty(_lineup, getPosition(pos, NORMAL)));
				}
            }
        }
        return retArray;
    }

	public static float calcPlayerStrength(double t, Player player, int skillType, boolean useForm, boolean isPressing) {
    	return calcPlayerStrength(t, player, skillType, useForm, isPressing, null, false, 1);
	}

    public static float calcPlayerStrength(double t, Player player, int skillType, boolean useForm, boolean isPressing, @Nullable Weather weather, boolean useWeatherImpact, double overcrowdingPenalty) {
        double retVal = 0.0F;
        try
        {
            double skill;
            double subSkill;
            skill = player.getValue4Skill(skillType);
            var subskillFromDB = player.getSub4Skill(skillType);

            /*
             * If we know the last level up date from this player or
             * the user has set an offset manually -> use this sub/offset
             */
            if (subskillFromDB > 0 || player.getLastLevelUp(skillType) != null ) {
				subSkill = subskillFromDB;
			}
            else {
				/*
				 * Try to guess the sub based on the skill level
				 */
				subSkill = getSubDeltaFromConfig(config.getPlayerStrengthParameters(), getSkillName(skillType), (int) skill);
			}
            // subSkill>1, this should not happen
            if (subSkill > 1)
            	subSkill = 1;
            skill = skill + subSkill;
            
            // Add loyalty and homegrown bonuses
            skill += getLoyaltyEffect(player);

            // consider weather impact
			// is it where it should be ? maybe weather impact should  be considered before loyalty and homegrown bonus, as always HT is not clear
			if (useWeatherImpact)
			{
				skill *= player.getImpactWeatherEffect(weather);
			}

			//HOLogger.instance().debug(RatingPredictionManager.class, "Player: " + player.getFullName());
            retVal = _calcPlayerStrength(config.getPlayerStrengthParameters(), getSkillName(skillType), player.getStamina(), player.getExperience() + player.getSubExperience(), skill, player.getForm(), useForm, overcrowdingPenalty);
        }
        catch(Exception e) {
        	e.printStackTrace();
        }

		double StaminaEffect = 1;
        if (t >= 0) StaminaEffect = GetStaminaEffect(player.getStamina(),player.getGameStartingTime(), t, isPressing);
        else if (t==-2) StaminaEffect = getAvg90StaminaEffect(player.getStamina()); //average contribution of stamina over a 90 minutes game, this is used to compare player at given position
        return (float)(retVal * StaminaEffect);
    }


    /*
       return the stamina effect over 90 minutes, result empirically obtained by application of GetStaminaEffect formula
     	in the future stamina could be set to a fload instead of an intger for higher precision
     */
    private static double getAvg90StaminaEffect(int iStamina)
	{
		return switch (iStamina) {
			case 2 -> 0.664462406015038;
			case 3 -> 0.726691729323308;
			case 4 -> 0.787578947368421;
			case 5 -> 0.846515037593985;
			case 6 -> 0.902372180451128;
			case 7 -> 0.949612781954887;
			case 8 -> 0.983289473684211;
			case 9 -> 1d;
			default -> 0.651561111111111;
		};
	}

    private static float getSubDeltaFromConfig (RatingPredictionParameter params, String sectionName, int skill) {
    	String useSection = sectionName;
    	if (!params.hasSection(sectionName))
    		useSection = RatingPredictionParameter.GENERAL;
		//    	System.out.println(delta);
    	return (float)params.getParam(useSection, "skillSubDeltaForLevel"+skill, 0);
    }

    private static double _calcPlayerExperienceEffect(RatingPredictionParameter playerStrengthParameters, double maxXpDelta, double experience) {
		if (maxXpDelta <= 0) return 0;
		validatePlayerStrengthCache();
		var key = "XP|" + experience;
		if ( playerStrengthCache.containsKey(key)){
			return playerStrengthCache.get(key);
		}
		var xp = Math.max(0,experience + playerStrengthParameters.getParam(RatingPredictionParameter.GENERAL, "xpDelta", 0));
		var ret = playerStrengthParameters.getParam(RatingPredictionParameter.GENERAL, "constantEffK", 0);
		var x = xp; // x
		ret += x * playerStrengthParameters.getParam(RatingPredictionParameter.GENERAL, "linearEffK", 1);
		x *= xp; // x^2
		ret += x * playerStrengthParameters.getParam(RatingPredictionParameter.GENERAL, "quadraticEffK", 0);
		x *= xp; // x^3
		ret += x * playerStrengthParameters.getParam(RatingPredictionParameter.GENERAL, "cubicEffK", 0);
		x *= xp; // x^4
		ret += x * playerStrengthParameters.getParam(RatingPredictionParameter.GENERAL, "quarticEffK", 0);
		ret *= maxXpDelta;
		playerStrengthCache.put(key, ret);
		return ret;
	}

    private static double _calcPlayerStrength (RatingPredictionParameter playerStrengthParameters,
    	String sectionName, double stamina, double xp, double skill, double form, boolean useForm, double overcrowdingPenalty) {
		// If config changed, we have to clear the cache

		validatePlayerStrengthCache();
		String key = playerStrengthParameters.toString() + "|" + sectionName + "|" + stamina + "|" + xp + "|" + skill + "|" + form + "|" + useForm + "|" + overcrowdingPenalty;
		if (playerStrengthCache.containsKey(key)) {
			return playerStrengthCache.get(key);
		}

//		HOLogger.instance().debug(RatingPredictionManager.class, "sectionName=" + sectionName + " stamina=" + stamina + " xp=" + xp + " skill=" + skill + " form=" + form + " useForm=" + useForm);

		double rating;
		String useSection = sectionName;
		if (!playerStrengthParameters.hasSection(sectionName))
			useSection = RatingPredictionParameter.GENERAL;

		// Compute Xp Effect
		if (playerStrengthParameters.getParam(useSection, "multiXpLog10", 99) != 99) {
			xp = playerStrengthParameters.getParam(useSection, "multiXpLog10", 0) * Math.log10(xp);
		} else {
			xp += playerStrengthParameters.getParam(useSection, "xpDelta", 0);
			xp = Math.min(xp, playerStrengthParameters.getParam(useSection, "xpMax", 99999));
			xp *= playerStrengthParameters.getParam(useSection, "xpMultiplier", 1);
			xp = Math.pow(xp, playerStrengthParameters.getParam(useSection, "xpPower", 1));
			xp *= playerStrengthParameters.getParam(useSection, "finalXpMultiplier", 1);
			xp += playerStrengthParameters.getParam(useSection, "finalXpDelta", 0);
		}
		xp = Math.max(xp, playerStrengthParameters.getParam(useSection, "xpMin", 0));

		skill += playerStrengthParameters.getParam(useSection, "skillDelta", 0);
		skill = Math.max(skill, playerStrengthParameters.getParam(useSection, "skillMin", 0));
		skill = Math.min(skill, playerStrengthParameters.getParam(useSection, "skillMax", 99999));
		skill *= playerStrengthParameters.getParam(useSection, "skillMultiplier", 1);
		skill = Math.pow(skill, playerStrengthParameters.getParam(useSection, "skillPower", 1));

		form += playerStrengthParameters.getParam(useSection, "formDelta", 0);
		form = Math.max(form, playerStrengthParameters.getParam(useSection, "formMin", 0));
		form = Math.min(form, playerStrengthParameters.getParam(useSection, "formMax", 99999));
		form *= playerStrengthParameters.getParam(useSection, "formMultiplier", 1);
		form = Math.pow(form, playerStrengthParameters.getParam(useSection, "formPower", 1));


		if (playerStrengthParameters.getParam(useSection, "skillLog", 0) > 0)
			skill = Math.log(skill) / Math.log(playerStrengthParameters.getParam(useSection, "skillLog", 0));
		if (playerStrengthParameters.getParam(useSection, "formLog", 0) > 0)
			form = Math.log(form) / Math.log(playerStrengthParameters.getParam(useSection, "formLog", 0));


		skill *= playerStrengthParameters.getParam(useSection, "finalSkillMultiplier", 1);
		form *= playerStrengthParameters.getParam(useSection, "finalFormMultiplier", 1);


		skill += playerStrengthParameters.getParam(useSection, "finalSkillDelta", 0);
		form += playerStrengthParameters.getParam(useSection, "finalFormDelta", 0);


		rating = skill;
		if (useForm && playerStrengthParameters.getParam(useSection, "resultMultiForm", 0) > 0)
			rating *= playerStrengthParameters.getParam(useSection, "resultMultiForm", 0);
		if (playerStrengthParameters.getParam(useSection, "resultMultiXp", 0) > 0)
			rating *= playerStrengthParameters.getParam(useSection, "resultMultiXp", 0) * xp;

		if (useForm) {
			rating *= form;
		}

		rating *= overcrowdingPenalty;

		rating += playerStrengthParameters.getParam(useSection, "resultAddXp", 0) * xp;

		playerStrengthCache.put(key, rating);
//		HOLogger.instance().debug(RatingPredictionManager.class, "calcPlayerStrength ("
//				+ "SN=" + sectionName + ", Stamina=" + stamina + ", XP=" + xp + ", skill=" + skill + ", form=" + form + ", uF=" + useForm + ", overcrowdingPenalty=" + overcrowdingPenalty + ") =" + rating);

		return rating;
	}

	private static void validatePlayerStrengthCache() {
		if (!playerStrengthCache.containsKey("lastRebuild") || playerStrengthCache.get("lastRebuild") < config.getLastParse()) {
			HOLogger.instance().debug(RatingPredictionManager.class, "Rebuilding RPM cache!");
			playerStrengthCache.clear();
			playerStrengthCache.put("lastRebuild", (double) new Date().getTime());
			loyaltyDelta = null;
			loyaltySkillMax = null;
			homegrownBonus = null;
			loyaltyMax = null;
		}
	}

	private void init(Team team)
    {
        try
        {
			this.attitude = (short)startingLineup.getAttitude();
            this.heimspiel = startingLineup.getLocation();
            this.taktikType = (short)startingLineup.getTacticType();
            this.stimmung = (short)team.getTeamSpirit();
            this.substimmung = (short)team.getSubTeamSpirit();
            this.selbstvertrauen = (short)team.getConfidence();
            this.pullBackMinute = startingLineup.getPullBackMinute();
		}
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }

	private static double TryGetStaminaEffect(LinkedHashMap<Double, Double> StaminaEffect, double t)
	{
		if (StaminaEffect.containsKey(t)) return StaminaEffect.get(t);
		else if (StaminaEffect.containsKey(Math.floor(t)+EPSILON)) return StaminaEffect.get(Math.floor(t)+EPSILON);
		else return StaminaEffect.get(Math.floor(t));
	}

	 /**
	 * Returns the stamina effect per minute from tEnter tp tExit
	  * @param stamina : player stamina
	  * @param tEnter : at which minute the player entered the game
	  * @param tNow : current minute being played
	  * @param isTacticPressing : flag to identify Pressing Tactic
	  */
	public static double GetStaminaEffect(double stamina, double tEnter, double tNow, boolean isTacticPressing){
		if (tNow < tEnter)
		{
//			System.err.println("Inconsistent Lineup !!");   // This error occurs because of incorrect HO!Last Lineup table
			return 0;
		}
		String key = stamina + "|" + tEnter + "|" + isTacticPressing;
		if (! allStaminaEffect.containsKey(key)) ComputeStaminaEffectAtEachMarks(stamina, tEnter, isTacticPressing);
		return TryGetStaminaEffect(allStaminaEffect.get(key), tNow);
	}



	/**
	 * Compute Hashmap {double t -> double stamina_effect} for each t in LineupEvolution
	 * @param stamina : player stamina
	 * @param tEnter : at which minute the player entered the game
	 * @param isTacticPressing : flag to identify Pressing Tactic
	 */
	public static void ComputeStaminaEffectAtEachMarks(double stamina, double tEnter, boolean isTacticPressing){
		LinkedHashMap<Double, Double> staminaEffectAtEachMarks = new LinkedHashMap<>();
		String key = stamina + "|" + tEnter + "|" + isTacticPressing;

		boolean isHighStaminaPlayer;
		double tolerance = EPSILON/10d;

		// players entering just after break are not impacted by break rest effect
		if (fuzzyEquals(tEnter, 45d, tolerance) || fuzzyEquals(tEnter, 90d, tolerance)) tEnter += EPSILON;

		stamina -= 1;
		double P = isTacticPressing ? 1.1 : 1.0;
		double energyLossPerMinuteLS = -P * (5.95 - 27*stamina/70.0)/5;
		double energyLossPerMinuteHS = -3.25 * P /5;

		double energy;

		if(stamina >= 7) {
			isHighStaminaPlayer = true;
			energy = 125 + (stamina - 7) * 100 / 7.0 - energyLossPerMinuteHS;  //energy when entering the field for player whose stamina >= 8
		}
		else{
			isHighStaminaPlayer = false;
			energy = 102 + 23 / 7.0 * stamina - energyLossPerMinuteLS; //energy when entering the field for player whose stamina < 8
		}


		double t=tEnter;

		while(t<=120d)
		{
			if (fuzzyEquals(t, 45d+EPSILON, tolerance) && (tEnter<45d)) energy += 18.75;  // Energy recovery during half-time
			else if ((t == (90d+EPSILON)) && (tEnter<90d)) energy += 6.25;  // Energy recovery before extra-time
			else {
				if(isHighStaminaPlayer) {
					energy = energy + energyLossPerMinuteHS;
				}
				else {
					energy = energy + energyLossPerMinuteLS;
				}
			}

			staminaEffectAtEachMarks.put(t, Math.max(10, Math.min(100, energy)) / 100.0);

			// we move from 45 (resp. 90) -> 45 + EPSILON (resp. 90 + EPSILON) to account for break rest effect
			if (fuzzyEquals(t, 45d, tolerance) || fuzzyEquals(t, 90d, tolerance)) t += EPSILON;

			// we resume normal course, i.e. ratings are calculated by step of 1 minutes
			else if (fuzzyEquals(t, 45d+EPSILON, tolerance)) t = 46d;
			else if (fuzzyEquals(t, 90d+EPSILON, tolerance)) t = 91d;

			else t += 1;
		}

		allStaminaEffect.put(key, staminaEffectAtEachMarks);
	}



	private double getTrainerEffect(double defensive, double offensive, double neutral) {
    	
    	// styleOfPlay * 0.1 gives us the fraction of the distance we need to go from
    	// neutral to either defensive or offensive depending on what the style is.
    	
    	double outlier;
    	var styleOfPlay = this.startingLineup.getCoachModifier();
    	if (styleOfPlay >= 0) {
    		outlier = offensive;
    	} else {
    		outlier = defensive;
    	}

		return neutral + (Math.abs(styleOfPlay) * 0.1)*(outlier - neutral);
    }


	/**
     * get the tactic level for AiM / AoW
     *
     * @return tactic level
     */
    public float getTacticLevelAowAim()
    {
    	RatingPredictionParameter params = config.getTacticsParameters();
    	double retVal = 0;
    	float passing;
        for(int i : aFieldMatchRoleID)
        {
            Player ispieler = startingLineup.getPlayerByPositionID(i);
            byte taktik = startingLineup.getTactic4PositionID(i);
            if(ispieler != null) {
            	passing =  calcPlayerStrength(-1, ispieler, PASSING, true, false, null, false, 1);
            	// Zus. MF/IV/ST
                if(taktik == 7 || taktik == 6 || taktik == 5)
                    passing *= params.getParam("extraMulti");
                retVal += passing;
            }
        }

        retVal *= params.getParam("aim_aow", "postMulti", 1.0);
        retVal += params.getParam("aim_aow", "postDelta", 0);
    	retVal = applyCommonProps (retVal, params, "aim_aow");
    	retVal = applyCommonProps (retVal, params, RatingPredictionParameter.GENERAL);
    	return (float)retVal;
    }

    /**
     * get the tactic level for counter
     *
     * @return tactic level
     */
    public float getTacticLevelCounter()
    {
		Weather weather = HOMainFrame.getWeather();
		double retVal = 0;
    	RatingPredictionParameter params = config.getTacticsParameters();
		List<Integer> allDefenders = Arrays.asList(rightBack, rightCentralDefender, middleCentralDefender, leftCentralDefender, leftBack);

        for(int pos : allDefenders)
        {
            Player player = startingLineup.getPlayerByPositionID(pos);
            if(player != null) {
				retVal += (params.getParam("counter", "multiPs", 1.0) * calcPlayerStrength(-1, player, PASSING, true, true, weather, true, 1));
				retVal += (params.getParam("counter", "multiDe", 1.0) * calcPlayerStrength(-1, player, DEFENDING, true, true, weather, true, 1));
            }
        }
        retVal *= params.getParam("counter", "postMulti", 1.0);
        retVal += params.getParam("counter", "postDelta", 0);
    	retVal = applyCommonProps (retVal, params, RatingPredictionParameter.GENERAL);
    	return (float)retVal;
    }

    public final float getTacticLevelPressing() {
    	RatingPredictionParameter params = config.getTacticsParameters();
		final Weather weather = HOMainFrame.getWeather();
    	double retVal = 0;
		double defense;
		for(int pos : aOutfieldMatchRoleID)
        {
            Player player = startingLineup.getPlayerByPositionID(pos);
            if(player != null) {
            	defense = calcPlayerStrength(-2, player, DEFENDING, true, true, weather, true, 1);
                if (player.getPlayerSpecialty() == PlayerSpeciality.POWERFUL) {
                	defense *= 2;
                }
                retVal += defense;
            }
        }

        retVal *= params.getParam("pressing", "multiDe", 1.0);
        retVal += params.getParam("pressing", "postDelta", 0);
    	retVal = applyCommonProps (retVal, params, "pressing");
    	retVal = applyCommonProps (retVal, params, RatingPredictionParameter.GENERAL);
    	return (float)retVal;
    }

    /**
     * @return the tactic level for long shots
     */
    public final float getTacticLevelLongShots() {
		Weather weather = HOMainFrame.getWeather();
       	RatingPredictionParameter params = config.getTacticsParameters();
    	double retVal = 0;

        for(int pos : aOutfieldMatchRoleID)
        {
			Player player = startingLineup.getPlayerByPositionID(pos);
			if(player != null) {
				retVal += (params.getParam("longshots", "multiSc", 1.0) * calcPlayerStrength(-1, player, SCORING, true, true, weather, true, 1));
				retVal += (params.getParam("longshots", "multiSp", 1.0) * calcPlayerStrength(-1, player, SETPIECES, true, true, weather, true, 1));
			}
        }

		retVal += params.getParam("longshots", "postDelta", 0);
    	retVal = applyCommonProps (retVal, params, "longshots");
    	retVal = applyCommonProps (retVal, params, RatingPredictionParameter.GENERAL);
    	return (float)retVal;
    }

	public float getTacticLevelCreative() {
		// TODO find formula
		return 0;
	}
}
