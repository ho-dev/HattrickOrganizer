package module.youth;

import core.model.HOVerwaltung;
import core.model.match.MatchLineupTeam;
import core.model.match.MatchType;
import core.model.player.Player;
import core.model.player.Specialty;
import module.training.Skills;

import java.sql.Timestamp;
import java.util.stream.Collectors;

public class YouthTrainingDevelopmentEntry {
    private YouthPlayer player;
    private YouthTraining training;
    private YouthSkillsInfo skills;
    private Specialty specialty;

    public YouthTrainingDevelopmentEntry(YouthPlayer player, YouthTraining training) {
        this.training = training;
        this.player = player;
        this.specialty = findSpecialty();
        if ( specialty != null){
            this.player.setSpecialty(specialty);
        }
    }

    private Specialty findSpecialty() {
        var matchDetails = training.getMatchDetails();
        var highlights = matchDetails.getHighlights().stream()
                .filter(h->h.getPlayerId()==player.getId()||h.getAssistingPlayerId()==player.getId())
                .collect(Collectors.toList());
        for ( var highlight : highlights){
            if (highlight.getPlayerId()==player.getId()) {
                switch (highlight.getMatchEventID()) {
                    case SE_NO_GOAL_CORNER_HEAD_SPECIALIST:
                    case SE_GOAL_CORNER_HEAD_SPECIALIST:
                        return Specialty.Head;

                    case SE_GOAL_UNPREDICTABLE_OWN_GOAL:
                    case SE_GOAL_UNPREDICTABLE_SCORES_ON_HIS_OWN:
                    case SE_GOAL_UNPREDICTABLE_SPECIAL_ACTION:
                    case SE_GOAL_UNPREDICTABLE_LONG_PASS:
                    case SE_GOAL_UNPREDICTABLE_MISTAKE:
                    case SE_NO_GOAL_UNPREDICTABLE_ALMOST_SCORES:
                    case SE_NO_GOAL_UNPREDICTABLE_LONG_PASS:
                    case SE_NO_GOAL_UNPREDICTABLE_MISTAKE:
                    case SE_NO_GOAL_UNPREDICTABLE_SPECIAL_ACTION:
                    case SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST:
                        return Specialty.Unpredictable;

                    case SE_NO_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE:
                    case SE_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE:
                    case SE_POWERFUL_DEFENSIVE_INNER_PRESSES_CHANCE:
                    case SE_POWERFUL_SUFFERS_FROM_SUN:
                    case SE_POWERFUL_THRIVES_IN_RAIN:
                        return Specialty.Powerful;

                    case SE_QUICK_LOSES_IN_RAIN:
                    case SE_QUICK_LOSES_IN_SUN:
                    case SE_QUICK_RUSHES_PASSES_AND_RECEIVER_SCORES:
                    case SE_QUICK_RUSHES_PASSES_BUT_RECEIVER_FAILS:
                    case SE_QUICK_RUSHES_STOPPED_BY_QUICK_DEFENDER:
                    case SE_QUICK_SCORES_AFTER_RUSH:
                    case SE_SPEEDY_MISSES_AFTER_RUSH:
                        return Specialty.Quick;

                    case SE_SUPPORT_PLAYER_BOOST_FAILED:
                    case SE_SUPPORT_PLAYER_BOOST_FAILED_AND_ORGANIZATION_DROPPED:
                    case SE_SUPPORT_PLAYER_BOOST_SUCCEEDED:
                        return Specialty.Support;

                    case SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER:
                    case SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER_NO_GOAL:
                    case SE_TECHNICAL_SUFFERS_FROM_RAIN:
                    case SE_TECHNICAL_THRIVES_IN_SUN:
                        return  Specialty.Technical;
                }
            }
            else {
                // Analyse assistant
                switch (highlight.getMatchEventID()) {
                    case SE_QUICK_RUSHES_STOPPED_BY_QUICK_DEFENDER:
                        return Specialty.Quick;

                    case SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER:
                    case SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER_NO_GOAL:
                    case SE_WINGER_TO_HEAD_SPEC_SCORES:
                        return Specialty.Head;

                }
            }
        }
        return null;
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

    public String getSpecialtyString() {
        if ( this.specialty != null ) return HOVerwaltung.instance().getLanguageString("ls.player.speciality." + this.specialty.toString().toLowerCase());
        return "";
    }
}
