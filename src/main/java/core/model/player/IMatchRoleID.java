package core.model.player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * MatchRoleID according to CHPP documentation.
 * 
 * @author akaSolace
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
    List<Integer> aOutfieldMatchRoleID = IntStream.rangeClosed(101, 113).boxed().collect(Collectors.toList());

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
    List<Integer> aSubsAndBackupssMatchRoleID = IntStream.rangeClosed(200, 213).boxed().collect(Collectors.toList());
    List<Integer> aFieldMSubsAndBackupMatchRoleID = Stream.of(aFieldMatchRoleID, aSubsAndBackupssMatchRoleID).flatMap(Collection::stream).collect(Collectors.toList());
    List<Integer> aFieldAndSubsMatchRoleID = Stream.of(aFieldMatchRoleID, aSubstitutesMatchRoleID).flatMap(Collection::stream).collect(Collectors.toList());

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
    byte FORWARD_DEF_TECH = 98;
    byte COACH = 99;
    byte UNSELECTABLE = 101;
    List<Integer> aPositionBehaviours = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 21);

    
    // The old role IDs used for mapping old data to new format
     List<Integer> oldKeeper = List.of(1);
     List<Integer>oldRightBack = List.of(2);
     List<Integer>oldRightCentralDefender = List.of(3);
     List<Integer>oldLeftCentralDefender = List.of(4);
     List<Integer>oldLeftBack = List.of(5);
     List<Integer>oldRightWinger = List.of(6);
     List<Integer>oldRightInnerMidfielder = List.of(7);
     List<Integer>oldLeftInnerMidfielder = List.of(8);
     List<Integer>oldLeftWinger = List.of(9);
     List<Integer>oldRightForward = List.of(10);
     List<Integer>oldLeftForward = List.of(11);
     List<Integer>oldSubstKeeper = List.of(12, 114);
     List<Integer>oldSubstDefender = List.of(13, 115);
     List<Integer>oldSubstMidfielder = List.of(14, 116);
     List<Integer>oldSubstWinger = List.of(15, 117);
     List<Integer>oldSubstForward = List.of(16, 118);
    
}
