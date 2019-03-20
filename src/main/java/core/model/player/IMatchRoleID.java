package core.model.player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * MatchRoleID according to CHPP documentation.
 * 
 * @author aksSolace
 */
public interface IMatchRoleID {

    //MatchRoleID and MatchBehaviourID according to CHPP documentation
    // Used for sending Lineup

    // ===========  MatchRoleID ================
    // https://www93.hattrick.org/Community/CHPP/NewDocs/DataTypes.aspx#matchRoleID

    int keeper = 100; // Keeper
    int rightBack = 101; // Right WB
    int rightCentralDefender = 102; // Right central defender
    int middleCentralDefender = 103; //	Middle central defender
    int leftCentralDefender = 104; // Left central defender
    int leftBack = 105; // Left WB
    int rightWinger = 106; // Right winger
    int rightInnerMidfield = 107; // Right inner midfield
    int centralInnerMidfield = 108; // 	Middle inner midfield
    int leftInnerMidfield = 109; // Left inner midfield
    int leftWinger = 110; // Left winger
    int rightForward = 111; // Right forward
    int centralForward = 112; // Middle forward
    int leftForward = 113; //Left forward
    List<Integer> aFieldMatchRoleID = IntStream.rangeClosed(100, 113).boxed().collect(Collectors.toList());

    // Subs
    int substGK1 = 200; // 	Substitution (Keeper)
    int substCD1 = 201; // Substitution (Central defender)
    int substWB1 = 202; // Substitution (WB)
    int substIM1 = 203; // Substitution (Inner midfielder)
    int substFW1 = 204; // 	Substitution (Forward)
    int substWI1 = 205; // 	Substitution (Winger)
    int substXT1 = 206; // 	Substitution (Extra)
    List<Integer> aSubstitutesMatchRoleID = IntStream.rangeClosed(200, 206).boxed().collect(Collectors.toList());
    int substGK2 = 207; // 	Backup (Keeper)
    int substCD2 = 208; // Backup (Central defender)
    int substWB2 = 209; // Backup (WB)
    int substIM2 = 210; // Backup (Inner midfielder)
    int substFW2 = 211; // 	Backup (Forward)
    int substWI2 = 212; // 	Backup (Winger)
    int substXT2 = 213; // 	Backup (Extra)
    List<Integer> aBackupssMatchRoleID = IntStream.rangeClosed(207, 213).boxed().collect(Collectors.toList());

    // SetPieces Taker and Captain
    int setPieces = 17;
    int captain = 18;

    // replaced Players
    int FirstPlayerReplaced = 19; // Replaced Player #1
    int SecondPlayerReplaced = 20; // Replaced Player #2
    int ThirdPlayerReplaced = 21; // Replaced Player #3

    // penalty Takers
    int penaltyTaker1 = 22;
    int penaltyTaker2 = 23;
    int penaltyTaker3 = 24;
    int penaltyTaker4 = 25;
    int penaltyTaker5 = 26;
    int penaltyTaker6 = 27;
    int penaltyTaker7 = 28;
    int penaltyTaker8 = 29;
    int penaltyTaker9 = 30;
    int penaltyTaker10 = 31;
    int penaltyTaker11 = 32;
    
    
//    /** ab welccher PositionsID gehört Pos zur Reserve Bank */
//
    //First id of the reserves
    int startReserves = 200;

    // Start of player list is not a good idea to hard code at 0 either
    int startLineup = 100;
//


    // ===========  MatchBehaviourID ================
    // https://www93.hattrick.org/Community/CHPP/NewDocs/DataTypes.aspx#matchBehaviourID

    byte NORMAL = 0;
    byte OFFENSIVE = 1;
    byte DEFENSIVE = 2;
    byte TOWARDS_MIDDLE = 3;
    byte TOWARDS_WING = 4;
    byte OLD_EXTRA_FORWARD = 5;
    byte OLD_EXTRA_MIDFIELD = 6;
    byte OLD_EXTRA_DEFENDER = 7;
    byte OLD_EXTRA_DEFENSIVE_FORWARD = 8;
    
  
    //Constants to identify the 22 possible combination of position and Behaviour

