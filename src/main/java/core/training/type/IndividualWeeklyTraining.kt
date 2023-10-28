package core.training.type;

import core.model.StaffMember;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.training.WeeklyTrainingType;
import module.training.Skills;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndividualWeeklyTraining extends WeeklyTrainingType {
    private static WeeklyTrainingType m_ciInstance;

    public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
            m_ciInstance = new IndividualWeeklyTraining();
        }
        return m_ciInstance;
    }

    private static Map<MatchRoleID.Sector, Map<Integer, Double>> probabilities;
    static {
        probabilities = new HashMap<>();
        probabilities.put(MatchRoleID.Sector.Goal, new HashMap<>() {{
            put(Skills.HTSkillID.Keeper.getValue(), 0.4);
            put(Skills.HTSkillID.Defender.getValue(), 0.4);
            put(Skills.HTSkillID.SetPieces.getValue(), 0.2);
        }});
        probabilities.put(MatchRoleID.Sector.Back, new HashMap<>() {{
            put(Skills.HTSkillID.Defender.getValue(), 0.3);
            put(Skills.HTSkillID.Winger.getValue(), 0.22);
            put(Skills.HTSkillID.Playmaker.getValue(), 0.2);
            put(Skills.HTSkillID.Passing.getValue(), 0.15);
            put(Skills.HTSkillID.SetPieces.getValue(), 0.1);
        }});
        probabilities.put(MatchRoleID.Sector.CentralDefence, new HashMap<>() {{
            put(Skills.HTSkillID.Defender.getValue(), 0.35);
            put(Skills.HTSkillID.Playmaker.getValue(), 0.3);
            put(Skills.HTSkillID.Passing.getValue(), 0.25);
            put(Skills.HTSkillID.SetPieces.getValue(), 0.1);
        }});
        probabilities.put(MatchRoleID.Sector.Wing, new HashMap<>() {{
            put(Skills.HTSkillID.Defender.getValue(), 0.16);
            put(Skills.HTSkillID.Playmaker.getValue(), 0.22);
            put(Skills.HTSkillID.Passing.getValue(), 0.19);
            put(Skills.HTSkillID.SetPieces.getValue(), 0.11);
            put(Skills.HTSkillID.Winger.getValue(), 0.32);
        }});
        probabilities.put(MatchRoleID.Sector.InnerMidfield, new HashMap<>() {{
            put(Skills.HTSkillID.Defender.getValue(), 0.28);
            put(Skills.HTSkillID.Playmaker.getValue(), 0.36);
            put(Skills.HTSkillID.Passing.getValue(), 0.24);
            put(Skills.HTSkillID.SetPieces.getValue(), 0.12);
        }});
        probabilities.put(MatchRoleID.Sector.Forward, new HashMap<>() {{
            put(Skills.HTSkillID.Scorer.getValue(), 0.38);
            put(Skills.HTSkillID.Winger.getValue(), 0.29);
            put(Skills.HTSkillID.Passing.getValue(), 0.22);
            put(Skills.HTSkillID.SetPieces.getValue(), 0.11);
        }});
    }

    public double calcSkillIncrementPerMinute(int skillId, int currentValue, MatchRoleID.Sector sector, int ageYears) {
        var probs = probabilities.get(sector);
        if ( probs != null){
            var ret = probs.get(skillId);
            if ( ret != null){
                var skill = Skills.HTSkillID.valueOf(skillId);
                switch (skill){
                    case Keeper: return 0.5 * ret * GoalkeepingWeeklyTraining.instance().getFullYouthTrainingPerMinute(Skills.HTSkillID.Keeper.getValue(), currentValue, ageYears);
                    case Defender: return 0.9 * ret * DefendingWeeklyTraining.instance().getFullYouthTrainingPerMinute(Skills.HTSkillID.Defender.getValue(), currentValue, ageYears);
                    case Winger: return 0.6 * ret * CrossingWeeklyTraining.instance().getFullYouthTrainingPerMinute(Skills.HTSkillID.Winger.getValue(), currentValue, ageYears);
                    case Playmaker: return 0.8 * ret * PlaymakingWeeklyTraining.instance().getFullYouthTrainingPerMinute((Skills.HTSkillID.Playmaker.getValue()), currentValue, ageYears);
                    case Scorer: return 0.6 * ret * ScoringWeeklyTraining.instance().getFullYouthTrainingPerMinute(Skills.HTSkillID.Scorer.getValue(), currentValue, ageYears);
                    case Passing: return 0.9 * ret * ShortPassesWeeklyTraining.instance().getFullYouthTrainingPerMinute(Skills.HTSkillID.Passing.getValue(), currentValue, ageYears);
                    case SetPieces: return 0.9 * ret * SetPiecesWeeklyTraining.instance().getFullYouthTrainingPerMinute(Skills.HTSkillID.SetPieces.getValue(), currentValue, ageYears);
                }
            }
        }
        return 0;
    }
}
