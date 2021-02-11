package module.youth;

import module.training.Skills;

import java.util.HashMap;

public class YouthSkillsInfo extends HashMap<Skills.HTSkillID, YouthSkillInfo> {

    /**
     * Determine if skills are keeper skills
     *
     * @return
     *  true, if keeper max > 4
     *  false, if max of pm,ps,wi or sc > 4
     *  null, if decision is not possible
     */
    public Boolean areKeeperSkills() {
        if (size() == 0) return null;
        for (var skill : this.values()) {
            if (skill.getMax() != null && skill.getMax() >= 5 ||
                    skill.getCurrentLevel() != null && skill.getCurrentLevel() >= 5 ||
                    skill.getCurrentValue() >= 5) {
                var skillId = skill.getSkillID();
                if (skillId == Skills.HTSkillID.Keeper) return true;
                else if (skillId == Skills.HTSkillID.Winger ||
                        skillId == Skills.HTSkillID.Playmaker ||
                        skillId == Skills.HTSkillID.Passing ||
                        skillId == Skills.HTSkillID.Scorer) return false;
            }
        }
        return null;
    }

    public void setPlayerMaxSkills(boolean isKeeper) {
        if ( size() == 0 ) return;
        for (var skill : this.values()) {
            if (skill.getMax() == null) {
                switch (skill.getSkillID()) {
                    case Winger:
                    case Playmaker:
                    case Passing:
                    case Scorer:
                        if (isKeeper) {
                            skill.setMax(4);
                        }
                        break;
                    case Keeper:
                        if (!isKeeper) {
                            skill.setMax(4);
                        }
                }
            }
        }
    }

    public double getTrainedSkillSum() {
        double ret = 0;
        for ( var skill: this.values()){
            ret += skill.getCurrentValue()-skill.getStartValue();
        }
        return ret;
    }
}
