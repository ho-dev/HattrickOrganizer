package module.youth;

import core.constants.player.PlayerSkill;
import java.util.Objects;

public class YouthSkillInfo {

    public static int UsefulTrainingThreshold = 4;

    /**
     * Skill Id
     */
    private final PlayerSkill skillID;

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
     * Range of possible start values
     * if no scout info of start level is given the range will be limited by the first occurrence of a current value
     */
    private SkillRange startValueRange = new SkillRange();

    /**
     * Range of possible current values
     */
    private final SkillRange currentValueRange = new SkillRange();

    /**
     * Range of possible maximum values
     */
    private final SkillRange maxValueRange = new SkillRange();

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
     * True if skill is one of the skills mentioned by the scout or found maximum is greater than one of the scout infos
     * False if skill maximum is not one of the top 3 maximums
     * null otherwise (not known)
     */
    private Boolean isTop3Skill;

    /**
     * Constructor, only setting the skill id
     * @param id HTSkillId
     */
    public YouthSkillInfo(PlayerSkill id) {
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

    /**
     * Set the skill's start value.
     * AdjustValues is called to consider constraints by known skill levels downloaded from hattrick.
     * @param value New start value. This may be changed, if the value conflicts with skill constraints.
     */
    public void setStartValue(double value) {
        this.startValue = value;
        adjustValues();
    }

    /**
     * Checks and adjusts start and current value of the skill to respect constraints:
     * startlevel<=startValue<startLevel+1
     * currentLevel<=currentValue<currentLevel+1
     * 0<=startValue<=currentValue<=max+1 or 8.3
     * Additionally possible ranges of start value (startValueRange) and currentValue (currentValueRange)
     * are examined.
     */
    private void adjustValues() {
        if (max != null) {
            if (currentValue > max + 1) {
                currentValue = max + 0.99;
            }
            currentValueRange.lessThan(max + 1);
            startValueRange.lessThan(max + 1);
            maxValueRange.between(currentValue, max + 1);
        }

        if (currentLevel != null) {
            if (currentValue < currentLevel) {
                this.currentValue = currentLevel;
            } else if (currentValue > currentLevel + 1) {
                this.currentValue = currentLevel + 0.99;
            }

            startValueRange.lessThan(currentLevel + 1);
            currentValueRange.between(currentLevel, currentLevel + 1);
            maxValueRange.greaterEqual(currentValue);
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
            startValueRange.between(startLevel, startLevel + 1);
            currentValueRange.greaterEqual(startLevel);
            maxValueRange.greaterEqual(currentValue);
        } else if (currentValue < startValue) {
            if ( currentValue < 0) currentValue = 0;
            startValue = currentValue;
        } else if (startValue < 0) {
            startValue = 0;
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

    /**
     * Skill maximum level is reached, but there might be some subskill points left for future training
     * @return true if current level is known and equal to maximum level
     */
    public boolean isMaxLevelReached() {
        return this.getCurrentLevel() != null  &&
                ( this.getCurrentLevel()==8 ||
                        this.isMaxAvailable() && Objects.equals(this.getCurrentLevel(), this.getMax()));
    }

    public boolean isMaxReached() {
        return isMaxReached;
    }

    /**
     * each skill of a youth player has a maximum value which is not known from the beginning,
     * except for one skill mentioned by the scout.
     * if this maximum value is reached, no further training of this skill is useful.
     * if this maximum is less than UsefulTrainingThreshold, i think training of this skill is not useful either.
     * @return  true, further training is useful
     *          false, no further training of this skill is useful
     */
    public boolean isTrainingUsefull(){
        return !isMaxReached() && (!this.isMaxAvailable() || this.getMax()>= UsefulTrainingThreshold);
    }

    public void setMaxReached(boolean maxReached) {
        isMaxReached = maxReached;
    }

    public Integer getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(Integer startLevel) {
        this.startLevel = startLevel;
        if (startLevel != null) this.startValueRange = new SkillRange(startLevel);
        adjustValues();
    }

    public SkillRange getStartValueRange() {
        return this.startValueRange;
    }

    public SkillRange getCurrentValueRange(){
        return this.currentValueRange;
    }

    public void setStartValueRange(SkillRange range) {
        this.startValueRange = range;
    }

    public PlayerSkill getSkillID() {
        return skillID;
    }

    public void addStartValue(double val) {
        this.startValue += val;
    }

    public void setPotential17Value(double val) {
        this.potential17Value = val;
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

    /**
     * get the minimum potential value. This is the maximum value if it is known
     * or the current level value, if maximum is unknown
     * @return int, 0 if neither current level nor maximum is known
     */
    public int getMinimumPotential() {
        return (int)maxValueRange.greaterEqual;
    }

    public int getMaximumPotential() {
        if (Math.round(maxValueRange.lessThan) - maxValueRange.lessThan < 0.001) return (int)maxValueRange.lessThan-1;
        return (int)maxValueRange.lessThan;
    }

    /**
     * Calculate the minimum contribution of the skill's overall skills level contribution
     * The exact value would be given by the average of the skill's start and max values.
     * Since the max value is not always known, it's value is given by the maximum of the calculated
     * 17 years potential, the maximum skill level, if known from trainer or scout messages or the currently
     * estimated skill level.
     * @return Double value of the minimum overall skills contribution
     */
    public double calculateMinimumOverallSkillsLevelContribution() {
        var sum = 0.;
        var potential = this.getPotential17Value();
        if (potential != null) {
            sum = potential;
        }
        if (this.isMaxAvailable() && this.max > sum) {
            sum = this.max;
        }
        if (this.currentValue > sum) {
            // minimum max value if nothing else is known
            sum = this.currentValue;
        }
        sum += this.startValue;
        return sum / 2.;
    }

    /**
     * Set the upper limit of skill maximum (potential).
     * The value is not for sure and could be reduced by future trainer reports
     * @param isKeeper Boolean
     *                  true, players skills are keeper skills
     *                  false, player skills are infield skills
     *                  null, unknown
     * @param minTop3Max int limit of potential
     */
    public void setMaxLimit(Boolean isKeeper, int minTop3Max) {
        if (this.max != null && this.max <= minTop3Max) return;  // nothing to do
        if (minTop3Max < 5) {
            maxValueRange.lessThan(minTop3Max + 1);
        } else if (isKeeper != null) {
            if (isKeeper) {
                switch (this.skillID) {
                    case KEEPER, DEFENDING, SETPIECES -> maxValueRange.lessThan(minTop3Max + 1);
                }
            } else {
                switch (this.skillID) {
                    case DEFENDING, WINGER, PLAYMAKING, PASSING, SCORING, SETPIECES -> maxValueRange.lessThan(minTop3Max + 1);
                }
            }
        }
        this.currentValueRange.lessThan(minTop3Max + 1);
        this.startValueRange.lessThan(minTop3Max + 1);
    }

    public void setMaxLevelLimit(int i) {
        this.maxValueRange.lessThan(i+1);
    }

    // Skill Range class
    public static class SkillRange {
        private double greaterEqual;
        private double lessThan;

        public SkillRange() {
            this(null);
        }

        public SkillRange(Integer level) {
            if (level != null) {
                greaterEqual = level;
                lessThan = level + 1;
            } else {
                greaterEqual = 0;
                lessThan = 8.3;
            }
        }

        public SkillRange(double min, double max) {
            this.lessThan = max;
            this.greaterEqual = min;
        }

        public double getGreaterEqual() {
            return greaterEqual;
        }

        public void setGreaterEqual(double greaterEqual) {
            this.greaterEqual = greaterEqual;
            if (this.lessThan < greaterEqual) this.lessThan = greaterEqual;
        }

        public double getLessThan() {
            return lessThan;
        }

        public void setLessThan(double lessThan) {
            this.lessThan = lessThan;
            if (this.greaterEqual >= lessThan) this.greaterEqual = lessThan;
        }

        public void lessThan(double limit) {
            if (this.lessThan > limit) {
                setLessThan(limit);
            }
        }

        public void between(double min, double max) {
            setGreaterEqual(min);
            setLessThan(max);
        }

        public void greaterEqual(double min) {
            if ( this.greaterEqual <min){
                setGreaterEqual(min);
            }
        }
    }

    public static String getSkillName(PlayerSkill skillId) {
        return switch (skillId)    {
            case KEEPER -> "Keeper";
            case DEFENDING -> "Defender";
            case WINGER -> "Winger";
            case PLAYMAKING -> "Playmaker";
            case SCORING -> "Scorer";
            case PASSING -> "Passing";
            case STAMINA -> "Stamina";
            case FORM -> "Form";
            case SETPIECES -> "SetPieces";
            case EXPERIENCE -> "Experience";
            case LEADERSHIP -> "Leadership";
            case LOYALTY -> "Loyalty";
        };
    }

}