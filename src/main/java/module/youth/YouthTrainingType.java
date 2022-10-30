package module.youth;

import core.model.HOVerwaltung;
import core.model.player.MatchRoleID;
import core.training.WeeklyTrainingType;
import core.training.type.*;
import module.training.Skills;
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

    static final WeeklyTrainingType[] trainingTypes = {
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
        if ( value == null) return hov.getLanguageString("ls.youth.trainingtype.undefined");
        return hov.getLanguageString("ls.youth.trainingtype."+value._toString());
    }

    private String _toString() {
        return super.toString();
    }

    @Override
    public String toString() {
        return StringValueOf(this);
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

    public static Integer getValue(YouthTrainingType type){
        if ( type != null){
            return type.getValue();
        }
        return null;
    }

    public List<List<MatchRoleID.Sector>> getTrainedSectors(){
        var ret = new ArrayList<List<MatchRoleID.Sector>>();
        var wt = trainingTypes[value];
        ret.add(wt.getBonusTrainingSectors());
        ret.add(wt.getFullTrainingSectors());
        ret.add(wt.getPartlyTrainingSectors());
        ret.add(wt.getOsmosisTrainingSectors());
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
    public double calcSkillIncrementPerMinute(Skills.HTSkillID skillId, int currentValue, int posPrio, int ageYears) {
        var playerskill = skillId.convertToPlayerSkill();
        var wt = trainingTypes[this.value];
        return switch (posPrio) {
            case 0 -> wt.getBonusYouthTrainingPerMinute(playerskill,currentValue, ageYears);
            case 1 -> wt.getFullYouthTrainingPerMinute(playerskill,currentValue, ageYears);
            case 2 -> wt.getPartlyYouthTrainingPerMinute(playerskill,currentValue, ageYears);
            case 3 -> wt.getOsmosisYouthTrainingPerMinute(playerskill,currentValue, ageYears);
            default -> 0;
        };
    }

    public double calcSkillIncrementPerMinute(Skills.HTSkillID skillId, int currentValue, MatchRoleID.Sector sector, int ageYears) {
        var wt = trainingTypes[this.value];
        return ((IndividualWeeklyTraining)wt).calcSkillIncrementPerMinute(skillId.convertToPlayerSkill(),currentValue,sector,ageYears);
    }
}
