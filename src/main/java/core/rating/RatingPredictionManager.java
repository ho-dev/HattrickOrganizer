package core.rating;

import core.constants.player.PlayerSkill;
import core.constants.player.PlayerSpeciality;
import core.model.Team;
import core.model.match.IMatchDetails;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.util.HOLogger;
import module.lineup.Lineup;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Properties;

public class RatingPredictionManager {
	//~ Class constants ----------------------------------------------------------------------------
	
    private static final int THISSIDE = RatingPredictionParameter.THISSIDE;
    private static final int OTHERSIDE = RatingPredictionParameter.OTHERSIDE;
    private static final int ALLSIDES = RatingPredictionParameter.ALLSIDES;
    private static final int MIDDLE = RatingPredictionParameter.MIDDLE;
    private static final int LEFT = RatingPredictionParameter.LEFT;
    private static final int RIGHT = RatingPredictionParameter.RIGHT;
    
    public static final Date LAST_CHANGE = (new GregorianCalendar(2009, 4, 18)).getTime(); //18.05.2009
    public static final Date LAST_CHANGE_FRIENDLY = (new GregorianCalendar(2009, 4, 18)).getTime(); //18.05.2009

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
	//private static final int STAMINA = ISpieler.SKILL_KONDITION; // 6
    //private static final int FORM = ISpieler.SKILL_FORM; // 7
    private static final int SETPIECES = PlayerSkill.SET_PIECES; // 8
    //private static final int EXPERIENCE = ISpieler.SKILL_EXPIERIENCE; // 9
    //private static final int LEADERSHIP = ISpieler.SKILL_LEADERSHIP; // 10
    
    public static final int SPEC_NONE = PlayerSpeciality.NO_SPECIALITY; // 0
    public static final int SPEC_TECHNICAL = PlayerSpeciality.TECHNICAL; // 1
    public static final int SPEC_QUICK = PlayerSpeciality.QUICK; // 2
    public static final int SPEC_POWERFUL = PlayerSpeciality.POWERFUL; // 3
    public static final int SPEC_UNPREDICTABLE = PlayerSpeciality.UNPREDICTABLE; // 4
    public static final int SPEC_HEADER = PlayerSpeciality.HEAD; // 5
    public static final int SPEC_REGAINER = PlayerSpeciality.REGAINER; // 6
    public static final int SPEC_ALL = PlayerSpeciality.REGAINER+1; // 7
    public static final int NUM_SPEC = SPEC_ALL+1; // 8

    //~ Class fields -------------------------------------------------------------------------------

    // Initialize with default config
    private static RatingPredictionConfig config = RatingPredictionConfig.getInstance();
	
    /** Cache for player strength (Hashtable<String, Float>) */
    private static Hashtable<String, Double> playerStrengthCache = new Hashtable<>();

    
    //~ Instance fields ----------------------------------------------------------------------------
    private short heimspiel;
    private short attitude;
    private short selbstvertrauen;
    private short stimmung;
    private short substimmung;
    private short taktikType;
	private short trainerType;
    private Lineup lineup;
    private int pullBackMinute;
    private boolean pullBackOverride;
    private int styleOfPlay;

    public RatingPredictionManager () {
    	if (RatingPredictionManager.config == null)
    		RatingPredictionManager.config = RatingPredictionConfig.getInstance();
    }

    public RatingPredictionManager (RatingPredictionConfig config) {
    	RatingPredictionManager.config = config;
    }
    
    public RatingPredictionManager(Lineup lineup, int i, Team iteam, short trainerType, int styleOfPlay, RatingPredictionConfig config)
    {
        this.lineup = lineup;
        RatingPredictionManager.config = config;
        init(iteam, trainerType, styleOfPlay);
    }

    public RatingPredictionManager(Lineup lineup, Team team, short trainerType, int styleOfPlay, RatingPredictionConfig config)
    {
        this(lineup, 0, team, trainerType, styleOfPlay, config);
    }
    
    private float calcRatings (int type) {
    	return calcRatings (type, ALLSIDES);
    }
    
    private float calcRatings (int type, int side2calc) {
//    	long startTime = new Date().getTime();
    	RatingPredictionParameter params;
    	switch (type) {
		case SIDEDEFENSE:
			params = config.getSideDefenseParameters();
			break;
		case CENTRALDEFENSE:
			params = config.getCentralDefenseParameters();
			break;
		case MIDFIELD:
			params = config.getMidfieldParameters();
			break;
		case SIDEATTACK:
			params = config.getSideAttackParameters();
			break;
		case CENTRALATTACK:
			params = config.getCentralAttackParameters();
			break;
		default:
			return 0;
		}
    	Hashtable<String, Properties> allSections = params.getAllSections();
    	Enumeration<String> allKeys = allSections.keys();
    	double retVal = 0;
    	while (allKeys.hasMoreElements()) {
    		String sectionName = (String)allKeys.nextElement();
    		double curValue = calcPartialRating (params, sectionName, side2calc);
//    		HOLogger.instance().debug(this.getClass(), "PartRating for type "+type+", section "+sectionName+" is "+curValue);
    		retVal += curValue;
    	}
    	retVal = applyCommonProps (retVal, params, RatingPredictionParameter.GENERAL);
//    	HOLogger.instance().debug(this.getClass(), "Prediction ["+config.getPredictionName()+"] FullRating for type "+type+" is "+retVal);    	
//    	long endTime = new Date().getTime();
//    	HOLogger.instance().debug(RatingPredictionManager.class, "calcRatings (T=" + type + ",S=" + side2calc + ")"
//    			+ " took " + (endTime-startTime) + "ms");
    	return (float)retVal;
    }

