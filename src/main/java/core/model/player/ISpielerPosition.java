package core.model.player;

/**
 * 
 * Interface giving the constants for player positions
 * 
 * Updated to 5-5-3 with new Position IDs by Blaghaid. Role IDs as of v1.5 of the match order api.
 * Old versions are kept around (with new names) for conversion purposes
 * 
 * @author thomas.werth
 */
/**
 * Interface Konst Definition for Pos Values
 */
public interface ISpielerPosition {
    //~ Static fields/initializers -----------------------------------------------------------------

    //Konstanten für die ID's

    /** ht-Keeper-Field */
    public static final int keeper = 100;

    /** ht-rightBack-Field */
    public static final int rightBack = 101;

    /** ht-insideBack1-Field */
    public static final int rightCentralDefender = 102;

    /** Middle Central defender */
    public static final int middleCentralDefender = 103;

    /** ht-insideBack2-Field */
    public static final int leftCentralDefender = 104;

    /** ht-leftBack-Field */
    public static final int leftBack = 105;

    /** ht-rightWinger-Field */
    public static final int rightWinger = 106;

    /** ht-insideMid1-Field */
    public static final int rightInnerMidfield = 107;
    
    /** Middle inner midfield */
    public static final int centralInnerMidfield = 108;

    /** ht-insideMid2-Field */
    public static final int leftInnerMidfield = 109;
    
    /** ht-leftWinger-Field */
    public static final int leftWinger = 110;

    /** ht-forward1-Field */
    public static final int rightForward = 111;

    /** Middle forward */
    public static final int centralForward = 112;
    
    /** ht-forward2-Field */
    public static final int leftForward = 113;

    /** ht-substKeeper-Field */
    public static final int substKeeper = 114;

    /** ht-substBack-Field */
    public static final int substDefender = 115;

    /** ht-substInsideMid-Field */
    public static final int substInnerMidfield = 116;

    /** ht-substWinger-Field */
    public static final int substWinger = 117;

    /** ht-substForward-Field */
    public static final int substForward = 118;

    /** ht-setpieces-Field */
    public static final int setPieces = 17;

    /** ht-Capitan-Field */
    public static final int captain = 18;

    /** ht-subsituded-Field */

    //oder grösser
    public static final int ausgewechselt = 19;

    /** Last field to check for substituted. Needed after 553 due to higher role IDs */
    public static final int ausgewechseltEnd = 21;

    public static final int penaltyTaker1 = 22;
    public static final int penaltyTaker2 = 23;
    public static final int penaltyTaker3 = 24;
    public static final int penaltyTaker4 = 25;
    public static final int penaltyTaker5 = 26;
    public static final int penaltyTaker6 = 27;
    public static final int penaltyTaker7 = 28;
    public static final int penaltyTaker8 = 29;
    public static final int penaltyTaker9 = 30;
    public static final int penaltyTaker10 = 31;
    public static final int penaltyTaker11 = 32;
    
    
    /** ab welccher PositionsID gehört Pos zur Reserve Bank */

    //First id of the reserves
    public static final int startReserves = 114;

    //Start of player list is not a good idea to hard code at 0 either
    public static final int startLineup = 100;
    
    //Konstanten fürs verhalten

    /** Normal */
    public static final byte NORMAL = 0;

    /** Offensive */
    public static final byte OFFENSIVE = 1;

    /** Defensive */
    public static final byte DEFENSIVE = 2;

    /** towards center */
    public static final byte TOWARDS_MIDDLE = 3;

    /** towards wing */
    public static final byte TOWARDS_WING = 4;
    
    /** additional forward */
    public static final byte OLD_EXTRA_FORWARD = 5;
    
    /** additional midfield */
    public static final byte OLD_EXTRA_MIDFIELD = 6;
    
    /** additional defender */
    public static final byte OLD_EXTRA_DEFENDER = 7;
    
    /** additional extra defensive forward */
    public static final byte OLD_EXTRA_DEFENSIVE_FORWARD = 8;
    
  
    //Constants for positions

    /** Number of Positions [0 (Keeper) - 24 (extraCD) => 25]
     * With 553 it is no more extras, so only 0 (keeper) - 21 (ftw) => 21 **/
    public static final byte NUM_POSITIONS = 22;
    
    /** Keeper */
    public static final byte KEEPER = 0;

    /** Central Defender */
    public static final byte CENTRAL_DEFENDER = 1;

    /** Central Defender offensive */
    public static final byte CENTRAL_DEFENDER_OFF = 2;

    /** Central Defender toward wing */

    public static final byte CENTRAL_DEFENDER_TOWING = 3;

    /** Wingback */
    public static final byte BACK = 4;

    /** Wingback offensive */
    public static final byte BACK_OFF = 5;

    /** Wingback toward center */
    public static final byte BACK_TOMID = 6;

    /** Wingback defensive */

    public static final byte BACK_DEF = 7;

    /** Midfield */

    public static final byte MIDFIELDER = 8;

    /** Midfield offensive */
    public static final byte MIDFIELDER_OFF = 9;

    /** Midfield defensive */
    public static final byte MIDFIELDER_DEF = 10;

    /** Midfield towards Wing */
    public static final byte MIDFIELDER_TOWING = 11;

    /** Winger */

    public static final byte WINGER = 12;

    /** Winger offensive */
    public static final byte WINGER_OFF = 13;

    /** Winger defensive */
    public static final byte WINGER_DEF = 14;

    /** Winger towards Midfield */
    public static final byte WINGER_TOMID = 15;

    /** Forward */
    public static final byte FORWARD = 16;

    /** Forward defensive */
    public static final byte FORWARD_DEF = 17;
    
    /** substited */
    public static final byte SUBSTITUTED1 = 18;

