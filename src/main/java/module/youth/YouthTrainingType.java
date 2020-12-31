package module.youth;

import core.model.HOVerwaltung;
import core.model.player.MatchRoleID;
import core.training.WeeklyTrainingType;
import core.training.type.*;

import java.util.ArrayList;
import java.util.List;

import static core.constants.TrainingType.*;

public enum YouthTrainingType {

/*
    public static final int SET_PIECES 			= 2;
    public static final int DEFENDING 			= 3;
    public static final int SCORING 			= 4;
    public static final int CROSSING_WINGER 	= 5;
    public static final int SHOOTING 			= 6;
    public static final int SHORT_PASSES 		= 7;
    public static final int PLAYMAKING 			= 8;
    public static final int GOALKEEPING 		= 9;
    public static final int THROUGH_PASSES 		= 10;
    public static final int DEF_POSITIONS 		= 11;
    public static final int WING_ATTACKS 		= 12;*/

    IndividualTraining(1),
    SetPieces(SET_PIECES),
    Defending(DEFENDING),
    Scoring(SCORING),
    Winger(CROSSING_WINGER),
    Shooting(SHOOTING),
    Passing(SHORT_PASSES),
    Playmaking(PLAYMAKING),
    Goalkeeping(GOALKEEPING),
    ThroughPassing(THROUGH_PASSES),
    DefencePositions(DEF_POSITIONS),
    WingAttacking(WING_ATTACKS);

    static WeeklyTrainingType[] trainingTypes = {
            null,
            IndividualWeeklyTraining.instance(),
            SetPiecesWeeklyTraining.instance(),
            DefendingWeeklyTraining.instance(),
            ScoringWeeklyTraining.instance(),
            CrossingWeeklyTraining.instance(),
            ShootingWeeklyTraining.instance(),
            ShortPassesWeeklyTraining.instance(),
            PlaymakingWeeklyTraining.instance(),
            GoalkeepingWeeklyTraining.instance(),
            ThroughPassesWeeklyTraining.instance(),
            DefensivePositionsWeeklyTraining.instance(),
            WingAttacksWeeklyTraining.instance()
    };

    private final int value;

    YouthTrainingType(int value) {
        this.value = value;
    }

    public static String StringValueOf(YouthTrainingType value) {
        var hov = HOVerwaltung.instance();
        if ( value == null) return hov.getLanguageString("undefined");
        return hov.getLanguageString(value.toString());
    }

    public int getValue() {
        return value;
    }

    public static YouthTrainingType valueOf(Integer id) {
        if ( id != null) {
            for (YouthTrainingType youthTrainingType : YouthTrainingType.values()) {
                if (youthTrainingType.getValue() == id) {
                    return youthTrainingType;
                }
            }
        }
        return null;
    }

    /**
     * Gets a list of match lineup positions of priority
     * 0 - bonus training positions
     * 1 - full training positions
     * 2 - partly training positions
     * 3 - osmosis training positions
     * @return list of position arrays
     */
    public List<int[]> getTrainedPositions() {
        var ret = new ArrayList<int[]>();
        var wt = trainingTypes[value];
        ret.add(wt.getTrainingSkillBonusPositions());
        ret.add(wt.getTrainingSkillPositions());
        ret.add(wt.getTrainingSkillPartlyTrainingPositions());
        ret.add(wt.getTrainingSkillOsmosisTrainingPositions());
        return ret;
    }

    /**
     * calculate training effect per minute
     *
     * @param skillId skill id
     * @param currentValue current skill value
     * @param posPrio 0 - bonus training
     *                1 - full skill training
     *                1 - partly skill training
     *                2 - osmosis training
     * @param ageYears age of the player
     * @return skill increment (training effect)
     */
    public double calcSkillIncrementPerMinute(int skillId, int currentValue, int posPrio, int ageYears) {
        var wt = trainingTypes[this.value];
        if ( wt.getPrimaryTrainingSkill() != skillId && wt.getSecondaryTrainingSkill() != skillId ) return 0;
        return switch (posPrio) {
            case 0 -> wt.getBonusYouthTrainingPerMinute(skillId,currentValue, ageYears);
            case 1 -> wt.getFullYouthTrainingPerMinute(skillId,currentValue, ageYears);
            case 2 -> wt.getPartlyYouthTrainingPerMinute(skillId,currentValue, ageYears);
            case 3 -> wt.getOsmosisYouthTrainingPerMinute(skillId,currentValue, ageYears);
            default -> 0;
        };
    }

    public double calcSkillIncrementPerMinute(int skillId, int currentValue, MatchRoleID.Sector sector, int ageYears) {
        var wt = trainingTypes[this.value];
        return ((IndividualWeeklyTraining)wt).calcSkillIncrementPerMinute(skillId,currentValue,sector,ageYears);
    }
}