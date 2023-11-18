package core.model.player

import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream

/**
 *
 * MatchRoleID according to CHPP documentation.
 *
 * @author akaSolace
 */
interface IMatchRoleID {
    companion object {
        //MatchRoleID and MatchBehaviourID according to CHPP documentation
        // Used for sending Lineup
        // ===========  MatchRoleID ================
        // https://www93.hattrick.org/Community/CHPP/NewDocs/DataTypes.aspx#matchRoleID
        const val keeper = 100 // Keeper
        const val rightBack = 101 // Right WB
        const val rightCentralDefender = 102 // Right central defender
        const val middleCentralDefender = 103 //	Middle central defender
        const val leftCentralDefender = 104 // Left central defender
        const val leftBack = 105 // Left WB
        const val rightWinger = 106 // Right winger
        const val rightInnerMidfield = 107 // Right inner midfield
        const val centralInnerMidfield = 108 // 	Middle inner midfield
        const val leftInnerMidfield = 109 // Left inner midfield
        const val leftWinger = 110 // Left winger
        const val rightForward = 111 // Right forward
        const val centralForward = 112 // Middle forward
        const val leftForward = 113 //Left forward

        val aFieldMatchRoleID = IntStream.rangeClosed(100, 113).boxed().collect(Collectors.toList())
        val aOutfieldMatchRoleID = IntStream.rangeClosed(101, 113).boxed().collect(Collectors.toList())

        // Subs
        // used in match lineup file
        const val substitutionKeeper = 114
        const val substitutionDefender = 115
        const val substitutionInnerMidfield = 116
        const val substitutionWinger = 117
        const val substitutionForward = 118

        // used in match order
        const val substGK1 = 200 // 	Substitution (Keeper)
        const val substCD1 = 201 // Substitution (Central defender)
        const val substWB1 = 202 // Substitution (WB)
        const val substIM1 = 203 // Substitution (Inner midfielder)
        const val substFW1 = 204 // 	Substitution (Forward)
        const val substWI1 = 205 // 	Substitution (Winger)
        const val substXT1 = 206 // 	Substitution (Extra)

        val aSubstitutesMatchRoleID = IntStream.rangeClosed(200, 206).boxed().collect(Collectors.toList())
        const val substGK2 = 207 // 	Backup (Keeper)
        const val substCD2 = 208 // Backup (Central defender)
        const val substWB2 = 209 // Backup (WB)
        const val substIM2 = 210 // Backup (Inner midfielder)
        const val substFW2 = 211 // 	Backup (Forward)
        const val substWI2 = 212 // 	Backup (Winger)
        const val substXT2 = 213 // 	Backup (Extra)
        val aBackupssMatchRoleID = IntStream.rangeClosed(207, 213).boxed().collect(Collectors.toList())

        val aSubsAndBackupssMatchRoleID = IntStream.rangeClosed(200, 213).boxed().collect(Collectors.toList())
        val aFieldMSubsAndBackupMatchRoleID =
            Stream.of(aFieldMatchRoleID, aSubsAndBackupssMatchRoleID).flatMap { obj: List<Int> -> obj.stream() }
                .collect(Collectors.toList())
        val aFieldAndSubsMatchRoleID =
            Stream.of(aFieldMatchRoleID, aSubstitutesMatchRoleID).flatMap { obj: List<Int> -> obj.stream() }
                .collect(Collectors.toList())

        // SetPieces Taker and Captain
        const val setPieces = 17
        const val captain = 18

        // replaced Players
        const val FirstPlayerReplaced = 19 // Replaced Player #1
        const val SecondPlayerReplaced = 20 // Replaced Player #2
        const val ThirdPlayerReplaced = 21 // Replaced Player #3

        // penalty Takers
        const val penaltyTaker1 = 22
        const val penaltyTaker2 = 23
        const val penaltyTaker3 = 24
        const val penaltyTaker4 = 25
        const val penaltyTaker5 = 26
        const val penaltyTaker6 = 27
        const val penaltyTaker7 = 28
        const val penaltyTaker8 = 29
        const val penaltyTaker9 = 30
        const val penaltyTaker10 = 31
        const val penaltyTaker11 = 32

        //    /** ab welccher PositionsID gehört Pos zur Reserve Bank */
        //
        //First id of the reserves
        const val startReserves = substitutionKeeper

        // Start of player list is not a good idea to hard code at 0 either
        const val startLineup = keeper

        //
        // ===========  MatchBehaviourID ================
        // https://www93.hattrick.org/Community/CHPP/NewDocs/DataTypes.aspx#matchBehaviourID
        const val NORMAL: Byte = 0
        const val OFFENSIVE: Byte = 1
        const val DEFENSIVE: Byte = 2
        const val TOWARDS_MIDDLE: Byte = 3
        const val TOWARDS_WING: Byte = 4
        const val OLD_EXTRA_FORWARD: Byte = 5
        const val OLD_EXTRA_MIDFIELD: Byte = 6
        const val OLD_EXTRA_DEFENDER: Byte = 7
        const val OLD_EXTRA_DEFENSIVE_FORWARD: Byte = 8

        //Constants to identify the 22 possible combination of position and Behaviour
        const val NUM_POSITIONS: Byte = 22
        const val KEEPER: Byte = 0
        const val CENTRAL_DEFENDER: Byte = 1
        const val CENTRAL_DEFENDER_OFF: Byte = 2
        const val CENTRAL_DEFENDER_TOWING: Byte = 3
        const val BACK: Byte = 4
        const val BACK_OFF: Byte = 5
        const val BACK_TOMID: Byte = 6
        const val BACK_DEF: Byte = 7
        const val MIDFIELDER: Byte = 8
        const val MIDFIELDER_OFF: Byte = 9
        const val MIDFIELDER_DEF: Byte = 10
        const val MIDFIELDER_TOWING: Byte = 11
        const val WINGER: Byte = 12
        const val WINGER_OFF: Byte = 13
        const val WINGER_DEF: Byte = 14
        const val WINGER_TOMID: Byte = 15
        const val FORWARD: Byte = 16
        const val FORWARD_DEF: Byte = 17
        const val SUBSTITUTED1: Byte = 18
        const val SUBSTITUTED2: Byte = 19
        const val SUBSTITUTED3: Byte = 20
        const val FORWARD_TOWING: Byte = 21
        const val UNKNOWN: Byte = -1
        const val EXTRA: Byte = 30
        const val FORWARD_DEF_TECH: Byte = 98
        const val COACH: Byte = 99
        const val UNSELECTABLE: Byte = 101
        val aPositionBehaviours: List<Int> =
            mutableListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 21)

        // The old role IDs used for mapping old data to new format
        val oldKeeper = listOf(1)
        val oldRightBack = listOf(2)
        val oldRightCentralDefender = listOf(3)
        val oldLeftCentralDefender = listOf(4)
        val oldLeftBack = listOf(5)
        val oldRightWinger = listOf(6)
        val oldRightInnerMidfielder = listOf(7)
        val oldLeftInnerMidfielder = listOf(8)
        val oldLeftWinger = listOf(9)
        val oldRightForward = listOf(10)
        val oldLeftForward = listOf(11)
        val oldSubstKeeper = listOf(12, 114)
        val oldSubstDefender = listOf(13, 115)
        val oldSubstMidfielder = listOf(14, 116)
        val oldSubstWinger = listOf(15, 117)
        val oldSubstForward = listOf(16, 118)
    }
}
