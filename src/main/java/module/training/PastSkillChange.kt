package module.training;

import core.model.player.ISkillChange;
import core.util.HODateTime;

import java.util.Date;


/**
 * Base Object for the Skillup table
 */
public class PastSkillChange implements ISkillChange {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Skillup Date */
    private HODateTime date;

    /** Hattrick Season */
    private int htSeason;

    /** Hattrick Week */
    private int htWeek;

    /** Training Type 0 old, 1 min, 2 max */
    private int trainType;

    /** type of skill changed */
    private int type;

    /** Value of skill */
    private double value;

    private String age;
    private int change=1;

    //~ Methods ------------------------------------------------------------------------------------
    public void setDate(HODateTime date) {
        this.date = date;
    }

    public HODateTime getDate() {
        return date;
    }

    public void setHtSeason(int i) {
        htSeason = i;
    }

    public int getHtSeason() {
        return htSeason;
    }

    public void setHtWeek(int i) {
        htWeek = i;
    }

    public int getHtWeek() {
        return htWeek;
    }

    public void setTrainType(int i) {
        trainType = i;
    }

    public int getTrainType() {
        return trainType;
    }

    /**
     * Set training type
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Get Training type
     *
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * Set the new value of the skill
     *
     * @param newValue
     */
    public void setValue(double newValue) {
        value = newValue;
    }

    /**
     * Set the new value of the skill
     *
     * @return value
     */
    public double getValue() { return value; }

    @Override
    public int getChange() {
        return change;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public String toString() {
        StringBuilder buffer = new StringBuilder();

        if ( change>0) {
            buffer.append("Skillup["); //$NON-NLS-1$
        }
        else {
            buffer.append("Skilldrop["); //$NON-NLS-1$
        }
        buffer.append(", type = ").append(type); //$NON-NLS-1$
        buffer.append(", value = ").append(value); //$NON-NLS-1$
        buffer.append(", htSeason = ").append(htSeason); //$NON-NLS-1$
        buffer.append(", htWeek = ").append(htWeek); //$NON-NLS-1$
        buffer.append(", trainType = ").append(trainType); //$NON-NLS-1$
        buffer.append("]"); //$NON-NLS-1$

        return buffer.toString();
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String getAge() {
        return age;
    }

    public void setChange(int skillup) {
        change = skillup;
    }
}
