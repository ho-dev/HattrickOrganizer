package module.youth;

import core.model.match.MatchLineupTeam;
import core.model.player.Player;
import module.training.Skills;

import java.sql.Timestamp;
import java.util.Map;

public class TrainingDevelopmentEntry {
    private YouthPlayer player;
    private YouthTraining training;
    private SkillsInfo skills;

    public TrainingDevelopmentEntry(YouthPlayer player, YouthTraining training) {
        this.training = training;
        this.player = player;
    }

    public void setSkills(SkillsInfo startSkills) {
        this.skills = startSkills;
    }

    public void setSkillConstraints(YouthPlayer player, SkillsInfo skillConstraints) {
        if (skillConstraints != null) {
            for (var constraint : skillConstraints.values()) {
                var skill = this.skills.get(constraint.getSkillID());
                var oldVal = skill.getCurrentValue();
                skill.setCurrentLevel(constraint.getCurrentLevel());
                skill.setMax(constraint.getMax());
                skill.setMaxReached(constraint.isMaxReached());
                if ( skill.getStartValue() == 0){
                    var adjustment = skill.getCurrentValue() - oldVal;
                    if ( adjustment > 0 ){
                        player.adjustSkill(skill.getSkillID(), adjustment);
                        skill.setStartValue(adjustment);
                    }
                }
            }
        }
    }

    public SkillsInfo calcSkills(SkillsInfo startSkills, SkillsInfo skillConstraints, MatchLineupTeam team) {
        if ( this.skills == null){
            this.skills = new SkillsInfo();
        }
        for (var skill : startSkills.values()) {
            this.skills.put(skill.getSkillID(), training.calcSkill(skill, player, team));
        }
        setSkillConstraints(player, skillConstraints);
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

    public String getPlayerAge() {
        return Player.getAgeWithDaysAsString(this.player.getAgeYears(),this.player.getAgeDays(),this.getMatchDate().getTime());
    }

    public String getSkillValue(Skills.HTSkillID skillID) {
        var val = this.skills.get(skillID);
        if ( val != null ) return String.format("%,.2f", val.getCurrentValue());
        return "";
    }

    public String getTrainingType(YouthTraining.Priority prio) {
        return YouthTrainingType.StringValueOf(this.training.getTraining(prio));
    }

    public String getPlayerSector() {
        return this.training.getPlayerTrainedSectors(this.player.getId());
    }

    public SkillsInfo getSkills() {
        return this.skills;
    }

    public int getPlayerAgeYears() {
        return this.player.getAgeYears();
    }
}
