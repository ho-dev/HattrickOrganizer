package module.youth;

import core.model.match.MatchLineupTeam;
import core.model.match.MatchType;
import core.model.player.Player;
import module.training.Skills;

import java.sql.Timestamp;

public class YouthTrainingDevelopmentEntry {
    private YouthPlayer player;
    private YouthTraining training;
    private YouthSkillsInfo skills;

    public YouthTrainingDevelopmentEntry(YouthPlayer player, YouthTraining training) {
        this.training = training;
        this.player = player;
    }

    public void setSkills(YouthSkillsInfo startSkills) {
        this.skills = startSkills;
    }

    public void setSkillConstraints(YouthPlayer player, YouthSkillsInfo skillConstraints) {
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

    public YouthSkillsInfo calcSkills(YouthSkillsInfo startSkills, YouthSkillsInfo skillConstraints, MatchLineupTeam team) {
        if ( this.skills == null){
            this.skills = new YouthSkillsInfo();
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

    public MatchType getMatchType() {
        return this.training.getMatchType();
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

    public YouthSkillsInfo getSkills() {
        return this.skills;
    }

    public int getPlayerAgeYears() {
        return this.player.getAgeYears();
    }

    public double getRating() {
        return this.training.getRating(this.player.getId());
    }
}