    private double calcPartialRating (RatingPredictionParameter params, String sectionName, int side2calc) {
    	int skillType = sectionNameToSkillAndSide(sectionName)[0];
    	int sideType = sectionNameToSkillAndSide(sectionName)[1];
    	double retVal = 0;
    	if (skillType == -1 || sideType == -1) {
    		HOLogger.instance().debug(this.getClass(), "parseError: "+sectionName+" resolves to Skill "+skillType+", Side "+sideType);
    		return 0;
    	}
    	double[][] allStk;
    	switch (sideType) {
		case THISSIDE:
			if (side2calc == LEFT)
				allStk = getAllPlayerStrengthLeft(skillType);
			else
				allStk = getAllPlayerStrengthRight(skillType);
			break;
		case OTHERSIDE:
			if (side2calc == LEFT)
				allStk = getAllPlayerStrengthRight(skillType);
			else
				allStk = getAllPlayerStrengthLeft(skillType);
			break;
		case MIDDLE:
			allStk = getAllPlayerStrengthMiddle(skillType);
	   		break;
		default:
			allStk = getAllPlayerStrength(skillType);
			break;
    	}
    	double[][] allWeights = getAllPlayerWeights(params, sectionName);
//    	System.out.println ("calcPartRating: using sidetype="+sideType+", side2calc="+side2calc);
    	// FIXME
    	for (int effPos=0; effPos < allStk.length; effPos++) {
			double curAllSpecWeight = allWeights[effPos][SPEC_ALL];
    		for (int spec=0; spec < SPEC_ALL; spec++) {
    			double curStk = allStk[effPos][spec];
    			double curWeight = allWeights[effPos][spec];
    			if (curStk > 0) {
    				if (curWeight > 0) {
    					
    	  
//    					System.out.println ("addingPlayer (SPEC) @"+effPos+": (skill "+skillType+") stk="+curStk + " * weight="+ curWeight+" = "+curStk * curWeight);
    					
    					// blaghaid: I could not find a better spot to adjust for crowding. For instance the parameters are
    					// set in static method and can't check the number of central players in the lineup. Feel free to
    					// move to a better home.
    					
    					retVal += adjustForCrowding(curStk, effPos) * curWeight;
    				} else {
    	  
//    					System.out.println ("addingPlayer (ALL) @"+effPos+": (skill "+skillType+") stk="+curStk + " * weight="+ curAllSpecWeight +" = "+curStk * curAllSpecWeight);
    					
    					retVal += adjustForCrowding(curStk, effPos) * curAllSpecWeight;
    				}
    			}
    		}
    	}
    	retVal = applyCommonProps (retVal, params, sectionName);
    	return retVal;
    }
    
    private double adjustForCrowding(double stk, int pos) {
    	
    	double weight;
    	
    	switch (pos) {
	    	case IMatchRoleID.CENTRAL_DEFENDER :
	    	case IMatchRoleID.CENTRAL_DEFENDER_OFF :
	    	case IMatchRoleID.CENTRAL_DEFENDER_TOWING :
	    	{
	    		weight = getCrowdingPenalty(CENTRALDEFENSE);
	    		break;
	    	}
	    	case IMatchRoleID.MIDFIELDER :
	    	case IMatchRoleID.MIDFIELDER_DEF :
	    	case IMatchRoleID.MIDFIELDER_OFF :
	    	case IMatchRoleID.MIDFIELDER_TOWING :
	    	{
	    		weight = getCrowdingPenalty(MIDFIELD);
	    		break;
	    	}
	    	case IMatchRoleID.FORWARD :
	    	case IMatchRoleID.FORWARD_DEF :
	    	case IMatchRoleID.FORWARD_TOWING :
	    	{
	    		weight = getCrowdingPenalty(CENTRALATTACK);
	    		break;
	    	}
	    	default :
	    	{
	    		weight = 1;
	    	}
    	}
    	
    	if ( !(weight > 0)) {
    		// It is probably not set in the config
    		weight = 1;
    	}
    	
    	return stk * weight;
    }

    public double applyCommonProps (double inVal, RatingPredictionParameter params, String sectionName) {
    	double retVal = inVal;
        retVal += params.getParam(sectionName, "squareMod", 0) * Math.pow(retVal, 2); // Avoid if possible! 
        retVal += params.getParam(sectionName, "cubeMod", 0) * Math.pow(retVal, 3); // Avoid even more! 

    	if (taktikType == Matchdetails.TAKTIK_WINGS)
    		retVal *= params.getParam(sectionName, "tacticAOW", 1);
    	else if (taktikType == Matchdetails.TAKTIK_MIDDLE)
    		retVal *= params.getParam(sectionName, "tacticAIM", 1);
    	else if (taktikType == Matchdetails.TAKTIK_KONTER)
    		retVal *= params.getParam(sectionName, "tacticCounter", 1);
    	else if (taktikType == Matchdetails.TAKTIK_CREATIVE)
    		retVal *= params.getParam(sectionName, "tacticCreative", 1);
    	else if (taktikType == Matchdetails.TAKTIK_PRESSING)
    		retVal *= params.getParam(sectionName, "tacticPressing", 1);
    	else if (taktikType == Matchdetails.TAKTIK_LONGSHOTS)
    		retVal *= params.getParam(sectionName, "tacticLongshots", 1);

        double teamspirit = (double)stimmung + ((double)substimmung / 5);
        // Alternative 1: TS linear
        retVal *= (1 + params.getParam(sectionName, "teamSpiritMulti", 0)
        			*(teamspirit - 5.5));
        // Alternative 2: TS exponentiell
       	retVal *= Math.pow((teamspirit * params.getParam(sectionName, "teamSpiritPreMulti", 1/4.5)), 
       				params.getParam(sectionName, "teamSpiritPower", 0));
        
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
        int actualPullBackMinute = (pullBackOverride ? 0 : pullBackMinute);
        if (actualPullBackMinute >= 0 && actualPullBackMinute <= 90) {
        	retVal *= 1.0 + (90 - actualPullBackMinute) / 90.0
					* params.getParam(sectionName, "pullback", 0);
        }
        
        retVal *= params.getParam(sectionName, "multiplier", 1);
        retVal += params.getParam(sectionName, "delta", 0);
        
//    	System.out.println ("applyCommonProps: section "+sectionName+", before="+inVal+", after="+retVal);
    	return retVal;
    }
    
