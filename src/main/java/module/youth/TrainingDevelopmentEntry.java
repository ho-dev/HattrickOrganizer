package module.youth;

import core.model.match.MatchLineupTeam;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class TrainingDevelopmentEntry {
    private YouthPlayer player;
    private YouthTraining training;
    private HashMap<Integer, YouthPlayer.SkillInfo> skills;

    public TrainingDevelopmentEntry(YouthPlayer player, YouthTraining training) {
        this.training = training;
        this.player = player;
    }

    public void setSkills(HashMap<Integer, YouthPlayer.SkillInfo> startSkills) {
        this.skills = startSkills;
    }

    public void setSkillConstraints(Map<Integer, YouthPlayer.SkillInfo> skillConstraints) {
        if (skillConstraints != null) {
            for (var constraint : skillConstraints.entrySet()) {
                var skill = skills.get(constraint.getKey());
                skill.setCurrentLevel(constraint.getValue().getCurrentLevel());
                skill.setMax(constraint.getValue().getMax());
            }
        }
    }

    public Map<Integer, YouthPlayer.SkillInfo> calcSkills(Map<Integer, YouthPlayer.SkillInfo> startSkills, Map<Integer, YouthPlayer.SkillInfo> skillConstraints, MatchLineupTeam team) {
        if ( this.skills == null){
            this.skills = new HashMap<>();
        }
        for (var skill : startSkills.entrySet()) {
            this.skills.put(skill.getKey(), training.calcSkill(skill.getValue(), player, team));
        }
        setSkillConstraints(skillConstraints);
        return this.skills;
    }

    public Timestamp getMatchDate() {
        return this.training.getMatchDate();
    }

    public int getMatchId() {
        return this.training.getMatchId();
    }

    public String getMatchName() {
        return this.training.getHomeTeamName() + "-" + this.training.getGuestTeamName();
    }

    public YouthTraining getTraining() {
        return this.training;
    }
}