    /** substited */
    public static final byte SUBSTITUTED2 = 19;

    /** substited */
    public static final byte SUBSTITUTED3 = 20;

    /** forward toward wing Thorsten Dietz 25-09-2005 */
    public static final byte FORWARD_TOWING = 21;
    

    /** Unknown */
    public static final byte UNKNOWN = -1;

    /** Coach */
    public static final byte COACH = 99;
    
    // The old role IDs used for mapping old data to new format
    
    public static final byte oldKeeper = 1;
    
    public static final byte oldRightBack = 2;
   
    public static final byte oldRightCentralDefender = 3;
	
    public static final byte oldLeftCentralDefender = 4;
    
    public static final byte oldLeftBack = 5;
    
    public static final byte oldRightWinger = 6;
    
    public static final byte oldRightInnerMidfielder = 7;
    
    public static final byte oldLeftInnerMidfielder = 8;
    
    public static final byte oldLeftWinger = 9;
    
    public static final byte oldRightForward = 10;
    
    public static final byte oldLeftForward = 11;
    
    public static final byte oldSubstKeeper = 12;
    
    public static final byte oldSubstDefender = 13;

    public static final byte oldSubstMidfielder = 14;
    
    public static final byte oldSubstWinger = 15;
    
    public static final byte oldSubstForward = 16;
    
  
//    /** Get the position byte back **/
//    public byte getPosition();
//    
//    /** Set the position ID **/
//    public void setId(int m_iId);
//    
//    /** Get the individual order ID **/
//    public byte getTaktik();
//    
//    /** Set the individual order ID **/
//    public void setTaktik(byte m_bTaktik);
    
    
    @Deprecated
    public static final int insideBack1 = rightCentralDefender;
    @Deprecated
    public static final int insideBack2 = leftCentralDefender;
    @Deprecated
    public static final int insideMid1 = rightInnerMidfield;
    @Deprecated
    public static final int insideMid2 = leftInnerMidfield;
    @Deprecated
    public static final int forward1 = rightForward;
    @Deprecated
    public static final int forward2 = leftForward;
    @Deprecated
    public static final int substBack = substDefender;
    @Deprecated
    public static final int substInsideMid = substInnerMidfield;
    @Deprecated
    public static final int standard = setPieces;
    @Deprecated
    public static final int spielfuehrer = captain;
    
    @Deprecated
    public static final int beginnReservere = 12;

    @Deprecated
    public static final byte OFFENSIV = OFFENSIVE;
    @Deprecated
    public static final byte DEFENSIV = DEFENSIVE;
    @Deprecated
    public static final byte ZUR_MITTE = TOWARDS_MIDDLE;
    @Deprecated
    public static final byte NACH_AUSSEN = TOWARDS_WING;
    @Deprecated
    public static final byte ZUS_STUERMER = OLD_EXTRA_FORWARD;
    @Deprecated
    public static final byte ZUS_MITTELFELD = OLD_EXTRA_MIDFIELD;
    @Deprecated
    public static final byte ZUS_INNENV = OLD_EXTRA_DEFENDER;
    @Deprecated
    public static final byte ZUS_STUERMER_DEF = OLD_EXTRA_DEFENSIVE_FORWARD;

    @Deprecated
    public static final byte TORWART = KEEPER;
    @Deprecated
    public static final byte INNENVERTEIDIGER = CENTRAL_DEFENDER;
    @Deprecated
    public static final byte INNENVERTEIDIGER_OFF = CENTRAL_DEFENDER_OFF;
    @Deprecated
    public static final byte INNENVERTEIDIGER_AUS = CENTRAL_DEFENDER_TOWING;
    @Deprecated
    public static final byte AUSSENVERTEIDIGER = BACK;
    @Deprecated
    public static final byte AUSSENVERTEIDIGER_OFF = BACK_OFF;
    @Deprecated
    public static final byte AUSSENVERTEIDIGER_IN = BACK_TOMID;
    @Deprecated
    public static final byte AUSSENVERTEIDIGER_DEF = BACK_DEF;
    @Deprecated
    public static final byte MITTELFELD = MIDFIELDER;
    @Deprecated
    public static final byte MITTELFELD_OFF = MIDFIELDER_OFF;
    @Deprecated
    public static final byte MITTELFELD_DEF = MIDFIELDER_DEF;
    @Deprecated
    public static final byte MITTELFELD_AUS = MIDFIELDER_TOWING;
    @Deprecated
    public static final byte FLUEGELSPIEL = WINGER;
    @Deprecated
    public static final byte FLUEGELSPIEL_OFF = WINGER_OFF;
    @Deprecated
    public static final byte FLUEGELSPIEL_DEF = WINGER_DEF;
    @Deprecated
    public static final byte FLUEGELSPIEL_IN = WINGER_TOMID;
    @Deprecated
    public static final byte STURM = FORWARD;
    @Deprecated
    public static final byte STURM_DEF = FORWARD_DEF;
    @Deprecated
    public static final byte AUSGEWECHSELT1 = SUBSTITUTED1;
    @Deprecated
    public static final byte AUSGEWECHSELT2 = SUBSTITUTED2;
    @Deprecated
    public static final byte AUSGEWECHSELT3 = SUBSTITUTED3;
    @Deprecated
    public static final byte STURM_AUS = FORWARD_TOWING;
    @Deprecated
    public static final byte POS_ZUS_STUERMER = 22;
    @Deprecated
    public static final byte POS_ZUS_STUERMER_DEF = 25;
    @Deprecated
    public static final byte POS_ZUS_MITTELFELD = 23;
    @Deprecated
    public static final byte POS_ZUS_INNENV = 24;
    @Deprecated
    public static final byte UNBESTIMMT = UNKNOWN;
    @Deprecated
    public static final byte TRAINER = COACH;
    
}