    private static String getSpecialtyName (int specialty, boolean withDot) {
    	String retVal = (withDot?".":"");
    	switch (specialty) {
		case SPEC_NONE:
			retVal += "none";
			break;
		case SPEC_TECHNICAL:
			retVal += "technical";
			break;
		case SPEC_QUICK:
			retVal += "quick";
			break;
		case SPEC_POWERFUL:
			retVal += "powerful";
			break;
		case SPEC_UNPREDICTABLE:
			retVal += "unpredictable";
			break;
		case SPEC_HEADER:
			retVal += "header";
			break;
		case SPEC_REGAINER:
			retVal += "regainer";
			break;
		case SPEC_ALL:
//			retVal += "all";
			retVal = "";
			break;
		default:
			return "";
		}
    	return retVal;
    }

//    private static int getSpecialtyByName (String specialtyName) {
//    	specialtyName = specialtyName.toLowerCase();
//    	if (specialtyName.equals("none"))
//    		return SPEC_NONE;
//    	else if (specialtyName.equals("technical"))
//    		return SPEC_TECHNICAL;
//    	else if (specialtyName.equals("quick"))
//    		return SPEC_QUICK;
//    	else if (specialtyName.equals("powerful"))
//    		return SPEC_POWERFUL;
//    	else if (specialtyName.equals("unpredictable"))
//    		return SPEC_UNPREDICTABLE;
//    	else if (specialtyName.equals("header"))
//    		return SPEC_HEADER;
//    	else if (specialtyName.equals("regainer"))
//    		return SPEC_REGAINER;
//    	else if (specialtyName.equals("all") || specialtyName.equals(""))
//    		return SPEC_ALL;
//    	else
//    		return -1;
//    }
    
    private static String getSkillName (int skill) {
    	switch (skill) {
		case GOALKEEPING:
			return "goalkeeping";
		case DEFENDING:
			return "defending";
		case WINGER:
			return "winger";
		case PLAYMAKING:
			return "playmaking";
		case SCORING:
			return "scoring";
		case PASSING:
			return "passing";
		case SETPIECES:
			return "setpieces";
		default:
			return "";
		}
    }

    private static int getSkillByName (String skillName) {
    	skillName = skillName.toLowerCase(java.util.Locale.ENGLISH);
    	if (skillName.equals("goalkeeping"))
    		return GOALKEEPING;
    	else if (skillName.equals("defending"))
    		return DEFENDING;
    	else if (skillName.equals("winger"))
    		return WINGER;
    	else if (skillName.equals("playmaking"))
    		return PLAYMAKING;
    	else if (skillName.equals("scoring"))
    		return SCORING;
    	else if (skillName.equals("passing"))
    		return PASSING;
    	else if (skillName.equals("setpieces"))
    		return SETPIECES;
    	else
    		return -1;
    }

    private static int getSideByName (String sideName) {
    	sideName = sideName.toLowerCase(java.util.Locale.ENGLISH);
    	if (sideName.equals("thisside"))
    		return THISSIDE;
    	else if (sideName.equals("otherside"))
    		return OTHERSIDE;
    	else if (sideName.equals("middle"))
    		return MIDDLE;
    	else if (sideName.equals("") || sideName.equals("allsides"))
    		return ALLSIDES;
    	else
    		return -1;
    }
    
