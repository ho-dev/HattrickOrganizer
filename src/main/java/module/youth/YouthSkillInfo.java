package module.youth;

import module.training.Skills;

public class YouthSkillInfo {

    /**
     * Skill Id
     */
    private Skills.HTSkillID skillID;

    /**
     * Value at scouting date, edited by the user (user's estimation)
     */
    private double startValue;

    /**
     * Calculated value based on trainings and edited start value
     */
    private double currentValue;

    /**
     * Skill level at the current download
     * null as long as not known
     */
    private Integer currentLevel;

    /**
     * Skill level at the scouting date
     * null as long as not known
     */
    private Integer startLevel;

    /**
     * Maximum reachable skill level (potential)
     * null as long as not known
     */
    private Integer max;

    /**
     * Indicates if the skill cant be trained anymore (false if current or max is not available).
     */
    private boolean isMaxReached;

    /**
     * Reachable skill value if player will get optimal training
     * if player is older than 17 the training development status of age 17 is set.
     * Calculated value is not stored in database
     */
    private Double potential17Value;

    /**
     * Scout mentions up to 2 skill info. Both of them belong to the top 3 skills with highest maximum.
     * Information is used to restrict other skill maxima.
     *
     * True if skill is one of the skills mentioned by the scout or found maximum is greater than one of the scout infos
     * False if skill maximum is not one of the top 3 maximums
     * null otherwise (not known)
     */
    private Boolean isTop3Skill;

    public YouthSkillInfo(Skills.HTSkillID id) {
        this.skillID = id;
    }

    public boolean isCurrentLevelAvailable() {
        return currentLevel != null;
    }

    public boolean isStartLevelAvailable() {
        return startLevel != null;
    }

    public boolean isMaxAvailable() {
        return max != null;
    }

    public double getStartValue() {
        return this.startValue;
    }

    public void setStartValue(double value) {
        this.startValue = value;
        adjustValues();
    }

    private void adjustValues() {
        if (max != null) {
            if (currentValue > max + 1) {
                currentValue = max + 0.99;
            }
        }
        if (currentLevel != null) {
            if (currentValue < currentLevel) {
                this.currentValue = currentLevel;
            } else if (currentValue > currentLevel + 1) {
                this.currentValue = currentLevel + 0.99;
            }
        }

        if (startLevel != null) {
            if (startValue < startLevel) {
                this.startValue = startLevel;
            } else if (startValue > startLevel + 1) {
                this.startValue = startLevel + 0.99;
            }

            if (currentValue < startValue) {
                currentValue = startValue;
            }
        } else if (currentValue < startValue) {
            startValue = currentValue;
        }
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double value) {
        this.currentValue = value;
        adjustValues();
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
        adjustValues();
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
        adjustValues();
    }

    public boolean isMaxReached() {
        return isMaxReached;
    }

    public void setMaxReached(boolean maxReached) {
        isMaxReached = maxReached;
    }

    public Integer getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(Integer startLevel) {
        this.startLevel = startLevel;
        adjustValues();
    }

    public Skills.HTSkillID getSkillID() {
        return skillID;
    }

    public void addStartValue(double val) {
        this.startValue+=val;
    }

    public void setPotential17Value(double val) {
        this.potential17Value=val;
    }

    public Double getPotential17Value() {
        return potential17Value;
    }

    public void setIsTop3(Boolean b) {
        this.isTop3Skill = b;
    }

    public Boolean isTop3() {
        return this.isTop3Skill;
    }

    public int getMinimumPotential() {
        if ( this.max != null) return this.max;
        if ( this.currentLevel != null) return this.currentLevel;
        return 0;
    }
}