    byte NUM_POSITIONS = 22;
    byte KEEPER = 0;
    byte CENTRAL_DEFENDER = 1;
    byte CENTRAL_DEFENDER_OFF = 2;
    byte CENTRAL_DEFENDER_TOWING = 3;
    byte BACK = 4;
    byte BACK_OFF = 5;
    byte BACK_TOMID = 6;
    byte BACK_DEF = 7;
    byte MIDFIELDER = 8;
    byte MIDFIELDER_OFF = 9;
    byte MIDFIELDER_DEF = 10;
    byte MIDFIELDER_TOWING = 11;
    byte WINGER = 12;
    byte WINGER_OFF = 13;
    byte WINGER_DEF = 14;
    byte WINGER_TOMID = 15;
    byte FORWARD = 16;
    byte FORWARD_DEF = 17;
    byte SUBSTITUTED1 = 18;
    byte SUBSTITUTED2 = 19;
    byte SUBSTITUTED3 = 20;
    byte FORWARD_TOWING = 21;
    byte UNKNOWN = -1;
    byte EXTRA = 30;
    byte COACH = 99;

    
    // The old role IDs used for mapping old data to new format
     byte oldKeeper = 1;
     byte oldRightBack = 2;
     byte oldRightCentralDefender = 3;
     byte oldLeftCentralDefender = 4;
     byte oldLeftBack = 5;
     byte oldRightWinger = 6;
     byte oldRightInnerMidfielder = 7;
     byte oldLeftInnerMidfielder = 8;
     byte oldLeftWinger = 9;
     byte oldRightForward = 10;
     byte oldLeftForward = 11;
     byte oldSubstKeeper = 12;
     byte oldSubstDefender = 13;
     byte oldSubstMidfielder = 14;
     byte oldSubstWinger = 15;
     byte oldSubstForward = 16;
    
  
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
    
    
//    @Deprecated
//    public static final int insideBack1 = rightCentralDefender;
//    @Deprecated
//    public static final int insideBack2 = leftCentralDefender;
//    @Deprecated
//    public static final int insideMid1 = rightInnerMidfield;
//    @Deprecated
//    public static final int insideMid2 = leftInnerMidfield;
//    @Deprecated
//    public static final int forward1 = rightForward;
//    @Deprecated
//    public static final int forward2 = leftForward;
//    @Deprecated
//    public static final int substBack = substDefender;
//    @Deprecated
//    public static final int substInsideMid = substInnerMidfield;
//    @Deprecated
//    public static final int standard = setPieces;
//    @Deprecated
//    public static final int spielfuehrer = captain;
//
//    @Deprecated
//    public static final int beginnReservere = 12;
//
//    @Deprecated
//    public static final byte OFFENSIV = OFFENSIVE;
//    @Deprecated
//    public static final byte DEFENSIV = DEFENSIVE;
//    @Deprecated
//    public static final byte ZUR_MITTE = TOWARDS_MIDDLE;
//    @Deprecated
//    public static final byte NACH_AUSSEN = TOWARDS_WING;
//    @Deprecated
//    public static final byte ZUS_STUERMER = OLD_EXTRA_FORWARD;
//    @Deprecated
//    public static final byte ZUS_MITTELFELD = OLD_EXTRA_MIDFIELD;
//    @Deprecated
//    public static final byte ZUS_INNENV = OLD_EXTRA_DEFENDER;
//    @Deprecated
//    public static final byte ZUS_STUERMER_DEF = OLD_EXTRA_DEFENSIVE_FORWARD;
//
//    @Deprecated
//    public static final byte TORWART = KEEPER;
//    @Deprecated
//    public static final byte INNENVERTEIDIGER = CENTRAL_DEFENDER;
//    @Deprecated
//    public static final byte INNENVERTEIDIGER_OFF = CENTRAL_DEFENDER_OFF;
//    @Deprecated
//    public static final byte INNENVERTEIDIGER_AUS = CENTRAL_DEFENDER_TOWING;
//    @Deprecated
//    public static final byte AUSSENVERTEIDIGER = BACK;
//    @Deprecated
//    public static final byte AUSSENVERTEIDIGER_OFF = BACK_OFF;
//    @Deprecated
//    public static final byte AUSSENVERTEIDIGER_IN = BACK_TOMID;
//    @Deprecated
//    public static final byte AUSSENVERTEIDIGER_DEF = BACK_DEF;
//    @Deprecated
//    public static final byte MITTELFELD = MIDFIELDER;
//    @Deprecated
//    public static final byte MITTELFELD_OFF = MIDFIELDER_OFF;
//    @Deprecated
//    public static final byte MITTELFELD_DEF = MIDFIELDER_DEF;
//    @Deprecated
//    public static final byte MITTELFELD_AUS = MIDFIELDER_TOWING;
//    @Deprecated
//    public static final byte FLUEGELSPIEL = WINGER;
//    @Deprecated
//    public static final byte FLUEGELSPIEL_OFF = WINGER_OFF;
//    @Deprecated
//    public static final byte FLUEGELSPIEL_DEF = WINGER_DEF;
//    @Deprecated
//    public static final byte FLUEGELSPIEL_IN = WINGER_TOMID;
//    @Deprecated
//    public static final byte STURM = FORWARD;
//    @Deprecated
//    public static final byte STURM_DEF = FORWARD_DEF;
//    @Deprecated
//    public static final byte AUSGEWECHSELT1 = SUBSTITUTED1;
//    @Deprecated
//    public static final byte AUSGEWECHSELT2 = SUBSTITUTED2;
//    @Deprecated
//    public static final byte AUSGEWECHSELT3 = SUBSTITUTED3;
//    @Deprecated
//    public static final byte STURM_AUS = FORWARD_TOWING;
//    @Deprecated
//    public static final byte POS_ZUS_STUERMER = 22;
//    @Deprecated
//    public static final byte POS_ZUS_STUERMER_DEF = 25;
//    @Deprecated
//    public static final byte POS_ZUS_MITTELFELD = 23;
//    @Deprecated
//    public static final byte POS_ZUS_INNENV = 24;
//    @Deprecated
//    public static final byte UNBESTIMMT = UNKNOWN;
//    @Deprecated
//    public static final byte TRAINER = COACH;
    
}