    private static int[] sectionNameToSkillAndSide (String sectionName) {
    	// retArray[0] == skill
    	// retArray[1] == side
    	int[] retArray = new int[2];
    	String skillName = "";
    	String sideName = "";    	
    	if (sectionName.indexOf("_") > -1) {
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
    
    
    public float getCentralAttackRatings()
    {
    	return calcRatings(CENTRALATTACK);
    }


    public float getCentralDefenseRatings()
    {
    	return calcRatings(CENTRALDEFENSE);
    }

    public float getRightAttackRatings()
    {
        return getSideAttackRatings(RIGHT);
    }

    public float getLeftAttackRatings()
    {
        return getSideAttackRatings(LEFT);
    }

    public float getSideAttackRatings (int side) {
    	return calcRatings(SIDEATTACK, side);
    }

    public float getRightDefenseRatings()
    {
        return getSideDefenseRatings(RIGHT);
    }

    public float getLeftDefenseRatings()
    {
        return getSideDefenseRatings(LEFT);
    }

    public float getSideDefenseRatings (int side) {
    	return calcRatings(SIDEDEFENSE, side);
    }

    
    private double getCrowdingPenalty(int pos) {
    	double penalty;
    	RatingPredictionParameter  para = config.getPlayerStrengthParameters();
    	
//    	HOLogger.instance().debug(getClass(), "Parameter file used: " + config.getPredictionName());
    	
    	switch (pos) {
    	case CENTRALDEFENSE :
    		// Central Defender
    		penalty = para.getParam(RatingPredictionParameter.GENERAL, getNumCDs() + "CdMulti");
    		break;
    	case MIDFIELD :
    		// Midfielder
    		penalty = para.getParam(RatingPredictionParameter.GENERAL, getNumIMs() + "MfMulti");
    		break;
    	case CENTRALATTACK :
    		// Forward
    		penalty = para.getParam(RatingPredictionParameter.GENERAL, getNumFWs() + "FwMulti");
    		break;
    	default :
    		penalty = 1;
    	}
    	return penalty;
    }
    

    public static double[][] getAllPlayerWeights (RatingPredictionParameter params, String sectionName) {
    	double[][] weights = new double[IMatchRoleID.NUM_POSITIONS][NUM_SPEC];
		double extraMulti = params.getParam(RatingPredictionParameter.GENERAL, "extraMulti", 0);
		double modCD = params.getParam(sectionName, "allCDs", 1);
		double modWB = params.getParam(sectionName, "allWBs", 1);
		double modIM = params.getParam(sectionName, "allIMs", 1);
		double modWI = params.getParam(sectionName, "allWIs", 1);
		double modFW = params.getParam(sectionName, "allFWs", 1);
    	for (int specialty=0; specialty<NUM_SPEC; specialty++) {
    		String specialtyName = getSpecialtyName(specialty, true);
    		weights[IMatchRoleID.KEEPER][specialty] = params.getParam(sectionName, "keeper" + specialtyName);
    		weights[IMatchRoleID.KEEPER][specialty] += params.getParam(sectionName, "gk" + specialtyName);	// alias for keeper
    		weights[IMatchRoleID.CENTRAL_DEFENDER][specialty] = params.getParam(sectionName, "cd_norm" + specialtyName) * modCD;
    		weights[IMatchRoleID.CENTRAL_DEFENDER][specialty] += params.getParam(sectionName, "cd" + specialtyName) * modCD;	// alias for cd_norm
    		weights[IMatchRoleID.CENTRAL_DEFENDER_OFF][specialty] = params.getParam(sectionName, "cd_off" + specialtyName) * modCD;
    		weights[IMatchRoleID.CENTRAL_DEFENDER_TOWING][specialty] = params.getParam(sectionName, "cd_tw" + specialtyName) * modCD;
    		weights[IMatchRoleID.BACK][specialty] = params.getParam(sectionName, "wb_norm" + specialtyName) * modWB;
    		weights[IMatchRoleID.BACK][specialty] += params.getParam(sectionName, "wb" + specialtyName) * modWB;	// alias for wb_norm
    		weights[IMatchRoleID.BACK_OFF][specialty] = params.getParam(sectionName, "wb_off" + specialtyName) * modWB;
    		weights[IMatchRoleID.BACK_DEF][specialty] = params.getParam(sectionName, "wb_def" + specialtyName) * modWB;
    		weights[IMatchRoleID.BACK_TOMID][specialty] = params.getParam(sectionName, "wb_tm" + specialtyName) * modWB;
    		weights[IMatchRoleID.MIDFIELDER][specialty] = params.getParam(sectionName, "im_norm" + specialtyName) * modIM;
    		weights[IMatchRoleID.MIDFIELDER][specialty] += params.getParam(sectionName, "im" + specialtyName) * modIM;	// alias for im_norm
    		weights[IMatchRoleID.MIDFIELDER_OFF][specialty] = params.getParam(sectionName, "im_off" + specialtyName) * modIM;
    		weights[IMatchRoleID.MIDFIELDER_DEF][specialty] = params.getParam(sectionName, "im_def" + specialtyName) * modIM;
    		weights[IMatchRoleID.MIDFIELDER_TOWING][specialty] = params.getParam(sectionName, "im_tw" + specialtyName) * modIM;
    		weights[IMatchRoleID.WINGER][specialty] = params.getParam(sectionName, "wi_norm" + specialtyName) * modWI;
    		weights[IMatchRoleID.WINGER][specialty] += params.getParam(sectionName, "wi" + specialtyName) * modWI;	// alias for wi_norm
    		weights[IMatchRoleID.WINGER_OFF][specialty] = params.getParam(sectionName, "wi_off" + specialtyName) * modWI;
    		weights[IMatchRoleID.WINGER_DEF][specialty] = params.getParam(sectionName, "wi_def" + specialtyName) * modWI;
    		weights[IMatchRoleID.WINGER_TOMID][specialty] = params.getParam(sectionName, "wi_tm" + specialtyName) * modWI;
    		weights[IMatchRoleID.FORWARD][specialty] = params.getParam(sectionName, "fw_norm" + specialtyName) * modFW;
    		weights[IMatchRoleID.FORWARD][specialty] += params.getParam(sectionName, "fw" + specialtyName) * modFW;	// alias for fw_norm
    		weights[IMatchRoleID.FORWARD_DEF][specialty] = params.getParam(sectionName, "fw_def" + specialtyName) * modFW;
    		weights[IMatchRoleID.FORWARD_TOWING][specialty] = params.getParam(sectionName, "fw_tw" + specialtyName) * modFW;
//    		weights[IMatchRoleID.POS_ZUS_INNENV][specialty] = params.getParam(sectionName, "extra_cd" + specialtyName) * modCD;
//    		weights[IMatchRoleID.POS_ZUS_MITTELFELD][specialty] = params.getParam(sectionName, "extra_im" + specialtyName) * modIM;
//			weights[IMatchRoleID.POS_ZUS_STUERMER][specialty] = params.getParam(sectionName, "extra_fw" + specialtyName) * modFW;
//			weights[IMatchRoleID.POS_ZUS_STUERMER_DEF][specialty] = params.getParam(sectionName, "fw_def" + specialtyName) * modFW;
//			weights[IMatchRoleID.POS_ZUS_INNENV][specialty] += params.getParam(sectionName, "cd_xtra" + specialtyName) * modCD;	// alias for extra_cd
//			weights[IMatchRoleID.POS_ZUS_MITTELFELD][specialty] += params.getParam(sectionName, "im_xtra" + specialtyName) * modIM;	// alias for extra_im
//			weights[IMatchRoleID.POS_ZUS_STUERMER][specialty] += params.getParam(sectionName, "fw_xtra" + specialtyName) * modFW;	// alias for extra_fw
//			if (extraMulti > 0) {
//				weights[IMatchRoleID.POS_ZUS_INNENV][specialty] = weights[IMatchRoleID.CENTRAL_DEFENDER][specialty] * extraMulti; // if extraMulti is defined, use extraMulti*CD
//				weights[IMatchRoleID.POS_ZUS_MITTELFELD][specialty] = weights[IMatchRoleID.MIDFIELDER][specialty] * extraMulti; // if extraMulti is defined, use extraMulti*IM
//				weights[IMatchRoleID.POS_ZUS_STUERMER][specialty] = weights[IMatchRoleID.FORWARD][specialty] * extraMulti; // if extraMulti is defined, use extraMulti*FW
//			}
    	}
    	return weights;
    }
    
    public double[][] getAllPlayerStrength (int skillType) {
    	return getAllPlayerStrength(skillType, true, true, true);
    }

    public double[][] getAllPlayerStrengthLeft (int skillType) {
    	return getAllPlayerStrength(skillType, true, false, false);
    }

    public double[][] getAllPlayerStrengthRight (int skillType) {
    	return getAllPlayerStrength(skillType, false, false, true);
    }

    public double[][] getAllPlayerStrengthMiddle (int skillType) {
    	return getAllPlayerStrength(skillType, false, true, false);
    }

    public int getNumIMs () {
    	int retVal = 0;
    	for(int pos : IMatchRoleID.aFieldMatchRoleID) {
    		Player player = lineup.getPlayerByPositionID(pos);
            if (player != null) {
            	if (pos == IMatchRoleID.rightInnerMidfield || pos == IMatchRoleID.leftInnerMidfield ||
            			pos == IMatchRoleID.centralInnerMidfield)
            		retVal++;
            }
    	}
    	return retVal;
    }

    public int getNumFWs () {
    	int retVal = 0;
    	for(int pos : IMatchRoleID.aFieldMatchRoleID) {
    		Player player = lineup.getPlayerByPositionID(pos);
            if (player != null) {
            	if (pos == IMatchRoleID.rightForward || pos == IMatchRoleID.leftForward ||
            			pos == IMatchRoleID.centralForward)
            		retVal++;
            }
    	}
    	return retVal;
    }

    public int getNumCDs () {
    	int retVal = 0;
    	for(int pos : IMatchRoleID.aFieldMatchRoleID) {
    		Player player = lineup.getPlayerByPositionID(pos);
            if (player != null) {
            	if (pos == IMatchRoleID.rightCentralDefender || pos == IMatchRoleID.leftCentralDefender ||
            			pos == IMatchRoleID.middleCentralDefender)
            		retVal++;
            }
    	}
    	return retVal;
    }
    

    public boolean isLeft (int pos) {
    	// Blaghaid - Taktik removed as parameter, no longer needed in 553
    	
    	if (pos == IMatchRoleID.leftCentralDefender
				|| pos == IMatchRoleID.leftInnerMidfield
				|| pos == IMatchRoleID.leftBack
				|| pos == IMatchRoleID.leftWinger
				|| pos == IMatchRoleID.leftForward)
			return true;
		else
			return false;
    }

    public boolean isRight (int  pos) {
    	// Blaghaid - Taktik removed as parameter, no longer needed in 553
    	
    	if (pos == IMatchRoleID.rightCentralDefender
				|| pos == IMatchRoleID.rightInnerMidfield
				|| pos == IMatchRoleID.rightBack
				|| pos == IMatchRoleID.rightWinger
				|| pos == IMatchRoleID.rightForward)
			return true;
		else
			return false;
    }
    
    public boolean isMiddle (int pos) {
    	// Blaghaid - Taktik removed as parameter, no longer needed in 553
    	// A bunch of logic on single forward being central, etc used to be here. Will not be missed.
    	
    	if (pos == IMatchRoleID.centralForward
				|| pos == IMatchRoleID.centralInnerMidfield
				|| pos == IMatchRoleID.middleCentralDefender)
			return true;
		else
			return false;
    }

    public static float getLoyaltyHomegrownBonus(Player player) {
    	float bonus = 0f;
    	 if (player.isHomeGrown()) {
         	bonus += config.getPlayerStrengthParameters().getParam(RatingPredictionParameter.GENERAL, "homegrownbonus");
         }
         
         // Loyalty bonus
         bonus += (float)config.getPlayerStrengthParameters().getParam(RatingPredictionParameter.GENERAL, "loyaltyMax") 
         			* player.getLoyalty()
         			/ (float)config.getPlayerStrengthParameters().getParam(RatingPredictionParameter.GENERAL, "loyaltySkillMax");
    	
    	
    	return bonus;
    }
    
    public double[][] getAllPlayerStrength (int skillType, boolean useLeft, boolean useMiddle, boolean useRight) {
    	double[][] retArray = new double[IMatchRoleID.NUM_POSITIONS][SPEC_ALL];
//    	System.out.println ("getAllPlayerStrength: st="+skillType+", l="+useLeft+", m="+useMiddle+", r="+useRight);
        for(int pos : IMatchRoleID.aFieldMatchRoleID) {
            Player player = lineup.getPlayerByPositionID(pos);
            byte taktik = lineup.getTactic4PositionID(pos);
            if(player != null) {
//            	System.out.println ("getAllPlayerStrength."+pos+", id="+player.getSpielerID()+", taktik="+taktik);
            	// Check sides
            	if (!useLeft && isLeft(pos)
            			|| !useMiddle && isMiddle(pos) // XXXX Could this bomb side contribution for the central ones?
            			|| !useRight && isRight(pos)) {
            		continue;
            	} else {
            		int specialty = player.getSpezialitaet();
            		// To avoid ArrayOutOfBound exception for unsupported/new specialties
            		if (specialty < SPEC_NONE || specialty >= SPEC_ALL)
            			specialty = SPEC_NONE;

            		// Old code had lots of checks for extras here (extra central defender, etc)
            		
            		else switch (pos) {
            		case IMatchRoleID.keeper:
            			retArray[IMatchRoleID.KEEPER][specialty] += calcPlayerStrength(player, skillType);
            			break;
            		case IMatchRoleID.rightCentralDefender:
            		case IMatchRoleID.leftCentralDefender:
            		case IMatchRoleID.middleCentralDefender:
            			if (taktik == IMatchRoleID.NORMAL)
            				retArray[IMatchRoleID.CENTRAL_DEFENDER][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.OFFENSIVE)
            				retArray[IMatchRoleID.CENTRAL_DEFENDER_OFF][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.TOWARDS_WING)
            				retArray[IMatchRoleID.CENTRAL_DEFENDER_TOWING][specialty] += calcPlayerStrength(player, skillType);
            			break;
            		case IMatchRoleID.rightBack:
            		case IMatchRoleID.leftBack:
            			if (taktik == IMatchRoleID.NORMAL)
            				retArray[IMatchRoleID.BACK][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.OFFENSIVE)
            				retArray[IMatchRoleID.BACK_OFF][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.DEFENSIVE)
            				retArray[IMatchRoleID.BACK_DEF][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.TOWARDS_MIDDLE)
            				retArray[IMatchRoleID.BACK_TOMID][specialty] += calcPlayerStrength(player, skillType);
            			break;
            		case IMatchRoleID.rightWinger:
            		case IMatchRoleID.leftWinger:
            			if (taktik == IMatchRoleID.NORMAL)
            				retArray[IMatchRoleID.WINGER][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.OFFENSIVE)
            				retArray[IMatchRoleID.WINGER_OFF][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.DEFENSIVE)
            				retArray[IMatchRoleID.WINGER_DEF][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.TOWARDS_MIDDLE)
            				retArray[IMatchRoleID.WINGER_TOMID][specialty] += calcPlayerStrength(player, skillType);
            			break;
            		case IMatchRoleID.rightInnerMidfield:
            		case IMatchRoleID.leftInnerMidfield:
            		case IMatchRoleID.centralInnerMidfield:
            			if (taktik == IMatchRoleID.NORMAL)
            				retArray[IMatchRoleID.MIDFIELDER][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.OFFENSIVE)
            				retArray[IMatchRoleID.MIDFIELDER_OFF][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.DEFENSIVE)
            				retArray[IMatchRoleID.MIDFIELDER_DEF][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.TOWARDS_WING)
            				retArray[IMatchRoleID.MIDFIELDER_TOWING][specialty] += calcPlayerStrength(player, skillType);
            			break;
            		case IMatchRoleID.rightForward:
            		case IMatchRoleID.leftForward:
            		case IMatchRoleID.centralForward:
            			if (taktik == IMatchRoleID.NORMAL)
            				retArray[IMatchRoleID.FORWARD][specialty] += calcPlayerStrength(player, skillType);
            			else if (taktik == IMatchRoleID.DEFENSIVE) {
            				retArray[IMatchRoleID.FORWARD_DEF][specialty] += calcPlayerStrength(player, skillType);
            			} else if (taktik == IMatchRoleID.TOWARDS_WING)
            				retArray[IMatchRoleID.FORWARD_TOWING][specialty] += calcPlayerStrength(player, skillType);
            			break;
            		}
            	}
            }
        }
        return retArray;
    }
    
    public float getMFRatings ()
    {
    	return calcRatings(MIDFIELD);
    }

    public static float calcPlayerStrength(Player player, int skillType) {
    	return calcPlayerStrength(player, skillType, true);
    }

    public static float calcPlayerStrength(Player player, int skillType, boolean useForm) {
        double retVal = 0.0F;
        try
        {
            Object lastLvlUp[];
            float skill;
            float subSkill;
            skill = player.getValue4Skill4(skillType);
            float subskillFromDB = (float) player.getSubskill4Pos(skillType);
//            System.out.println ("t="+skillType+", o="+manualOffset+", s="+subskillFromDB);
            /**
             * If we know the last level up date from this player or
             * the user has set an offset manually -> use this sub/offset
             */
            if (subskillFromDB > 0 || 
            		(lastLvlUp = player.getLastLevelUp(skillType)) != null && (Timestamp)lastLvlUp[0] != null && ((Boolean)lastLvlUp[1]).booleanValue())
                subSkill = player.getSubskill4Pos(skillType);
            else
            	/**
            	 * Try to guess the sub based on the skill level
            	 */
              subSkill = getSubDeltaFromConfig (config.getPlayerStrengthParameters(), getSkillName(skillType), (int)skill);
            // subSkill>1, this should not happen
            if (subSkill > 1)
            	subSkill = 1;
            skill = skill + subSkill;
            
            // Add loyalty and homegrown bonuses
            skill += getLoyaltyHomegrownBonus(player);
               
            retVal = calcPlayerStrength(config.getPlayerStrengthParameters(), 
            		getSkillName(skillType), player.getKondition(), player.getErfahrung(), skill, player.getForm(), useForm);
//            System.out.println("calcPlayerStrength for "+player.getSpielerID()
//            		+", st="+skillType+", s="+skill+", k="+player.getKondition()
//            		+", xp="+player.getErfahrung()+", f="+player.getForm()+": "+retVal);
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        return (float)retVal;    	
    }

    private static float getSubDeltaFromConfig (RatingPredictionParameter params, String sectionName, int skill) {
    	String useSection = sectionName;
    	if (!params.hasSection(sectionName))
    		useSection = RatingPredictionParameter.GENERAL;
    	float delta = (float)params.getParam(useSection, "skillSubDeltaForLevel"+skill, 0);
//    	System.out.println(delta);
    	return delta;    	
    }
    
    public static double calcPlayerStrength (RatingPredictionParameter params,
    		String sectionName, double stamina, double xp, double skill, double form, boolean useForm) {
//    	long startTime = new Date().getTime();
    	// If config changed, we have to clear the cache
		boolean forceRefresh = true; //FIXME this should be necessary only in debug mode
    	if (!playerStrengthCache.containsKey("lastRebuild") 
    			|| playerStrengthCache.get("lastRebuild") < config.getLastParse() || forceRefresh) {
    		HOLogger.instance().debug(RatingPredictionManager.class, "RPM tainted, clearing cache!");
    		playerStrengthCache.clear();
    		playerStrengthCache.put ("lastRebuild", new Double(new Date().getTime()));
    	}
    	String key = params.toString() + "|" + sectionName + "|" + stamina + "|" + xp + "|" + skill + "|" + form + "|" + useForm;
    	if (playerStrengthCache.containsKey(key)) {
//    		HOLogger.instance().debug(RatingPredictionManager.class, "Using from cache: " + key);
    		return playerStrengthCache.get(key);
    	}
    	double stk = 0;
    	String useSection = sectionName;
    	if (!params.hasSection(sectionName))
    		useSection = RatingPredictionParameter.GENERAL;

    	form += params.getParam(useSection, "formDelta", 0);

    	// Compute Xp Effect
		if (params.getParam(useSection, "multiXpLog", 99) != 99) {xp = Math.log(params.getParam(useSection, "multiXpLog", 0)) * Math.log(xp);}
		else {
			xp += params.getParam(useSection, "xpDelta", 0);
			xp = Math.max(xp, params.getParam(useSection, "xpMin", 0));
			xp = Math.min(xp, params.getParam(useSection, "xpMax", 99999));
			xp *= params.getParam(useSection, "xpMultiplier", 1);
			xp = Math.pow(xp, params.getParam(useSection, "xpPower", 1));
			xp *= params.getParam(useSection, "finalXpMultiplier", 1);
			xp += params.getParam(useSection, "finalXpDelta", 0);
		}

		skill += params.getParam(useSection, "skillDelta", 0);
    	skill = Math.max(skill, params.getParam(useSection, "skillMin", 0));
		skill = Math.min(skill, params.getParam(useSection, "skillMax", 99999));
		skill *= params.getParam(useSection, "skillMultiplier", 1);
		skill = Math.pow(skill, params.getParam(useSection, "skillPower", 1));

    	form = Math.max(form, params.getParam(useSection, "formMin", 0));
    	form = Math.min(form, params.getParam(useSection, "formMax", 99999));
    	form *= params.getParam(useSection, "formMultiplier", 1);
    	form = Math.pow(form, params.getParam(useSection, "formPower", 1));


    	if (params.getParam(useSection, "skillLog", 0) > 0)
    		skill = Math.log(skill)/Math.log(params.getParam(useSection, "skillLog", 0));
    	if (params.getParam(useSection, "formLog", 0) > 0)
    		form = Math.log(form)/Math.log(params.getParam(useSection, "formLog", 0));


    	skill *= params.getParam(useSection, "finalSkillMultiplier", 1);
    	form *= params.getParam(useSection, "finalFormMultiplier", 1);

    	
    	skill += params.getParam(useSection, "finalSkillDelta", 0);
    	form += params.getParam(useSection, "finalFormDelta", 0);

    	
    	stk = skill;
    	if (useForm && params.getParam(useSection, "resultMultiForm", 0) > 0)
    		stk *= params.getParam(useSection, "resultMultiForm", 0) * form;
    	if (params.getParam(useSection, "resultMultiXp", 0) > 0)
    		stk *= params.getParam(useSection, "resultMultiXp", 0) * xp;
		stk += params.getParam(useSection, "resultAddXp", 0) * xp;

   		if (useForm)
   			stk += params.getParam(useSection, "resultAddForm", 0) * form;

//		HOLogger.instance().debug(RatingPredictionManager.class, "Adding to cache: " + key + "=" + stk);

   		playerStrengthCache.put(key, stk);
//    	long endTime = new Date().getTime();
//    	HOLogger.instance().debug(RatingPredictionManager.class, "calcPlayerStrength (" 
//    			+ "SN=" + sectionName + ",ST" + stamina + ",XP" + xp + ",SK" + skill + ",FO" + form + ",uF" + useForm+ ") took " + (endTime-startTime) + "ms");

		return stk;
    }

    private void init(Team team, short trainerType, int styleOfPlay)
    {
        try
        {
            this.trainerType = trainerType;
            this.attitude = (short)lineup.getAttitude();
            this.heimspiel = lineup.getLocation();
            this.taktikType = (short)lineup.getTacticType();
            this.stimmung = (short)team.getStimmungAsInt();
            this.substimmung = (short)team.getSubStimmung();
            this.selbstvertrauen = (short)team.getSelbstvertrauenAsInt();
            this.pullBackMinute = lineup.getPullBackMinute();
            this.pullBackOverride = lineup.isPullBackOverride();
            this.styleOfPlay = styleOfPlay;
            return;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }

	 /**
	 * Returns the stamina effect per minute from tEnter tp tExit
	 * @param stamina: player stamina
	 * @param tEnter: at which minute the player entered the game
     * @param tExit: at which minute the player exited the game
	 */
	public static double[] GetStaminaEffect(double stamina, int tEnter, int tExit, boolean isTactitPressing){
		boolean isHighStaminaPlayer = false;

		int nb_events = tExit - tEnter;
		if ( (tEnter < 45) && (tExit > 45) ) nb_events += 1;
		if ( (tEnter < 90) && (tExit > 90) ) nb_events += 1;
		double[] result = new double[nb_events];

		double energy = 102 + 23/7 * stamina;  // energy when entering the field for player whose stamina < 8
		double P = isTactitPressing ? 1.1 : 1.0;
		double energyLossPerMinuteLS = P * (5.95 - 27*stamina/70);
		double energyLossPerMinuteHS = -3.25 * P;

		if 	(stamina >= 8)
		{
			isHighStaminaPlayer = true;
			energy = 125 + (stamina-7) *100 /7; // energy when entering the field for player whose stamina >= 8
		}

		for(int t=tEnter; t<tExit; t++)
		{
			if (t == 46) energy += 18.75;  // Energy recovery during half-time
			else if (t == 91) energy += 6.25;  // Energy recovery before extra-time
			else if (isHighStaminaPlayer) energy += energyLossPerMinuteHS;
			else energy += energyLossPerMinuteLS;
			result[t-tEnter] = energy;
		}
		return result;
	}

    private double getTrainerEffect(double defensive, double offensive, double neutral) {
    	
    	// styleOfPlay * 0.1 gives us the fraction of the distance we need to go from
    	// neutral to either defensive or offensive depending on what the style is.
    	
    	double outlier;
    	
    	if (styleOfPlay >= 0) {
    		outlier = offensive;
    	} else {
    		outlier = defensive;
    	}
    	
    	double effect = neutral + (Math.abs(styleOfPlay) * 0.1)*(outlier - neutral);
    	
    	return effect;
    }
    
    
    /************************************************************************* 
     * 
     * TacticLevel Functions
     * (AIW, AOW, Counter...)
     * 
     *************************************************************************/
    
    /**
     * get the tactic level for AiM / AoW
     *
     * @return tactic level
     */
    public float getTacticLevelAowAim()
    {
    	RatingPredictionParameter params = config.getTacticsParameters();
    	double retVal = 0;
    	float passing = 0;
        for(int i : IMatchRoleID.aFieldMatchRoleID)
        {
            Player ispieler = lineup.getPlayerByPositionID(i);
            byte taktik = lineup.getTactic4PositionID(i);
            if(ispieler != null) {
            	passing =  calcPlayerStrength(ispieler, PASSING);
            	// Zus. MF/IV/ST
                if(taktik == 7 || taktik == 6 || taktik == 5)
                    passing *= params.getParam("extraMulti", 1.0);
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
        float deDefender = 0.0F;
        float psDefender = 0.0F;
        double playerContribution = 0d;
    	RatingPredictionParameter params = config.getTacticsParameters();
    	double retVal = 0;
        for(int pos = IMatchRoleID.rightBack; pos <= IMatchRoleID.leftBack; pos++)
        {
        	playerContribution = 0d;
            Player player = lineup.getPlayerByPositionID(pos);
            if(player != null) {
            		playerContribution = (params.getParam("counter", "multiPs", 1.0) * calcPlayerStrength(player, PASSING));
            		playerContribution += (params.getParam("counter", "multiDe", 1.0) * calcPlayerStrength(player, DEFENDING));
            		playerContribution *= params.getParam("counter", "playerPostMulti", 1.0);
            		playerContribution += params.getParam("counter", "playerPostDelta", 0);
            		retVal += playerContribution;
            }
        }
        retVal *= params.getParam("counter", "postMulti", 1.0);
        retVal += params.getParam("counter", "postDelta", 0);
    	retVal = applyCommonProps (retVal, params, "counter");
    	retVal = applyCommonProps (retVal, params, RatingPredictionParameter.GENERAL);
    	return (float)retVal;
    }

    /**
     * get the tactic level for pressing
     *
     * @return tactic level
     */
    public final float getTacticLevelPressing() {
    	RatingPredictionParameter params = config.getTacticsParameters();
    	double retVal = 0;
        for(int pos = IMatchRoleID.startLineup + 1; pos < IMatchRoleID.startReserves; pos++)
        {
            float defense = 0.0F;
            Player player = lineup.getPlayerByPositionID(pos);
            if(player != null) {
            	defense = calcPlayerStrength(player, DEFENDING);
                if (player.getSpezialitaet() == PlayerSpeciality.POWERFUL) {
                	defense *= 2;
                }
                retVal += defense;
            }
        }

        retVal *= params.getParam("pressing", "postMulti", 1.0);
        retVal += params.getParam("pressing", "postDelta", 0);
    	retVal = applyCommonProps (retVal, params, "pressing");
    	retVal = applyCommonProps (retVal, params, RatingPredictionParameter.GENERAL);
    	return (float)retVal;
    }

    /**
     * get the tactic level for long shots
     *
     * @return tactic level
     */
    public final float getTacticLevelLongShots() {
       	RatingPredictionParameter params = config.getTacticsParameters();
    	double retVal = 0;
        for(int pos = IMatchRoleID.startLineup +1; pos < IMatchRoleID.startReserves; pos++)
        {
            float scoring = 0.0F;
            float setpieces = 0.0F;
            Player player = lineup.getPlayerByPositionID(pos);
            if(player != null) {
            	scoring = 3*calcPlayerStrength(player, SCORING);
            	setpieces = calcPlayerStrength(player, SETPIECES);
                retVal += scoring;
                retVal += setpieces;
            }
        }
        
        retVal *= params.getParam("longshots", "postMulti", 1.0);
        retVal += params.getParam("longshots", "postDelta", 0);
    	retVal = applyCommonProps (retVal, params, "longshots");
    	retVal = applyCommonProps (retVal, params, RatingPredictionParameter.GENERAL);
    	return (float)retVal;
    }
}
