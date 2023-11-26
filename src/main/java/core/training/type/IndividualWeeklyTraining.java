package core.training.type;

import core.constants.player.PlayerSkill;
import core.model.player.MatchRoleID;
import core.training.WeeklyTrainingType;
import java.util.HashMap;
import java.util.Map;

public class IndividualWeeklyTraining extends WeeklyTrainingType {
    private static WeeklyTrainingType m_ciInstance;

    public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
            m_ciInstance = new IndividualWeeklyTraining();
        }
        return m_ciInstance;
    }

    private static final Map<MatchRoleID.Sector, Map<PlayerSkill, Double>> probabilities;
    static {
        probabilities = new HashMap<>();
        probabilities.put(MatchRoleID.Sector.Goal, new HashMap<>() {{
            put(PlayerSkill.KEEPER, 0.4);
            put(PlayerSkill.DEFENDING, 0.4);
            put(PlayerSkill.SETPIECES, 0.2);
        }});
        probabilities.put(MatchRoleID.Sector.Back, new HashMap<>() {{
            put(PlayerSkill.DEFENDING, 0.3);
            put(PlayerSkill.WINGER, 0.22);
            put(PlayerSkill.PLAYMAKING, 0.2);
            put(PlayerSkill.PASSING, 0.15);
            put(PlayerSkill.SETPIECES, 0.1);
        }});
        probabilities.put(MatchRoleID.Sector.CentralDefence, new HashMap<>() {{
            put(PlayerSkill.DEFENDING, 0.35);
            put(PlayerSkill.PLAYMAKING, 0.3);
            put(PlayerSkill.PASSING, 0.25);
            put(PlayerSkill.SETPIECES, 0.1);
        }});
        probabilities.put(MatchRoleID.Sector.Wing, new HashMap<>() {{
            put(PlayerSkill.DEFENDING, 0.16);
            put(PlayerSkill.PLAYMAKING, 0.22);
            put(PlayerSkill.PASSING, 0.19);
            put(PlayerSkill.SETPIECES, 0.11);
            put(PlayerSkill.WINGER, 0.32);
        }});
        probabilities.put(MatchRoleID.Sector.InnerMidfield, new HashMap<>() {{
            put(PlayerSkill.DEFENDING, 0.28);
            put(PlayerSkill.PLAYMAKING, 0.36);
            put(PlayerSkill.PASSING, 0.24);
            put(PlayerSkill.SETPIECES, 0.12);
        }});
        probabilities.put(MatchRoleID.Sector.Forward, new HashMap<>() {{
            put(PlayerSkill.SCORING, 0.38);
            put(PlayerSkill.WINGER, 0.29);
            put(PlayerSkill.PASSING, 0.22);
            put(PlayerSkill.SETPIECES, 0.11);
        }});
    }

    public double calcSkillIncrementPerMinute(PlayerSkill skillId, int currentValue, MatchRoleID.Sector sector, int ageYears) {
        var probs = probabilities.get(sector);
        if ( probs != null){
            var ret = probs.get(skillId);
            if ( ret != null){
                switch (skillId){
                    case KEEPER: return 0.5 * ret * GoalkeepingWeeklyTraining.instance().getFullYouthTrainingPerMinute(skillId, currentValue, ageYears);
                    case DEFENDING: return 0.9 * ret * DefendingWeeklyTraining.instance().getFullYouthTrainingPerMinute(skillId, currentValue, ageYears);
                    case WINGER: return 0.6 * ret * CrossingWeeklyTraining.instance().getFullYouthTrainingPerMinute(skillId, currentValue, ageYears);
                    case PLAYMAKING: return 0.8 * ret * PlaymakingWeeklyTraining.instance().getFullYouthTrainingPerMinute(skillId, currentValue, ageYears);
                    case SCORING: return 0.6 * ret * ScoringWeeklyTraining.instance().getFullYouthTrainingPerMinute(skillId, currentValue, ageYears);
                    case PASSING: return 0.9 * ret * ShortPassesWeeklyTraining.instance().getFullYouthTrainingPerMinute(skillId, currentValue, ageYears);
                    case SETPIECES: return 0.9 * ret * SetPiecesWeeklyTraining.instance().getFullYouthTrainingPerMinute(skillId, currentValue, ageYears);
                }
            }
        }
        return 0;
    }
}
